package tn.esprit.smartbotfactory.rag;

public class ExternalBot {
    private String id;
    private String name;
    private String description;
    private String provider;
    private String source = "OpenRouter";
    private double score;

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}


