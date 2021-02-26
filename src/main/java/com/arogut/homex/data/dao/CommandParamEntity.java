package com.arogut.homex.data.dao;

import com.arogut.homex.data.model.ValueType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity(name = "commandParam")
@Getter
@Setter
@EqualsAndHashCode(exclude = "parent")
public class CommandParamEntity implements JpaChild<CommandEntity> {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @NotNull
    private String name;

    @Enumerated
    @NotNull
    private ValueType type;

    @ManyToOne
    @JoinColumn(name = "COMMAND_ID", referencedColumnName = "COMMAND_ID", nullable = false)
    private CommandEntity parent;
}
