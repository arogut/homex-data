package com.arogut.homex.data.api;

import com.arogut.homex.data.dao.DeviceRepository;
import com.arogut.homex.data.model.MeasurementMessage;
import com.arogut.homex.data.model.MeasurementValue;
import com.arogut.homex.data.service.DeviceMessageService;
import org.influxdb.InfluxDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureWebTestClient
class MeasurementMessageControllerTest {

    @MockBean
    private DeviceMessageService deviceMessageService;

    @MockBean
    private DeviceRepository deviceRepository;

    @MockBean
    private InfluxDB influxDB;

    @Autowired
    private WebTestClient webClient;

    @Test
    @WithMockUser
    void shouldAcceptMessageAndReturn200OK() {
        var msg = createMessage();

        Mockito.when(deviceRepository.existsById("dummy")).thenReturn(true);

        webClient.post()
                .uri("/devices/dummy/measurement")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(msg), MeasurementMessage.class)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody().isEmpty();
    }

    @Test
    @WithMockUser
    void shouldNotAcceptInvalidMessageAndReturn400() {
        var msg = createMessage();
        msg.setData(null);

        webClient.post()
                .uri("/devices/dummy/measurement")
                .body(Mono.just(msg), MeasurementMessage.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private MeasurementMessage createMessage() {
        return MeasurementMessage.builder()
                .measuredTime(Instant.now().toEpochMilli())
                .receivedTime(Instant.now().toEpochMilli())
                .data(List.of(MeasurementValue.builder()
                        .name("temp")
                        .value(25).build()
                ))
                .build();
    }
}
