package tn.esprit.smartbotfactory.rag;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAIEmbeddingService implements EmbeddingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final RagProperties props;

    public OpenAIEmbeddingService(RagProperties props) {
        this.props = props;
    }

    // ---------- API publique ----------
    @Override
    public List<Double> embed(String text) {
        List<List<Double>> all = embedAll(Collections.singletonList(text));
        return all.isEmpty() ? Collections.emptyList() : all.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<List<Double>> embedAll(List<String> texts) {
        String url;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();

        if (props.isAzureEnabled()) {
            // --------- Azure OpenAI ---------
            url = props.azureEmbeddingsUrl(); // .../openai/deployments/{deployment}/embeddings?api-version=...
            headers.set("api-key", props.getAzureApiKeyOrFallback());

            // Azure n’a pas besoin du "model", uniquement "input"
            body.put("input", texts);
        } else {
            // --------- OpenAI public ---------
            url = props.getOpenAiBaseUrl() + "/v1/embeddings";
            headers.setBearerAuth(props.getOpenAiApiKey());

            body.put("model", props.getOpenAiEmbeddingModel());
            body.put("input", texts);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        Map<String, Object> resp = restTemplate.postForObject(url, request, Map.class);
        if (resp == null) return Collections.emptyList();

        Object dataObj = resp.get("data");
        if (!(dataObj instanceof List<?> dataList)) {
            return Collections.emptyList();
        }

        List<List<Double>> result = new ArrayList<>(texts.size());

        for (Object item : dataList) {
            if (!(item instanceof Map<?, ?> m)) continue;
            Object emb = m.get("embedding");
            if (emb instanceof List<?> vec) {
                // Convertit chaque nombre en Double proprement
                List<Double> vector = new ArrayList<>(vec.size());
                for (Object n : vec) {
                    if (n instanceof Number num) {
                        vector.add(num.doubleValue());
                    } else if (n != null) {
                        // fallback si jamais le JSON arrive en String
                        try { vector.add(Double.parseDouble(n.toString())); }
                        catch (Exception ignored) { }
                    }
                }
                result.add(vector);
            }
        }
        return result;
    }
}
