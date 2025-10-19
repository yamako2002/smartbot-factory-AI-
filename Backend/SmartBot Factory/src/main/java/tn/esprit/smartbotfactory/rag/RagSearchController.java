    // src/main/java/tn/esprit/smartbotfactory/rag/RagSearchController.java
    package tn.esprit.smartbotfactory.rag;

    import org.springframework.web.bind.annotation.*;
    import java.util.List;

    @RestController
    @RequestMapping("/api/rag")
    public class RagSearchController {
        private final RagService rag;

        public RagSearchController(RagService rag) { this.rag = rag; }

        @GetMapping("/{botId}/search")
        public List<RagService.SearchHit> search(
                @PathVariable long botId,
                @RequestParam("q") String query,
                @RequestParam(value = "k", defaultValue = "3") int k) {
            return rag.semanticSearch(botId, query, k);
        }
    }
