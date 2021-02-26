package com.arogut.homex.data.model;

import com.arogut.homex.data.validation.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementMessage {

    @Past
    private long measuredTime;

    private long receivedTime = System.currentTimeMillis();

    @NotEmpty
    private List<@Valid MeasurementValue> data;
}
