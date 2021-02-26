package com.arogut.homex.data.service;

import com.arogut.homex.data.dao.DeviceEntity;
import com.arogut.homex.data.dao.DeviceRepository;
import com.arogut.homex.data.mapper.DeviceMapper;
import com.arogut.homex.data.model.Device;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    private final DeviceMapper mapper = Mappers.getMapper(DeviceMapper.class);

    @Mock
    private DeviceRepository deviceRepository;

    private DeviceService deviceService;

    @BeforeEach
    void setUp() {
        deviceService = new DeviceService(deviceRepository, mapper);
    }

    @Test
    void shouldReturnAllDevicesFromDB() {
        List<DeviceEntity> entities = Arrays.asList(DeviceEntity.builder().build(), DeviceEntity.builder().build());
        List<Device> devices = entities.stream()
                .map(mapper::toDevice)
                .collect(Collectors.toList());
        Mockito.when(deviceRepository.findAll()).thenReturn(entities);

        Assertions.assertThat(deviceService.getAll().collectList().block()).containsExactlyElementsOf(devices);
    }

    @Test
    void shouldReturnSingleDeviceById() {
        DeviceEntity entity = DeviceEntity.builder()
                .build();
        Device device = mapper.toDevice(entity);
        Mockito.when(deviceRepository.findById(Mockito.anyString())).thenReturn(Optional.ofNullable(entity));

        Assertions.assertThat(deviceService.getById("1").blockOptional()).isPresent();
        Assertions.assertThat(deviceService.getById("1").blockOptional()).contains(device);
    }

    @Test
    void shouldSuccessfullyAddNewDevice() {
        DeviceEntity entity = DeviceEntity.builder()
                .build();
        Device device = mapper.toDevice(entity);
        Mockito.when(deviceRepository.save(entity)).thenReturn(entity);

        Assertions.assertThat(deviceService.add(device).block()).isEqualTo(device);
    }

    @Test
    void shouldReturnDeviceIfSameMacAddress() {
        DeviceEntity entity = DeviceEntity.builder()
                .id("1")
                .macAddress("dummy")
                .name("dummy-name")
                .build();
        Device device = mapper.toDevice(entity);
        Mockito.when(deviceRepository.findByMacAddressAndName(entity.getMacAddress(), entity.getName())).thenReturn(Optional.of(entity));

        Assertions.assertThat(deviceService.add(device).block()).isEqualTo(device);
    }

    @Test
    void shouldReturnTrueWhenDeviceExists() {
        Mockito.when(deviceRepository.existsById(Mockito.anyString())).thenReturn(true);

        Assertions.assertThat(deviceService.existsById("1").block()).isTrue();
    }
}
