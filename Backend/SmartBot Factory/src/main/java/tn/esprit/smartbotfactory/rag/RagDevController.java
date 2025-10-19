package tn.esprit.smartbotfactory.rag;

import org.springframework.web.bind.annotation.*;
import tn.esprit.smartbotfactory.rag.RagService;

@RestController
@RequestMapping("/dev/rag")
public class RagDevController {

    private final RagService rag;

    public RagDevController(RagService rag) { this.rag = rag; }

    @PostMapping("/ensure/{botId}")
    public void ensure(@PathVariable long botId) { rag.ensureBotCollection(botId); }

    @DeleteMapping("/drop/{botId}")
    public void drop(@PathVariable long botId) { rag.dropBotCollection(botId); }

    @PostMapping("/seed/{botId}")
    public long seed(@PathVariable long botId, @RequestParam(defaultValue = "50") int n) {
        rag.ensureBotCollection(botId);
        return rag.seedRandom(botId, n);
    }
}
