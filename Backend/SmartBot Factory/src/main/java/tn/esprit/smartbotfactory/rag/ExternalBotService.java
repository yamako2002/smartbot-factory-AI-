package tn.esprit.smartbotfactory.rag;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExternalBotService {

    private static final String OPENROUTER_URL = "https://openrouter.ai/api/v1/models";

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public List<ExternalBot> fetchFromOpenRouter() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(OPENROUTER_URL, Map.class);
            Object data = response.getBody().get("data");
            if (!(data instanceof List<?> models)) return Collections.emptyList();

            return models.stream().map(obj -> {
                Map<String, Object> m = (Map<String, Object>) obj;
                ExternalBot bot = new ExternalBot();
                bot.setId((String) m.get("id"));
                bot.setName((String) m.get("name"));
                bot.setDescription((String) m.get("description"));
                bot.setProvider((String) m.get("provider"));
                return bot;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("⚠️ Failed to fetch external bots: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}

