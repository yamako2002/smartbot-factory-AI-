package tn.esprit.smartbotfactory.chatbot.dto;

public class OwnerResponse {
    private Long id;
    private String email;
    private String fullName;

    public OwnerResponse() {}

    public OwnerResponse(Long id, String email, String fullName) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
