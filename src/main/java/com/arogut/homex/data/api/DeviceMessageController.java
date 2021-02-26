package com.arogut.homex.data.api;

import com.arogut.homex.data.model.CommandMessage;
import com.arogut.homex.data.model.MeasurementMessage;
import com.arogut.homex.data.service.DeviceMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceMessageController {

    private final DeviceMessageService deviceMessageService;

    @PostMapping("/{id}/measurement")
    public Mono<ResponseEntity<Void>> handleMeasurement(@PathVariable String id, @Valid @RequestBody MeasurementMessage message) {
        deviceMessageService.handleMeasurement(id, message);
        return Mono.just(ResponseEntity.accepted().build());
    }

    @PostMapping("/{id}/command")
    public Mono<ResponseEntity<Void>> handleCommand(@PathVariable String id, @Valid @RequestBody CommandMessage message) {
        deviceMessageService.handleCommand(id, message);
        return Mono.just(ResponseEntity.accepted().build());
    }
}
