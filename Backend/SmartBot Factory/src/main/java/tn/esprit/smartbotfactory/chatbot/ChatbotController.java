package tn.esprit.smartbotfactory.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import tn.esprit.smartbotfactory.chatbot.dto.ChatbotResponse;
import tn.esprit.smartbotfactory.chatbot.dto.CreateChatbotRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatbots")
@CrossOrigin(origins = "http://localhost:4200")
public class ChatbotController {

    @Autowired
    private ChatbotService service;

    @Autowired
    private ChatbotRepository chatbotRepository;

    @PostMapping
    public ChatbotResponse create(@Valid @RequestBody CreateChatbotRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<ChatbotResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public ChatbotResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!chatbotRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        chatbotRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
