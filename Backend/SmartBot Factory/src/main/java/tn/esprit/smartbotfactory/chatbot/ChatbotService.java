package tn.esprit.smartbotfactory.chatbot;
import  tn.esprit.smartbotfactory.chatbot.dto.OwnerResponse;
import tn.esprit.smartbotfactory.chatbot.dto.ChatbotResponse;
import tn.esprit.smartbotfactory.chatbot.dto.CreateChatbotRequest;
import tn.esprit.smartbotfactory.user.User;
import tn.esprit.smartbotfactory.user.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatbotService {

    private final ChatbotRepository repo;
    private final UserRepository userRepository;

    public ChatbotService(ChatbotRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChatbotResponse create(CreateChatbotRequest req) {
        // 1) récupérer l’email du user depuis le SecurityContext (mis par JwtAuthFilter)
        String email = currentEmail();
        if (email == null) throw new RuntimeException("Unauthenticated");

        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        // 2) créer le bot
        Chatbot bot = new Chatbot();
        bot.setName(req.getName());
        bot.setDomain(req.getDomain());
        bot.setStatus("READY");
        bot.setSources(req.getSources() == null ? new ArrayList<>() : req.getSources());
        bot.setOwner(owner);

        bot = repo.save(bot);
        return toResponse(bot);
    }

    @Transactional(readOnly = true)
    public List<ChatbotResponse> list() {
        // Option simple : retourner tous (ou filtre par owner si tu veux)
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ChatbotResponse get(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Chatbot not found: " + id));
    }

    private String currentEmail() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? (String) auth.getPrincipal() : null;
    }

    private ChatbotResponse toResponse(Chatbot c) {
        ChatbotResponse res = new ChatbotResponse();
        res.setId(c.getId());
        res.setName(c.getName());
        res.setDomain(c.getDomain());
        res.setStatus(c.getStatus());
        res.setSources(c.getSources());
        res.setCreatedAt(c.getCreatedAt());
        res.setUpdatedAt(c.getUpdatedAt());

        if (c.getOwner() != null) {
            res.setOwner(new OwnerResponse(
                    c.getOwner().getId(),
                    c.getOwner().getEmail(),
                    c.getOwner().getFullName()
            ));
        } else {
            res.setOwner(null);
        }
        return res;
    }
}
