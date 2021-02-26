package com.arogut.homex.data.api;

import com.arogut.homex.data.model.Device;
import com.arogut.homex.data.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Links;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.function.Supplier;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public Mono<EntityModel<Device>> add(@Valid @RequestBody Device device) {
        return Mono.zip(
                deviceService.add(device),
                linkTo(methodOn(DeviceController.class)
                        .getDevices())
                        .withSelfRel()
                        .toMono())
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2())));

    }

    @GetMapping
    public Mono<CollectionModel<EntityModel<Device>>> getDevices() {
        return Mono.zip(
                deviceService.getAll()
                        .flatMap(d -> getDeviceWithLinks(() -> Mono.justOrEmpty(d), d.getId()))
                        .collectList(),
                linkTo(methodOn(DeviceController.class)
                        .getDevices())
                        .withSelfRel()
                        .toMono())
                .map(o -> CollectionModel.of(o.getT1(), Links.of(o.getT2())));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<EntityModel<Device>>> getDeviceById(@PathVariable String id) {
        return getDeviceWithLinks(() -> deviceService.getById(id), id)
                .map(d -> ResponseEntity.ok().body(d))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Mono<EntityModel<Device>> getDeviceWithLinks(Supplier<Mono<Device>> deviceSupplier, String id) {
        return Mono.zip(
                deviceSupplier.get(),
                linkTo(methodOn(DeviceController.class)
                        .getDeviceById(id))
                        .withSelfRel()
                        .toMono(),
                linkTo(methodOn(DeviceController.class)
                        .getDevices())
                        .withRel(IanaLinkRelations.ITEM)
                        .toMono(),
                linkTo(methodOn(DeviceMessageController.class)
                        .handleMeasurement(id, null))
                        .withRel("measurements")
                        .toMono(),
                linkTo(methodOn(DeviceMessageController.class)
                        .handleCommand(id, null))
                        .withRel("commands")
                        .toMono())
                .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3(), o.getT4(), o.getT5())));
    }
}
