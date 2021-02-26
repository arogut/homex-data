package com.arogut.homex.data.model;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contract {

    @NotNull
    private Set<@Valid Measurement> measurements;

    @NotNull
    private Set<@Valid Command> commands;
}
