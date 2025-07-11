package edu.progavud.parcial3pa.follow;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "follows", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"from_user_id", "to_user_id"}))
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    @Column(name = "from_username", nullable = false)
    private String fromUsername;

    @Column(name = "to_username", nullable = false)
    private String toUsername;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructor por defecto
    public Follow() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Follow(Long fromUserId, Long toUserId, String fromUsername, String toUsername) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "id=" + id +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", fromUsername='" + fromUsername + '\'' +
                ", toUsername='" + toUsername + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}