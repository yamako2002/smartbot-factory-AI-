package tn.esprit.smartbotfactory.rag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.smartbotfactory.chatbot.Chatbot;
import tn.esprit.smartbotfactory.chatbot.ChatbotRepository;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "*")
public class RecommendationController {

    @Autowired
    private ChatbotRepository chatbotRepository;

    @Autowired
    private RecommendationService recommendationService;

    /**
     * Endpoint example:
     *   GET /api/recommend?query=travel booking assistant
     *   GET /api/recommend?query=healthcare chatbot&threshold=0.1
     *
     * Returns a ranked list of chatbots (internal + external)
     */
    @GetMapping
    public List<Map<String, Object>> recommend(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "0.0") double threshold
    ) {
        List<Chatbot> allBots = chatbotRepository.findAll();
        return recommendationService.recommendSimilarBots(query, allBots, threshold);
    }
}
