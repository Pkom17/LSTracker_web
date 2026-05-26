package org.itech.labSampleTracker.api.sync.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class SamplePushResponse {
    private List<MappedId> mapped;
    @NoArgsConstructor @AllArgsConstructor @Getter @Setter @Builder
    public static class MappedId {
        private String uuid;
        private String external_id; // id serveur renvoyé en string
    }
}
