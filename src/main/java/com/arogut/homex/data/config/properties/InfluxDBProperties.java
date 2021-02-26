package com.arogut.homex.data.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "influx")
@Component
@Getter
@Setter
public class InfluxDBProperties {

    private String url;
    private String user;
    private String password;
    private String database;
}
