package com.arogut.homex.data.dao;

import com.arogut.homex.data.model.ValueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "parent")
public class MeasurementEntity implements JpaChild<DeviceEntity> {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "MEASUREMENT_ID")
    private String id;

    @Column
    @NotNull
    private String name;

    @Enumerated
    @NotNull
    private ValueType type;

    @ManyToOne
    @JoinColumn(name = "DEVICE_ID", referencedColumnName = "DEVICE_ID", nullable = false)
    private DeviceEntity parent;
}
