package tn.esprit.smartbotfactory.chatbot;

import jakarta.persistence.*;
import tn.esprit.smartbotfactory.user.User; // <-- import de l'entité User
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chatbots")
public class Chatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private String status; // READY / INDEXING / ERROR

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ElementCollection
    @CollectionTable(
            name = "chatbot_sources",
            joinColumns = @JoinColumn(name = "chatbot_id", referencedColumnName = "id")
    )
    @Column(name = "sources")
    private List<String> sources = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ Add this transient field for recommendation similarity
    @Transient
    private double score;

    /* --- Lifecycle hooks pour timestamps --- */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.status == null) this.status = "READY";
        if (this.sources == null) this.sources = new ArrayList<>();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public double getScore() { return score; } // ✅ added
    public void setScore(double score) { this.score = score; } // ✅ added
}
