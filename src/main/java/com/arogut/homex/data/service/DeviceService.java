package com.arogut.homex.data.service;

import com.arogut.homex.data.dao.DeviceEntity;
import com.arogut.homex.data.dao.DeviceRepository;
import com.arogut.homex.data.dao.JpaChild;
import com.arogut.homex.data.mapper.DeviceMapper;
import com.arogut.homex.data.model.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    private final DeviceMapper deviceMapper;

    public Flux<Device> getAll() {
        return Flux.fromIterable(deviceRepository.findAll())
                .map(deviceMapper::toDevice);
    }

    public Mono<Device> getById(String id) {
        return Mono.justOrEmpty(deviceRepository.findById(id))
                .map(deviceMapper::toDeviceWithChildren);
    }

    public Mono<Boolean> existsById(String id) {
        return Mono.just(deviceRepository.existsById(id));
    }

    public Mono<Device> add(Device device) {

        return Mono.just(deviceRepository.findByMacAddressAndName(device.getMacAddress(), device.getName())
                .map(deviceMapper::toDevice)
                .orElseGet(() -> create(device)));
    }

    private Device create(Device device) {
        DeviceEntity deviceEntity = prepareSave(deviceMapper.toEntity(device));
        return deviceMapper.toDevice(deviceRepository.save(deviceEntity));
    }

    private DeviceEntity prepareSave(final DeviceEntity deviceEntity) {

        setUpParent(deviceEntity, deviceEntity.getMeasurements());
        setUpParent(deviceEntity, deviceEntity.getCommands());
        if (deviceEntity.getCommands() != null) {
            deviceEntity.getCommands()
                    .forEach(commandEntity -> setUpParent(commandEntity, commandEntity.getParams()));
        }
        return deviceEntity;
    }

    private <T> void setUpParent(final T parent, final Set<? extends JpaChild<T>> deviceChildren) {
        if (null != deviceChildren) {
            deviceChildren.forEach(measurement -> {
                measurement.setId(null);
                measurement.setParent(parent);
            });
        }
    }
}
