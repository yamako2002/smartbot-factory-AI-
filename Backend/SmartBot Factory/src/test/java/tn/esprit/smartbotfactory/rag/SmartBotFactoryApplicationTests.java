package tn.esprit.smartbotfactory.rag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.smartbotfactory.chatbot.Chatbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SmartBotFactoryApplicationTests {

    @Autowired
    private RecommendationService recommendationService;

    private List<Chatbot> mockBots;

    @BeforeEach
    void setUp() {
        assertNotNull(recommendationService, "✅ RecommendationService should be autowired");

        // Create mock Chatbots for testing
        mockBots = new ArrayList<>();

        Chatbot bot1 = new Chatbot();
        bot1.setName("Travel Assistant");
        bot1.setDomain("Travel AI chatbot");
        mockBots.add(bot1);

        Chatbot bot2 = new Chatbot();
        bot2.setName("Health Advisor");
        bot2.setDomain("Health and wellness chatbot");
        mockBots.add(bot2);

        Chatbot bot3 = new Chatbot();
        bot3.setName("EduBot");
        bot3.setDomain("Education helper chatbot");
        mockBots.add(bot3);
    }

    @Test
    @DisplayName("🧠 Verify context loads successfully")
    void contextLoads() {
        assertTrue(true);
    }

    @Test
    @DisplayName("🤖 Test recommendSimilarBots() safely")
    void testRecommendSimilarBots() {
        String query = "chatbot for education and learning";

        List<Map<String, Object>> results = recommendationService.recommendSimilarBots(query, mockBots);

        assertNotNull(results, "Result list should not be null");
        System.out.println("✅ recommendSimilarBots() executed successfully, found " + results.size() + " results.");

        // Optional: print result details for visibility
        results.forEach(r -> System.out.println("→ " + r));
    }
}
