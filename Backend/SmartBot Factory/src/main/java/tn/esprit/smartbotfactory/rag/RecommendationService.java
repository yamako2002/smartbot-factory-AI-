package tn.esprit.smartbotfactory.rag;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.smartbotfactory.chatbot.Chatbot;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    // ==============================
    // 🔹 AZURE EMBEDDING CONFIG
    // ==============================
    @Value("${rag.azure.endpoint}")
    private String azureEndpoint;

    @Value("${rag.azure.api-key}")
    private String azureApiKey;

    @Value("${rag.azure.embedding-deployment:text-embedding-3-small}")
    private String azureEmbeddingDeployment;

    @Value("${rag.azure.embedding-api-version:2023-05-15}")
    private String azureEmbeddingApiVersion;

    // ==============================
    // 🔹 RECOMMENDATION SETTINGS
    // ==============================
    @Value("${ai.recommendation.topk:5}")
    private int topK;

    @Value("${ai.recommendation.threshold:0.5}") // Default strictness
    private double threshold;

    private final RestTemplate restTemplate = new RestTemplate();

    // ==============================
    // 🔹 1. Generate embeddings via Azure OpenAI
    // ==============================
    public List<Double> embedText(String text) {
        try {
            String url = String.format(
                    "%s/openai/deployments/%s/embeddings?api-version=%s",
                    azureEndpoint, azureEmbeddingDeployment, azureEmbeddingApiVersion
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", azureApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("input", text);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("data")) {
                throw new RuntimeException("Empty embedding response from Azure");
            }

            List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");
            List<?> embedding = (List<?>) data.get(0).get("embedding");

            return embedding.stream().map(o -> ((Number) o).doubleValue()).toList();

        } catch (Exception e) {
            System.err.println("❌ Azure embedding error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==============================
    // 🔹 2. Compute cosine similarity
    // ==============================
    private double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a.isEmpty() || b.isEmpty() || a.size() != b.size()) return 0.0;
        double dot = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.size(); i++) {
            dot += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }
        return (normA == 0 || normB == 0) ? 0.0 : dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // ==============================
    // 🔹 3. Fetch external chatbot models from OpenRouter
    // ==============================
    private List<Map<String, String>> fetchExternalBots() {
        String url = "https://openrouter.ai/api/v1/models";
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Object data = response.getBody() != null ? response.getBody().get("data") : null;

            if (!(data instanceof List<?> models)) return Collections.emptyList();

            // ⚡ Limit to 10 models for faster dev tests
            return models.stream()
                    .limit(10)
                    .map(obj -> {
                        Map<String, Object> m = (Map<String, Object>) obj;
                        Map<String, String> bot = new HashMap<>();
                        bot.put("name", (String) m.getOrDefault("id", "Unknown"));
                        bot.put("description", (String) m.getOrDefault("description", ""));
                        bot.put("provider", (String) m.getOrDefault("provider", "Unknown"));
                        return bot;
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("⚠️ Failed to fetch external bots: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==============================
    // 🔹 4. Recommend similar bots (internal + external)
    // ==============================
    public List<Map<String, Object>> recommendSimilarBots(String query, List<Chatbot> allBots) {
        return recommendSimilarBots(query, allBots, 0.0);
    }

    public List<Map<String, Object>> recommendSimilarBots(String query, List<Chatbot> allBots, double customThreshold) {
        double effectiveThreshold = (customThreshold > 0) ? customThreshold : threshold;
        System.out.println("🔍 Using threshold: " + effectiveThreshold);

        List<Double> queryEmbedding = embedText(query);
        if (queryEmbedding.isEmpty()) return Collections.emptyList();

        List<Map<String, Object>> recommendations = new ArrayList<>();

        // 🧩 A. Compare with internal SmartBotFactory bots
        for (Chatbot bot : allBots) {
            String content = bot.getName() + " " + bot.getDomain();
            List<Double> botEmbedding = embedText(content);
            double score = cosineSimilarity(queryEmbedding, botEmbedding);

            if (score >= effectiveThreshold) {
                recommendations.add(Map.of(
                        "name", bot.getName(),
                        "domain", bot.getDomain(),
                        "source", "SmartBotFactory",
                        "score", score
                ));
            }
        }

        // 🌍 B. Compare with external OpenRouter models
        List<Map<String, String>> externalBots = fetchExternalBots();
        System.out.println("✅ Retrieved " + externalBots.size() + " external models for comparison.");

        for (Map<String, String> ext : externalBots) {
            String text = ext.get("name") + " " + ext.get("description");
            List<Double> emb = embedText(text);
            double score = cosineSimilarity(queryEmbedding, emb);

            System.out.printf("→ %s : %.3f%n", ext.get("name"), score);

            if (score >= effectiveThreshold) {
                recommendations.add(Map.of(
                        "name", ext.get("name"),
                        "provider", ext.get("provider"),
                        "source", "OpenRouter",
                        "score", score
                ));
            }
        }

        // 🧮 Sort and limit
        return recommendations.stream()
                .sorted((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")))
                .limit(topK)
                .collect(Collectors.toList());
    }
}
