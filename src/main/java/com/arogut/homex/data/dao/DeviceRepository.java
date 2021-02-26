package com.arogut.homex.data.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceRepository extends CrudRepository<DeviceEntity, String> {

    @Query(value = "SELECT d FROM DeviceEntity d LEFT JOIN FETCH d.measurements LEFT JOIN FETCH d.commands WHERE d.macAddress = :macAddress AND d.name = :name")
    Optional<DeviceEntity> findByMacAddressAndName(@Param("macAddress")String macAddress, @Param("name")String name);

    @Query(value = "SELECT d FROM DeviceEntity d LEFT JOIN FETCH d.measurements LEFT JOIN FETCH d.commands WHERE d.id = :id")
    Optional<DeviceEntity> findById(@Param("id") String id);
}
