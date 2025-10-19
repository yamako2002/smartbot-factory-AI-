package tn.esprit.smartbotfactory.rag;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "rag")
public class RagProperties {

    // ===================== QDRANT =====================
    @NestedConfigurationProperty
    private final Qdrant qdrant = new Qdrant();

    public static class Qdrant {
        private String host = "localhost";
        private int httpPort = 6333;
        private String apiKey;
        private int dim = 1536;
        private String collectionPrefix = "bot_";

        public String baseUrl() { return "http://" + host + ":" + httpPort; }

        // getters / setters
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        public int getHttpPort() { return httpPort; }
        public void setHttpPort(int httpPort) { this.httpPort = httpPort; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public int getDim() { return dim; }
        public void setDim(int dim) { this.dim = dim; }
        public String getCollectionPrefix() { return collectionPrefix; }
        public void setCollectionPrefix(String collectionPrefix) { this.collectionPrefix = collectionPrefix; }
    }

    // ===================== Raccourcis Qdrant =====================
    public String baseUrl() { return qdrant.baseUrl(); }
    public int getDim() { return qdrant.getDim(); }
    public String getCollectionPrefix() { return qdrant.getCollectionPrefix(); }
    public String getApiKey() { return qdrant.getApiKey(); }

    // ===================== OPENAI PUBLIC =====================
    @NestedConfigurationProperty
    private final OpenAI openai = new OpenAI();

    public static class OpenAI {
        private String apiKey;
        private String baseUrl = "https://api.openai.com";
        private String embeddingModel = "text-embedding-3-small";

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getBaseUrl() { return baseUrl; }
        public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
        public String getEmbeddingModel() { return embeddingModel; }
        public void setEmbeddingModel(String embeddingModel) { this.embeddingModel = embeddingModel; }
    }

    // ===================== AZURE OPENAI =====================
    @NestedConfigurationProperty
    private final AzureOpenAI azure = new AzureOpenAI();

    public static class AzureOpenAI {
        private boolean enabled = false;
        private String endpoint;
        private String apiKey;

        // Déploiement pour Chat
        private String chatDeployment;
        private String chatApiVersion = "2025-01-01-preview";

        // Déploiement pour Embeddings
        private String embeddingDeployment;
        private String embeddingApiVersion = "2023-05-15";

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }

        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        // Chat
        public String getChatDeployment() { return chatDeployment; }
        public void setChatDeployment(String chatDeployment) { this.chatDeployment = chatDeployment; }
        public String getChatApiVersion() { return chatApiVersion; }
        public void setChatApiVersion(String chatApiVersion) { this.chatApiVersion = chatApiVersion; }

        // Embeddings
        public String getEmbeddingDeployment() { return embeddingDeployment; }
        public void setEmbeddingDeployment(String embeddingDeployment) { this.embeddingDeployment = embeddingDeployment; }
        public String getEmbeddingApiVersion() { return embeddingApiVersion; }
        public void setEmbeddingApiVersion(String embeddingApiVersion) { this.embeddingApiVersion = embeddingApiVersion; }

        // Helpers URLs
        public String chatUrl() {
            String ep = (endpoint != null && endpoint.endsWith("/")) ? endpoint : (endpoint + "/");
            return ep + "openai/deployments/" + chatDeployment + "/chat/completions?api-version=" + chatApiVersion;
        }

        public String embeddingsUrl() {
            String ep = (endpoint != null && endpoint.endsWith("/")) ? endpoint : (endpoint + "/");
            return ep + "openai/deployments/" + embeddingDeployment + "/embeddings?api-version=" + embeddingApiVersion;
        }
    }

    // ===================== Accès aux sous-blocs =====================
    public Qdrant getQdrant() { return qdrant; }
    public OpenAI getOpenai() { return openai; }
    public AzureOpenAI getAzure() { return azure; }

    // ===================== Raccourcis OpenAI public =====================
    public String getOpenAiApiKey() { return openai.getApiKey(); }
    public String getOpenAiBaseUrl() { return openai.getBaseUrl(); }
    public String getOpenAiEmbeddingModel() { return openai.getEmbeddingModel(); }

    // ===================== Raccourcis Azure =====================
    public boolean isAzureEnabled() { return azure.isEnabled(); }
    public String getAzureApiKeyOrFallback() {
        return azure.getApiKey() != null ? azure.getApiKey() : openai.getApiKey();
    }
    public String azureChatUrl() { return azure.chatUrl(); }
    public String azureEmbeddingsUrl() { return azure.embeddingsUrl(); }
}
