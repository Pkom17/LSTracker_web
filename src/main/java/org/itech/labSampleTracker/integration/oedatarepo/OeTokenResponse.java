package org.itech.labSampleTracker.integration.oedatarepo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Réponse de POST /auth/token côté oedatarepo : { accessToken, expiresIn }.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OeTokenResponse {
    private String accessToken;
    /** Durée de validité du token en secondes. */
    private long expiresIn;
}
