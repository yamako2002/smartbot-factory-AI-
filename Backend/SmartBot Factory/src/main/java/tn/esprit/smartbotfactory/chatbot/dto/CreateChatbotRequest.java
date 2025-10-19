package tn.esprit.smartbotfactory.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CreateChatbotRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String domain;

    private List<String> sources; // optionnel

    public CreateChatbotRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }
}
