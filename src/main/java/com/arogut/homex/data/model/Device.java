package com.arogut.homex.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    private String id;

    @NotEmpty
    private String macAddress;

    @NotEmpty
    private String name;

    @NotNull
    private DeviceType deviceType;

    private boolean isConnected;

    @NotEmpty
    private String host;

    @Positive
    @Max(99999)
    private int port;

    @NotNull
    private Contract contract;
}
