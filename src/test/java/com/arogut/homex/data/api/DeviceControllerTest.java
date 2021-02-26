package com.arogut.homex.data.api;

import com.arogut.homex.data.auth.AuthType;
import com.arogut.homex.data.auth.JwtUtil;
import com.arogut.homex.data.model.*;
import com.arogut.homex.data.service.DeviceService;
import org.assertj.core.api.Assertions;
import org.influxdb.InfluxDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles(value = "test")
class DeviceControllerTest {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private WebTestClient webClient;

    @SpyBean
    private JwtUtil jwtUtil;

    @MockBean
    private InfluxDB influxDB;

    @Test
    void shouldReturnDeviceAnd200OK() {
        Device device = createDevice();
        Device saved = deviceService.add(device).block();
        String token = jwtUtil.generateToken(device.getId(), Map.of("role", AuthType.INTERNAL));

        webClient.get()
                .uri("/devices/" + saved.getId())
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Device.class)
                .value(d -> {
                    Assertions.assertThat(d.getId()).isNotEmpty();
                    Assertions.assertThat(d.getMacAddress()).isEqualTo("dummy");
                    Assertions.assertThat(d.getName()).isEqualTo("dummy");
                    Assertions.assertThat(d.getDeviceType()).isEqualTo(DeviceType.SOURCE);
                    Assertions.assertThat(d.getHost()).isEqualTo("localhost");
                    Assertions.assertThat(d.getPort()).isEqualTo(999);
                    Assertions.assertThat(d.getContract().getMeasurements().size()).isEqualTo(1);
                    Assertions.assertThat(d.getContract().getCommands().size()).isEqualTo(1);
                });
    }

    @Test
    void shouldReturn401WhenTokenCorrupted() {
        Device device = Device.builder()
                .id("test")
                .build();
        String token = jwtUtil.generateToken(device.getId(), Map.of("role", AuthType.INTERNAL));

        Mockito.when(jwtUtil.validateToken(token)).thenThrow(RuntimeException.class);

        webClient.get()
                .uri("/devices/test")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    void shouldReturn404NotFoundWhenNotExists() {
        webClient.get()
                .uri("/device/test12")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    void shouldReturnDevicesAnd200OK() {
        webClient.get()
                .uri("/devices")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser
    void shouldAcceptDeviceAndReturn200OK() {
        Device device = createDevice();

        webClient.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(device), Device.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Device.class)
                .value(d -> {
                    Assertions.assertThat(d.getId()).isNotEmpty();
                    Assertions.assertThat(d.getMacAddress()).isEqualTo("dummy");
                    Assertions.assertThat(d.getName()).isEqualTo("dummy");
                    Assertions.assertThat(d.getDeviceType()).isEqualTo(DeviceType.SOURCE);
                    Assertions.assertThat(d.getHost()).isEqualTo("localhost");
                    Assertions.assertThat(d.getPort()).isEqualTo(999);
                });
    }

    @Test
    @WithMockUser
    void shouldNotAcceptDeviceAndReturn400() {
        Device device = Device.builder()
                .isConnected(true)
                .deviceType(DeviceType.SOURCE)
                .host("localhost")
                .port(999)
                .build();

        webClient.post()
                .uri("/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(device), Device.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private Device createDevice() {
        return Device.builder()
                .id("dummy")
                .name("dummy")
                .isConnected(true)
                .macAddress("dummy")
                .deviceType(DeviceType.SOURCE)
                .host("localhost")
                .port(999)
                .contract(Contract.builder()
                        .measurements(Set.of(createMeasurement()))
                        .commands(Set.of(createCommand()))
                        .build())
                .build();
    }

    private Measurement createMeasurement() {
        return Measurement.builder()
                .name("temp")
                .type(ValueType.NUMBER)
                .build();
    }

    private Command createCommand() {
        return Command.builder()
                .name("turn-off")
                .endpoint("/turn-off")
                .params(Set.of(CommandParam.builder()
                        .name("time")
                        .type(ValueType.STRING)
                        .build()))
                .build();
    }
}
