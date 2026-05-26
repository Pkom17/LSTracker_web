package org.itech.labSampleTracker.api.sync.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class SamplePushRequest {
    @Valid
    @Size(max = 500, message = "push batch size cannot exceed 500 items")
    private List<SampleUpsertItem> samples;
}
