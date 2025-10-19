// src/main/java/tn/esprit/smartbotfactory/rag/RagService.java
package tn.esprit.smartbotfactory.rag;

import tn.esprit.smartbotfactory.rag.QdrantDtos.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
public class RagService {

    private final QdrantHttpService qdrant;
    private final RagProperties props;
    private final EmbeddingService embedding;
    private final AzureChatService chat;

    public RagService(QdrantHttpService qdrant,
                      RagProperties props,
                      EmbeddingService embedding,
                      AzureChatService chat) {
        this.qdrant = qdrant;
        this.props = props;
        this.embedding = embedding;
        this.chat = chat;
    }

    // --- Utilitaires de seed aléatoire ---
    private List<Double> randomVector() {
        Random rnd = new Random();
        return DoubleStream.generate(rnd::nextDouble)
                .limit(props.getDim())
                .boxed()
                .collect(Collectors.toList());
    }

    public long seedRandom(long botId, int n) {
        ensureBotCollection(botId);
        List<Point> pts = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Map<String, Object> payload = Map.of("text", "dummy #" + i);
            pts.add(new Point(UUID.randomUUID().toString(), randomVector(), payload));
        }
        return upsert(botId, pts);
    }

    public String collectionForBot(long botId) { return props.getCollectionPrefix() + botId; }

    public void ensureBotCollection(long botId) { qdrant.createCollectionIfNeeded(collectionForBot(botId)); }
    public void dropBotCollection(long botId) { qdrant.deleteCollection(collectionForBot(botId)); }

    public long upsert(long botId, List<Point> points) {
        String col = collectionForBot(botId);
        ensureBotCollection(botId);

        List<QdrantDtos.PointStruct> pt = points.stream()
                .map(p -> new QdrantDtos.PointStruct(
                        p.id,
                        p.vector,
                        p.payload == null ? Map.of("text", "") : p.payload))
                .collect(Collectors.toList());

        UpsertPointsRequest req = new UpsertPointsRequest(pt);
        qdrant.upsert(col, req);

        // ⚠️ Qdrant ne renvoie pas toujours le nombre exact de points,
        // donc on retourne la taille envoyée
        return points.size();
    }

    // -------- Recherche avec payload forcé --------
    public List<SearchHit> search(long botId, List<Double> query, int k) {
        String col = collectionForBot(botId);

        SearchRequest req = new SearchRequest(query, k);
        req.with_payload = true; // ✅ pour avoir les textes

        SearchResponse res = qdrant.search(col, req);
        if (res == null || res.result == null) return Collections.emptyList();
        return res.result.stream()
                .map(r -> new SearchHit(r.id, r.score, r.payload))
                .collect(Collectors.toList());
    }

    // -------- Ingestion à partir de textes --------
    public long ingestTexts(long botId, List<String> texts) {
        ensureBotCollection(botId);
        System.out.println("📥 [RAG] Ingestion demandée pour botId=" + botId
                + " avec " + texts.size() + " textes");

        List<List<Double>> vectors = embedding.embedAll(texts);
        if (vectors == null) {
            System.err.println("❌ [RAG] embedding.embedAll a renvoyé null !");
            return 0;
        }
        System.out.println("✅ [RAG] Embeddings générés: " + vectors.size());
        int count = Math.min(texts.size(), vectors.size());
        System.out.println("➡️ [RAG] Nombre de points à insérer: " + count);

        List<Point> pts = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String text = texts.get(i) != null ? texts.get(i) : "";
            List<Double> vec = vectors.get(i);

            if (vec == null) {
                System.err.println("⚠️ [RAG] Embedding null pour le texte: \"" + text + "\"");
                continue;
            }

            if (vec.size() != props.getDim()) {
                System.err.println("⚠️ [RAG] Taille d'embedding incorrecte (" + vec.size() +
                        " au lieu de " + props.getDim() + ") pour texte: \"" + text + "\"");
            } else {
                System.out.println("✔️ [RAG] Texte[" + i + "] = \"" + text
                        + "\" | embedding size=" + vec.size());
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("text", text);
            pts.add(new Point(UUID.randomUUID().toString(), vec, payload));
        }

        long upserted = upsert(botId, pts);
        System.out.println("📊 [RAG] Points insérés dans Qdrant: " + upserted);

        return upserted;
    }

    // -------- Recherche sémantique --------
    public List<SearchHit> semanticSearch(long botId, String query, int k) {
        List<Double> v = embedding.embed(query);
        return search(botId, v, k);
    }

    // ----------- Boucle RAG complète -----------
    public AskResponse ask(long botId, String question, int k) {
        int topK = (k <= 0) ? 3 : k;

        List<SearchHit> hits = semanticSearch(botId, question, topK);

        StringBuilder ctx = new StringBuilder();
        for (int i = 0; i < hits.size(); i++) {
            Map<String, Object> payload = hits.get(i).payload;
            String text = (payload != null && payload.get("text") != null)
                    ? payload.get("text").toString() : "";
            ctx.append("[").append(i + 1).append("] ").append(text).append("\n");
        }

        // ✅ Nouveau prompt amélioré
        String system = """
                Tu es un assistant utile. Réponds en français, de façon claire et concise.

                Règles :
                1. Utilise UNIQUEMENT les informations du Contexte ci-dessous.
                2. Quand tu utilises une information, cite sa source avec son numéro entre crochets [ ].
                3. Si plusieurs sources contiennent la même information, cite UNIQUEMENT la première (évite les doublons comme [1][2][3]).
                4. Si le contexte ne contient pas la réponse, dis-le honnêtement.
                5. Ta réponse doit être naturelle, sans répéter inutilement le texte des sources.
                """;

        String user = "Question : " + question + "\n\nContexte :\n" + ctx;

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", system),
                Map.of("role", "user",   "content", user)
        );

        String answer = chat.chat(messages, 0.2);

        List<AskResponse.Source> sources = hits.stream()
                .map(h -> new AskResponse.Source(
                        h.id,
                        (h.payload != null && h.payload.get("text") != null)
                                ? h.payload.get("text").toString() : "",
                        h.score))
                .collect(Collectors.toList());

        return new AskResponse(answer, sources);
    }

    // ================== DTO internes ==================
    public static class Point {
        public final String id;
        public final List<Double> vector;
        public final Map<String, Object> payload;
        public Point(String id, List<Double> vector, Map<String, Object> payload) {
            this.id = id; this.vector = vector; this.payload = payload;
        }
    }

    public static class SearchHit {
        public final String id; public final double score; public final Map<String, Object> payload;
        public SearchHit(String id, double score, Map<String, Object> payload) {
            this.id = id; this.score = score; this.payload = payload;
        }
    }

    public static class AskResponse {
        public final String answer;
        public final List<Source> sources;
        public AskResponse(String answer, List<Source> sources) {
            this.answer = answer; this.sources = sources;
        }
        public static class Source {
            public final String id;
            public final String text;
            public final double score;
            public Source(String id, String text, double score) {
                this.id = id; this.text = text; this.score = score;
            }
        }
    }
}
