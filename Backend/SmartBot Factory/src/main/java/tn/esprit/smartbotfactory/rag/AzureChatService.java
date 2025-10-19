
package tn.esprit.smartbotfactory.rag;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AzureChatService {

    private final RestTemplate rest = new RestTemplate();
    private final RagProperties props;

    public AzureChatService(RagProperties props) {
        this.props = props;
    }

    /** Appelle /chat/completions du déploiement Azure (gpt-4o) et renvoie le texte de la 1re choice. */
    @SuppressWarnings("unchecked")
    public String chat(List<Map<String, String>> messages, Double temperature) {
        String url = props.azureChatUrl();

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.set("api-key", props.getAzureApiKeyOrFallback()); // Azure: header api-key

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("messages", messages);
        body.put("temperature", temperature != null ? temperature : 0.2);

        HttpEntity<Map<String, Object>> req = new HttpEntity<>(body, h);
        Map<String, Object> resp = rest.postForObject(url, req, Map.class);
        if (resp == null) return "";

        Object choices = resp.get("choices");
        if (choices instanceof List<?> list && !list.isEmpty()) {
            Object first = list.get(0);
            if (first instanceof Map<?, ?> ch) {
                Object msg = ch.get("message");
                if (msg instanceof Map<?, ?> m) {
                    Object content = m.get("content");
                    return content != null ? content.toString() : "";
                }
            }
        }
        return "";
    }
}
