package tn.esprit.smartbotfactory.chatbot.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChatbotResponse {
    private Long id;
    private String name;
    private String domain;
    private String status;
    private List<String> sources;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OwnerResponse owner;   // 👈 nouveau

    public ChatbotResponse() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public OwnerResponse getOwner() { return owner; }
    public void setOwner(OwnerResponse owner) { this.owner = owner; }
}
