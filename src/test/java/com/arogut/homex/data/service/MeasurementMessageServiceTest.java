package com.arogut.homex.data.service;

import com.arogut.homex.data.model.MeasurementMessage;
import com.arogut.homex.data.model.MeasurementValue;
import org.assertj.core.api.Assertions;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
class MeasurementMessageServiceTest {

    @Mock
    private InfluxDB influxDB;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private DeviceMessageService deviceMessageService;

    @Test
    void shouldAcceptDeviceMessage() {
        MeasurementMessage msg = MeasurementMessage.builder()
                .measuredTime(Instant.now().toEpochMilli())
                .receivedTime(Instant.now().toEpochMilli())
                .data(List.of(MeasurementValue.builder()
                        .name("temp")
                        .value(25L).build()
                ))
                .build();

        deviceMessageService.handleMeasurement("dummy", msg);

        ArgumentCaptor<BatchPoints> pointsCaptor = ArgumentCaptor.forClass(BatchPoints.class);
        Mockito.verify(influxDB).write(pointsCaptor.capture());
        BatchPoints bp = BatchPoints.builder()
                .tag("deviceId", "dummy")
                .point(
                        Point.measurement("measurement")
                                .time(msg.getMeasuredTime(), TimeUnit.MILLISECONDS)
                                .addField("temp", 25L)
                                .build()
                )
                .build();
        Assertions.assertThat(pointsCaptor.getValue()).isEqualTo(bp);
    }
}

