package com.arogut.homex.data.service;

import com.arogut.homex.data.model.CommandMessage;
import com.arogut.homex.data.model.Device;
import com.arogut.homex.data.model.MeasurementMessage;
import com.arogut.homex.data.model.MeasurementValue;
import lombok.RequiredArgsConstructor;
import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeviceMessageService {

    private final DeviceService deviceService;
    private final CommandService commandService;
    private final InfluxDB influxDB;

    public void handleMeasurement(String deviceId, MeasurementMessage measurementMessage) {
        persist(deviceId, measurementMessage.getData(), measurementMessage.getMeasuredTime());
    }

    public void handleCommand(String deviceId, CommandMessage commandMessage) {
        deviceService.getById(deviceId)
                .doOnNext(device -> sendCommand(device, commandMessage));
    }

    private void persist(String deviceId, List<MeasurementValue> measurements, long measuredTime) {
        BatchPoints.Builder batchPoints = BatchPoints.builder()
                .tag("deviceId", deviceId);
        measurements.forEach(m -> batchPoints.point(Point.measurement("measurement")
                .addField(m.getName(), m.getValue())
                .time(measuredTime, TimeUnit.MILLISECONDS)
                .build()));

        influxDB.write(batchPoints.build());
    }

    private void sendCommand(Device device, CommandMessage commandMessage) {
        commandService.sendCommand(device, commandMessage);
    }
}
