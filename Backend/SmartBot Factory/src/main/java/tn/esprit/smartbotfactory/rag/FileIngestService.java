package tn.esprit.smartbotfactory.rag;

import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class FileIngestService {

    private final RagService rag;
    private final EmbeddingService embedding;

    private static final int CHUNK_SIZE = 1200;  // ~800-1500 ok
    private static final int OVERLAP    = 150;

    private final Tika tika = new Tika();

    public FileIngestService(RagService rag, EmbeddingService embedding) {
        this.rag = rag;
        this.embedding = embedding;
    }

    /** Ingestion d’un fichier arbitraire (PDF, DOCX, TXT…). */
    public Map<String, Object> ingest(long botId, MultipartFile file) {
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElse("file");
        String sourceId = "src_" + UUID.randomUUID();

        String extracted = extractText(file);
        List<String> chunks = TextChunker.split(extracted, CHUNK_SIZE, OVERLAP);
        if (chunks.isEmpty()) {
            return Map.of("upserted", 0, "chunks", 0, "fileName", fileName, "message", "Aucun texte exploitable");
        }

        // Embeddings
        List<List<Double>> vectors = embedding.embedAll(chunks);

        // Construire les points avec payload enrichi
        List<RagService.Point> points = new ArrayList<>(chunks.size());
        int count = Math.min(chunks.size(), vectors.size());

        for (int i = 0; i < count; i++) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("text", chunks.get(i));
            payload.put("sourceType", "FILE");
            payload.put("fileName", fileName);
            payload.put("sourceId", sourceId);
            payload.put("chunkIndex", i);

            points.add(new RagService.Point(UUID.randomUUID().toString(), vectors.get(i), payload));
        }

        long upserted = rag.upsert(botId, points);

        return Map.of(
                "upserted", upserted,
                "chunks", count,
                "sourceId", sourceId,
                "fileName", fileName
        );
    }

    /** Extraction via Tika (gère PDF/DOCX/TXT/HTML, etc.). */
    private String extractText(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            // AutoDetect + handler large
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();
            parser.parse(is, handler, metadata, context);
            return TextChunker.normalize(handler.toString());
        } catch (Exception e) {
            // fallback simple si besoin
            try {
                return TextChunker.normalize(tika.parseToString(file.getInputStream()));
            } catch (Exception ignored) {
                return "";
            }
        }
    }
}