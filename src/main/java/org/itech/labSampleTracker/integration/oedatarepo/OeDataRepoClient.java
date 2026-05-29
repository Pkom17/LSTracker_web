package org.itech.labSampleTracker.integration.oedatarepo;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Client HTTP vers l'API d'interopérabilité oedatarepo (OpenELIS consolidé).
 *
 * Authentification : compte technique → POST /auth/token (Basic Auth) renvoie
 * un JWT, mis en cache et réutilisé jusqu'à expiration (renouvelé avec une
 * marge de sécurité). Sur 401, le token est invalidé et l'appel retenté une
 * fois avec un token frais.
 *
 * Désactivable via lstracker.oedatarepo.enabled=false (défaut). Si l'URL ou
 * les credentials ne sont pas configurés, le client reste inactif et
 * getAnalysisStatus renvoie Optional.empty() sans appel réseau.
 */
@Component
public class OeDataRepoClient {

    private static final Logger log = LoggerFactory.getLogger(OeDataRepoClient.class);

    /** Marge avant expiration pour renouveler le token de manière proactive. */
    private static final long EXPIRY_MARGIN_SECONDS = 60;

    private final boolean enabled;
    private final String baseUrl;
    private final String username;
    private final String password;
    private final RestClient restClient;

    // Cache de token (thread-safe via synchronized sur les accès).
    private String cachedToken;
    private Instant tokenExpiry = Instant.EPOCH;

    public OeDataRepoClient(
            @Value("${lstracker.oedatarepo.enabled:false}") boolean enabled,
            @Value("${lstracker.oedatarepo.base-url:}") String baseUrl,
            @Value("${lstracker.oedatarepo.username:}") String username,
            @Value("${lstracker.oedatarepo.password:}") String password) {
        this.enabled = enabled;
        this.baseUrl = baseUrl == null ? "" : baseUrl.trim();
        this.username = username;
        this.password = password;
        // baseUrl appliqué par appel (uri absolue) plutôt qu'au builder : évite
        // de fournir une URL vide au builder quand le client n'est pas configuré.
        this.restClient = RestClient.builder().build();
    }

    /** Le client est-il configuré et activé ? */
    public boolean isReady() {
        return enabled && notBlank(baseUrl) && notBlank(username) && notBlank(password);
    }

    /**
     * Récupère le statut consolidé d'analyse pour un labno, en distinguant les
     * cas (OK / 404 / erreur / client inactif).
     *
     * @return un {@link OeAnalysisResult} non nul :
     *         OK (corps présent), NOT_FOUND (404), ERROR (réseau/5xx/auth) ou
     *         SKIPPED (client non configuré ou labno vide).
     */
    public OeAnalysisResult fetchAnalysis(String labno) {
        if (!isReady()) {
            log.debug("OeDataRepoClient inactif (enabled/url/credentials non configurés)");
            return OeAnalysisResult.skipped();
        }
        if (labno == null || labno.trim().isEmpty()) {
            return OeAnalysisResult.skipped();
        }
        String trimmed = labno.trim();

        // Un labno qui contient des caractères impossibles dans un segment
        // d'URL (slash, antislash, ?, #, %) ne pourra jamais être résolu via
        // l'API path-param d'oedatarepo (Tomcat rejette les slash encodés en
        // 400). On le classe NOT_FOUND (épuisable) sans appel réseau, plutôt
        // qu'ERROR transitoire qui serait re-tenté en boucle.
        if (NON_QUERYABLE.matcher(trimmed).find()) {
            log.debug("labno '{}' non interrogeable (caractère interdit dans l'URL) → NOT_FOUND", trimmed);
            return OeAnalysisResult.notFound();
        }

        try {
            return doGet(trimmed, true);
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            log.debug("labno {} inconnu côté oedatarepo (404)", trimmed);
            return OeAnalysisResult.notFound();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // 4xx autre que 404/401 (ex. 400 sur format de labno invalide) :
            // requête non résoluble → NOT_FOUND (épuisable), pas ERROR.
            log.debug("labno {} → {} côté oedatarepo, classé NOT_FOUND", trimmed, e.getStatusCode());
            return OeAnalysisResult.notFound();
        } catch (Exception e) {
            // Erreur réseau / 5xx / parsing : transitoire → ERROR (non épuisable).
            log.error("Échec récupération statut oedatarepo pour labno={}: {}", trimmed, shortMessage(e));
            return OeAnalysisResult.error(shortMessage(e));
        }
    }

    /** Caractères qui rendent un labno inutilisable comme segment d'URL. */
    private static final java.util.regex.Pattern NON_QUERYABLE =
            java.util.regex.Pattern.compile("[/\\\\?#%]");

    /** Message d'erreur court (1re ligne, sans corps HTML), pour logs/JSON. */
    private static String shortMessage(Exception e) {
        String msg = e.getMessage();
        if (msg == null) {
            return e.getClass().getSimpleName();
        }
        int nl = msg.indexOf('\n');
        if (nl >= 0) {
            msg = msg.substring(0, nl);
        }
        return msg.length() > 200 ? msg.substring(0, 200) : msg;
    }

    /**
     * Variante historique : renvoie seulement le corps en cas de succès, vide
     * sinon. Conservée pour les appelants qui n'ont pas besoin de distinguer
     * 404 / erreur (délègue à {@link #fetchAnalysis}).
     */
    public Optional<OeAnalysisStatusResponse> getAnalysisStatus(String labno) {
        OeAnalysisResult r = fetchAnalysis(labno);
        return r.isOk() ? Optional.ofNullable(r.getBody()) : Optional.empty();
    }

    /**
     * Vérifie que l'on peut obtenir un token (panneau d'état). Léger : réutilise
     * le token en cache s'il est valide, sinon tente un /auth/token. Ne lève pas.
     */
    public boolean ping() {
        if (!isReady()) {
            return false;
        }
        try {
            return getToken() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private OeAnalysisResult doGet(String labno, boolean retryOn401) {
        String token = getToken();
        if (token == null) {
            log.warn("Impossible d'obtenir un token oedatarepo");
            return OeAnalysisResult.error("authentification impossible (token null)");
        }

        try {
            // NB : on ne fournit PAS de handler onStatus ici. Avec RestClient,
            // dès qu'un handler est enregistré pour un statut, l'exception par
            // défaut n'est plus levée — un 404 reviendrait alors en body=null et
            // serait confondu avec un succès "en cours". On laisse donc le
            // comportement par défaut lever HttpClientErrorException (404/401…),
            // attrapée par les catch typés ci-dessous.
            OeAnalysisStatusResponse body = restClient.get()
                    .uri(baseUrl + "/api/v1/order-analysis/{labno}", labno)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(OeAnalysisStatusResponse.class);
            return OeAnalysisResult.ok(body);
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            if (retryOn401) {
                log.info("Token oedatarepo expiré, renouvellement et nouvelle tentative");
                invalidateToken();
                return doGet(labno, false);
            }
            throw e;
        }
    }

    /** Retourne un token valide (depuis le cache ou en le renouvelant). */
    private synchronized String getToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiry)) {
            return cachedToken;
        }
        try {
            String basic = java.util.Base64.getEncoder()
                    .encodeToString((username + ":" + password).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            OeTokenResponse resp = restClient.post()
                    .uri(baseUrl + "/auth/token")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(OeTokenResponse.class);
            if (resp == null || resp.getAccessToken() == null) {
                return null;
            }
            cachedToken = resp.getAccessToken();
            long ttl = Math.max(0, resp.getExpiresIn() - EXPIRY_MARGIN_SECONDS);
            tokenExpiry = Instant.now().plusSeconds(ttl);
            return cachedToken;
        } catch (Exception e) {
            log.error("Échec authentification oedatarepo (/auth/token): {}", e.getMessage());
            return null;
        }
    }

    private synchronized void invalidateToken() {
        cachedToken = null;
        tokenExpiry = Instant.EPOCH;
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
