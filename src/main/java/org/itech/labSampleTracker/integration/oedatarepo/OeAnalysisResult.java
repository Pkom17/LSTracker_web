package org.itech.labSampleTracker.integration.oedatarepo;

/**
 * Résultat brut d'un appel à oedatarepo pour un labno, renvoyé par
 * {@code OeDataRepoClient.fetchAnalysis}.
 *
 * Permet de distinguer les cas que l'ancienne API {@code Optional<...>}
 * confondait tous en "vide" :
 * <ul>
 *   <li>{@link Kind#OK}        : HTTP 200 avec corps (peut être "en cours").</li>
 *   <li>{@link Kind#NOT_FOUND} : HTTP 404, labno inconnu d'oedatarepo.</li>
 *   <li>{@link Kind#ERROR}     : erreur réseau / 5xx / authentification.</li>
 *   <li>{@link Kind#SKIPPED}   : client inactif ou labno vide (pas d'appel).</li>
 * </ul>
 *
 * Le classement final UPDATED / PENDING (parmi les cas {@code OK}) est décidé
 * par le service de synchronisation, pas par le client.
 */
public final class OeAnalysisResult {

    public enum Kind {
        OK,
        NOT_FOUND,
        ERROR,
        SKIPPED
    }

    private final Kind kind;
    private final OeAnalysisStatusResponse body;
    private final String errorMessage;

    private OeAnalysisResult(Kind kind, OeAnalysisStatusResponse body, String errorMessage) {
        this.kind = kind;
        this.body = body;
        this.errorMessage = errorMessage;
    }

    public static OeAnalysisResult ok(OeAnalysisStatusResponse body) {
        return new OeAnalysisResult(Kind.OK, body, null);
    }

    public static OeAnalysisResult notFound() {
        return new OeAnalysisResult(Kind.NOT_FOUND, null, null);
    }

    public static OeAnalysisResult error(String message) {
        return new OeAnalysisResult(Kind.ERROR, null, message);
    }

    public static OeAnalysisResult skipped() {
        return new OeAnalysisResult(Kind.SKIPPED, null, null);
    }

    public Kind getKind() {
        return kind;
    }

    /** Corps de réponse, non nul uniquement quand {@link #getKind()} == OK. */
    public OeAnalysisStatusResponse getBody() {
        return body;
    }

    /** Message d'erreur, non nul uniquement quand {@link #getKind()} == ERROR. */
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isOk() {
        return kind == Kind.OK;
    }
}
