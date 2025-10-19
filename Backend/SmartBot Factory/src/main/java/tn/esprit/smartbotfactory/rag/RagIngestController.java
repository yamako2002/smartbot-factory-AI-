// src/main/java/tn/esprit/smartbotfactory/rag/RagIngestController.java
package tn.esprit.smartbotfactory.rag;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagIngestController {
    private final RagService rag;

    public RagIngestController(RagService rag) { this.rag = rag; }

    @PostMapping("/{botId}/ingest-texts")
    public Map<String, Object> ingest(@PathVariable long botId, @RequestBody List<String> texts) {
        long n = rag.ingestTexts(botId, texts);
        return Map.of("upserted", n);
    }
}
