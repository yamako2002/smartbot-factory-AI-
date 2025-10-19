package tn.esprit.smartbotfactory.rag;

public class ChatbotModel {
    private String name;
    private double score;

    public ChatbotModel() {}

    public ChatbotModel(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}
