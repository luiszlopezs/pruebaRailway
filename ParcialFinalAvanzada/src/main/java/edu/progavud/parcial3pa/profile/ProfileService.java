package edu.progavud.parcial3pa.profile;

import edu.progavud.parcial3pa.auth.User;
import edu.progavud.parcial3pa.auth.UserRepository;
import edu.progavud.parcial3pa.follow.FollowRepository;
import edu.progavud.parcial3pa.home.CommentRepository;
import edu.progavud.parcial3pa.home.Post;
import edu.progavud.parcial3pa.home.PostLikeRepository;
import edu.progavud.parcial3pa.home.PostRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private PostLikeRepository postLikeRepository;
    
    @Autowired
    private FollowRepository followRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, RestTemplate restTemplate) {
        this.profileRepository = profileRepository;
        this.restTemplate = restTemplate;
    }

    public Profile updateProfile(Profile updatedProfile) {
        Profile existingProfile = profileRepository.findByUsername(updatedProfile.getUser().getUsername())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró el perfil con username: " + updatedProfile.getUser().getUsername()));

        existingProfile.setBio(updatedProfile.getBio());
        existingProfile.setProfilePicture(updatedProfile.getProfilePicture());

        if (updatedProfile.getUser() != null && updatedProfile.getUser().getUsername() != null) {
            existingProfile.getUser().setUsername(updatedProfile.getUser().getUsername());
        }

        return profileRepository.save(existingProfile);
    }

    public Profile findByUsername(String username) {
        Optional<Profile> existente = profileRepository.findByUsername(username);

        if (existente.isPresent()) {
            return existente.get();
        }

        User user = restTemplate.getForObject("https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + username, User.class);

        if (user == null || user.getId() == null) {
            throw new RuntimeException("No se pudo obtener el usuario desde auth-service");
        }

        Profile nuevoPerfil = new Profile();
        nuevoPerfil.setUser(user);
        nuevoPerfil.setBio("hi, I'm using Insta!");
        nuevoPerfil.setFollowers(0);
        nuevoPerfil.setFollowing(0);
        nuevoPerfil.setNumPosts(0);
        nuevoPerfil.setProfilePicture("/postsImgs/profile_pictures/default.jpg");

        profileRepository.save(nuevoPerfil);

        return nuevoPerfil;
    }

    @Transactional
    public void incrementCounters(String followerUsername, String followedUsername) {
        Profile follower = profileRepository.findByUsername(followerUsername).orElseThrow();
        Profile followed = profileRepository.findByUsername(followedUsername).orElseThrow();

        follower.setFollowing(follower.getFollowing() + 1);
        followed.setFollowers(followed.getFollowers() + 1);
        profileRepository.save(follower);
        profileRepository.save(followed);
    }

    @Transactional
    public void decrementCounters(String followerUsername, String followedUsername) {
        Profile follower = profileRepository.findByUsername(followerUsername).orElseThrow();
        Profile followed = profileRepository.findByUsername(followedUsername).orElseThrow();

        follower.setFollowing(Math.max(0, follower.getFollowing() - 1));
        followed.setFollowers(Math.max(0, followed.getFollowers() - 1));
        profileRepository.save(follower);
        profileRepository.save(followed);
    }

    /**
     * Elimina el perfil de usuario y todos sus datos distribuidos en otros
     * microservicios:
     * - Comentarios
     * - Likes
     * - Posts
     * - Seguidores y seguidos
     * - Perfil
     * - Cuenta de usuario
     */
@Transactional
    public void deleteProfileAndUser(String username) {
        Optional<Profile> perfilOpt = profileRepository.findByUsername(username);
        
        if (perfilOpt.isEmpty()) {
            throw new RuntimeException("Perfil no encontrado");
        }
        
        Profile perfil = perfilOpt.get();
        Long userId = perfil.getUser().getId();
        
        try {
            // 1. Eliminar comentarios del usuario (en todos los posts)
            commentRepository.deleteByUserId(userId);
            
            // 2. Eliminar likes del usuario (en todos los posts)
            postLikeRepository.deleteByUserId(userId);
            
            // 3. Eliminar comentarios en posts del usuario
            List<Post> userPosts = postRepository.findByUserId(userId);
            for (Post post : userPosts) {
                commentRepository.deleteByPostId(post.getId());
                postLikeRepository.deleteByPostId(post.getId());
            }
            
            // 4. Eliminar posts del usuario
            postRepository.deleteByUserId(userId);
            
            // 5. Eliminar relaciones de seguimiento
            followRepository.deleteAllByUserId(userId);
            
            // 6. Eliminar perfil
            profileRepository.delete(perfil);
            profileRepository.flush();
            
            // 7. Eliminar usuario
            userRepository.deleteById(userId);
            
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar perfil y usuario: " + e.getMessage(), e);
        }
    }
}