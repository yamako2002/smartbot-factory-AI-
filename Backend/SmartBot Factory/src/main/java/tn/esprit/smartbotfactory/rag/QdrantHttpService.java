package tn.esprit.smartbotfactory.rag;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tn.esprit.smartbotfactory.rag.QdrantDtos.*;

@Service
public class QdrantHttpService {

    private final WebClient qdrant;
    private final RagProperties props;

    public QdrantHttpService(WebClient qdrantWebClient, RagProperties props) {
        this.qdrant = qdrantWebClient;
        this.props = props;
    }

    private String collectionUrl(String collection) {
        return "/collections/" + collection;
    }

    /** Crée une collection si elle n'existe pas. */
    public void createCollectionIfNeeded(String collection) {
        CreateCollectionRequest req = new CreateCollectionRequest(
                new VectorParams(props.getDim(), "Cosine")
        );
        qdrant.put()
                .uri(collectionUrl(collection))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(BasicResponse.class)
                .onErrorResume(e -> Mono.just(new BasicResponse())) // si existe déjà, Qdrant renvoie 409 => on ignore
                .block();
    }

    public void deleteCollection(String collection) {
        qdrant.delete()
                .uri(collectionUrl(collection))
                .retrieve()
                .bodyToMono(BasicResponse.class)
                .block();
    }

    public UpsertResponse upsert(String collection, UpsertPointsRequest body) {
        return qdrant.put()
                .uri(collectionUrl(collection) + "/points?wait=true")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(UpsertResponse.class)
                .block();
    }

    public SearchResponse search(String collection, SearchRequest body) {
        return qdrant.post()
                .uri(collectionUrl(collection) + "/points/search")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(SearchResponse.class)
                .block();
    }
}
