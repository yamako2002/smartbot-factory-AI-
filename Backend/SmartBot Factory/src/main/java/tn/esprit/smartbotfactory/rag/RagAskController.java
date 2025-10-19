// src/main/java/tn/esprit/smartbotfactory/rag/RagAskController.java
package tn.esprit.smartbotfactory.rag;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagAskController {

    private final RagService rag;

    public RagAskController(RagService rag) {
        this.rag = rag;
    }

    public record AskRequest(String question, Integer k) {}

    @PostMapping("/{botId}/ask")
    public RagService.AskResponse ask(@PathVariable long botId,
                                      @RequestBody AskRequest req) {
        int k = (req.k() == null || req.k() < 1) ? 3 : req.k();
        return rag.ask(botId, req.question(), k);
    }
}
