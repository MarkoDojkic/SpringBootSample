package dev.markodojkic.api;

import dev.markodojkic.model.IntegrationMessage;
import dev.markodojkic.model.IntegrationMessageRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class IntegrationMessageController {
    private final IntegrationMessageRepository repository;

    public IntegrationMessageController(IntegrationMessageRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<IntegrationMessage> findAll() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IntegrationMessage create(@RequestBody CreateMessageRequest request) {
        return repository.save(new IntegrationMessage(request.message()));
    }

    @PutMapping("/{id}")
    public IntegrationMessage update(@PathVariable Long id, @RequestBody CreateMessageRequest request) {
        IntegrationMessage message = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + id));
        message.setMessage(request.message());
        return repository.save(message);
    }

    public record CreateMessageRequest(String message) {
    }
}
