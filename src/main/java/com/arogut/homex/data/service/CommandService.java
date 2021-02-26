package com.arogut.homex.data.service;

import com.arogut.homex.data.model.Command;
import com.arogut.homex.data.model.CommandMessage;
import com.arogut.homex.data.model.CommandParam;
import com.arogut.homex.data.model.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommandService {

    public void sendCommand(Device device, CommandMessage commandMessage) {
        getCommandDefinition(device, commandMessage.getName())
                .subscribe(command -> executeCommand(device, commandMessage, command.getEndpoint()));
    }

    private void executeCommand(Device device, CommandMessage commandMessage, String endpoint) {
        WebClient.create(prepareUrl(device, endpoint))
                .post()
                .body(commandMessage.getParams(), new ParameterizedTypeReference<Set<CommandParam>>() {
                })
                .exchange();
    }

    private String prepareUrl(Device device, String endpoint) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(device.getHost())
                .port(device.getPort())
                .path(endpoint).build()
                .toString();
    }

    private Mono<Command> getCommandDefinition(Device device, String commandName) {
        return Mono.justOrEmpty(device.getContract()
                .getCommands().stream()
                .filter(command -> command.getName().equals(commandName))
                .findAny());
    }
}
