package com.arogut.homex.data.mapper;

import com.arogut.homex.data.dao.DeviceEntity;
import com.arogut.homex.data.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(target = ".", source = "contract")
    DeviceEntity toEntity(Device device);

    @Mapping(target = "contract.measurements", ignore = true)
    @Mapping(target = "contract.commands", ignore = true)
    Device toDevice(DeviceEntity entity);

    @Mapping(target = "contract", source = ".")
    Device toDeviceWithChildren(DeviceEntity entity);
}
