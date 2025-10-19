package tn.esprit.smartbotfactory.rag;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * DTO simples pour l'API HTTP de Qdrant.
 * On ne mappe que ce dont on a besoin.
 */
public class QdrantDtos {

    /* ---------- Create collection ---------- */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateCollectionRequest {
        public VectorParams vectors;

        public CreateCollectionRequest() {}
        public CreateCollectionRequest(VectorParams vectors) { this.vectors = vectors; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class VectorParams {
        public Integer size;             // dimension
        public String distance = "Cosine"; // Euclid / Dot / Cosine

        public VectorParams() {}
        public VectorParams(Integer size, String distance) {
            this.size = size;
            this.distance = distance;
        }
    }

    /* ---------- Upsert points ---------- */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpsertPointsRequest {
        public List<PointStruct> points;

        public UpsertPointsRequest() {}
        public UpsertPointsRequest(List<PointStruct> points) { this.points = points; }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PointStruct {
        public String id;                   // id arbitraire (string)
        public List<Double> vector;         // vecteur
        public Map<String, Object> payload; // payload libre (meta)

        public PointStruct() {}
        public PointStruct(String id, List<Double> vector, Map<String, Object> payload) {
            this.id = id;
            this.vector = vector;
            this.payload = payload;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UpsertResponse {
        public UpsertResult result;
        public double time;

        public static class UpsertResult {
            public String status;       // ex: "completed"
            public long operation_id;   // identifiant opération
        }
    }

    /* ---------- Search ---------- */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SearchRequest {
        public List<Double> vector;
        public Integer limit = 5;
        public Boolean with_payload = true;

        public SearchRequest() {}
        public SearchRequest(List<Double> vector, Integer limit) {
            this.vector = vector; this.limit = limit;this.with_payload = true;
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SearchResponse {
        public String status; // "ok"
        public List<ScoredPoint> result;

        public static class ScoredPoint {
            public String id;
            public double score;
            public Map<String, Object> payload;
        }
    }

    /* ---------- Generic response ---------- */

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BasicResponse {
        public String status; // "ok"
        public Object result;
    }
}
