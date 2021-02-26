package com.arogut.homex.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Command {

    @NotEmpty
    private String name;

    @NotEmpty
    private String endpoint;

    private Set<@Valid CommandParam> params;
}
