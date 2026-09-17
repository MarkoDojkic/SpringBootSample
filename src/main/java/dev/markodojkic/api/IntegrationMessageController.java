package dev.markodojkic.api;

import dev.markodojkic.model.IntegrationMessage;
import dev.markodojkic.model.IntegrationMessageRepository;
import java.util.List;
import dev.markodojkic.model.IntegrationMessageSpecifications;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class IntegrationMessageController {
    private final IntegrationMessageRepository repository;
    private final MessageMapper mapper;

    public IntegrationMessageController(IntegrationMessageRepository repository, MessageMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @GetMapping
    public List<IntegrationMessageDto> findAll() {
        return repository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<IntegrationMessageDto> search(
            @org.springframework.web.bind.annotation.RequestParam(name = "text") String text) {
        return repository.findAll(IntegrationMessageSpecifications.messageContains(text))
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IntegrationMessageDto create(@RequestBody CreateMessageRequest request) {
        return mapper.toDto(repository.save(new IntegrationMessage(
                request.message(), request.secretText(), request.secretDate(), request.secretBytes())));
    }

    @PutMapping("/{id}")
    public IntegrationMessageDto update(
            @PathVariable(name = "id") Long id, @RequestBody CreateMessageRequest request) {
        IntegrationMessage message = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + id));
        message.setMessage(request.message());
        return mapper.toDto(repository.save(message));
    }

    public record CreateMessageRequest(
            String message,
            String secretText,
            java.time.Instant secretDate,
            byte[] secretBytes) {
    }
}
