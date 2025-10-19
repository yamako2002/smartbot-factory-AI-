// src/main/java/tn/esprit/smartbotfactory/rag/RagDebugController.java
package tn.esprit.smartbotfactory.rag;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/dev/rag/debug")
public class RagDebugController {

    private final EmbeddingService embedding;

    public RagDebugController(EmbeddingService embedding) {
        this.embedding = embedding;
    }

    @GetMapping("/embed")
    public Map<String,Object> embed(@RequestParam String text) {
        var vec = embedding.embed(text);
        return Map.of("len", vec.size(), "first5", vec.subList(0, Math.min(5, vec.size())));
    }
}
