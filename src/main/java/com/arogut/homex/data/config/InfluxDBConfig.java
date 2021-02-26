package com.arogut.homex.data.config;

import com.arogut.homex.data.config.properties.InfluxDBProperties;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {

    @Bean
    public InfluxDB influxDB(InfluxDBProperties properties) {
        return InfluxDBFactory
                .connect(properties.getUrl(), properties.getUser(), properties.getPassword())
                .setDatabase(properties.getDatabase());
    }
}
