package org.itech.labSampleTracker.integration.oedatarepo;

/**
 * Résultat d'une tentative de synchronisation d'un échantillon avec oedatarepo.
 *
 * Pilote la machine d'état du compteur de tentatives
 * (cf. {@code OeSyncTrackingService.recordOutcome}) :
 * <ul>
 *   <li>{@link #UPDATED}   : statut/dates modifiés et persistés → compteur remis à 0.</li>
 *   <li>{@link #PENDING}   : labno présent côté oedatarepo mais analyse encore en
 *       cours (lstrackerStatus null) → compteur remis à 0 (auto-guérison).</li>
 *   <li>{@link #NOT_FOUND} : labno absent d'oedatarepo (404) → compteur +1 ; seul
 *       cas qui mène à l'épuisement.</li>
 *   <li>{@link #ERROR}     : erreur transitoire (réseau / 5xx / auth) → compteur
 *       inchangé.</li>
 *   <li>{@link #SKIPPED}   : client inactif / labno vide → aucune écriture de suivi.</li>
 * </ul>
 */
public enum OeSyncOutcome {
    UPDATED,
    PENDING,
    NOT_FOUND,
    ERROR,
    SKIPPED
}
