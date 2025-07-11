package edu.progavud.parcial3pa.profile;

import edu.progavud.parcial3pa.auth.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Objects;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String bio;

    @Column(name = "profilePicture", length = 255)
    private String profilePicture;

    @Column(nullable = false)
    private int followers;

    @Column(nullable = false)
    private int following;

    @Column(name = "num_posts", nullable = false)
    private int numPosts;
    
//    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
//    private ArrayList<Post> posts = new ArrayList<>();

    // ✅ Relación con User
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Profile() {
    }

    public Profile(String bio, String profilePicture, int followers, int following, int numPosts, User user) {
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.followers = followers;
        this.following = following;
        this.numPosts = numPosts;
        this.user = user;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public int getNumPosts() {
        return numPosts;
    }

    public void setNumPosts(int numPosts) {
        this.numPosts = numPosts;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // equals, hashCode, toString
    @Override
    public String toString() {
        return "Profile{"
                + "id=" + id
                + ", bio='" + bio + '\''
                + ", profilePicture='" + profilePicture + '\''
                + ", followers=" + followers
                + ", following=" + following
                + ", numPosts=" + numPosts
                + ", user=" + user.getUsername()
                + // o user.getId()
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Profile profile)) {
            return false;
        }
        return Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
