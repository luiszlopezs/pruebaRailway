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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de manejar la lógica relacionada con los perfiles de
 * usuario, incluyendo actualizaciones, contadores y eliminación de perfiles.
 */
@Service
public class ProfileService {

    /**
     * Repositorio para acceder y modificar perfiles de usuario.
     */
    @Autowired
    private ProfileRepository profileRepository;
    /**
     * Cliente HTTP para consumir servicios REST de otros módulos.
     */
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

    /**
     * Actualiza solo bio, profilePicture y username del usuario asociado.
     */
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

    /**
     * Busca un perfil por el nombre de usuario.
     *
     * @param username nombre de usuario
     * @return perfil correspondiente al usuario
     * @throws IllegalArgumentException si no se encuentra el perfil
     */
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
        nuevoPerfil.setProfilePicture("https://avatars.pfptown.com/327/black-and-white-pfp-2744.png");

        profileRepository.save(nuevoPerfil);

        return nuevoPerfil;
    }

    /**
     * Incrementa los contadores de "siguiendo" y "seguidores" entre dos
     * perfiles.
     *
     * @param followerUsername nombre de usuario del que sigue
     * @param followedUsername nombre de usuario del que es seguido
     * @throws NoSuchElementException si alguno de los perfiles no existe
     */
    @Transactional
    public void incrementCounters(String followerUsername, String followedUsername) {
        Profile follower = profileRepository.findByUsername(followerUsername).orElseThrow();
        Profile followed = profileRepository.findByUsername(followedUsername).orElseThrow();

        follower.setFollowing(follower.getFollowing() + 1);
        followed.setFollowers(followed.getFollowers() + 1);
        profileRepository.save(follower);
        profileRepository.save(followed);
    }

    /**
     * Decrementa los contadores de "siguiendo" y "seguidores" entre dos
     * perfiles, asegurando que no bajen de cero.
     *
     * @param followerUsername nombre de usuario del que deja de seguir
     * @param followedUsername nombre de usuario del que fue dejado de seguir
     * @throws NoSuchElementException si alguno de los perfiles no existe
     */
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
     * microservicios: - Comentarios - Likes - Posts - Seguidores y seguidos -
     * Perfil - Cuenta de usuario
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
            //commentRepository.deleteByUserId(userId);
            restTemplate.delete("https://exciting-tranquility-production-14e6.up.railway.app/posts/delete-comments/" + userId);

            // 2. Eliminar likes del usuario (en todos los posts)
            //postLikeRepository.deleteByUserId(userId);
            restTemplate.delete("https://exciting-tranquility-production-14e6.up.railway.app/posts/delete-likes/" + userId);

            // 3. Eliminar comentarios en posts del usuario
            //List<Post> userPosts = postRepository.findByUserId(userId);
            ResponseEntity<List<Post>> response = restTemplate.exchange(
                    "https://exciting-tranquility-production-14e6.up.railway.app/posts/get-posts/" + userId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Post>>() {
            }
            );

            List<Post> userPosts = response.getBody();

            if (userPosts != null) {
                for (Post post : userPosts) {
                    restTemplate.delete("https://exciting-tranquility-production-14e6.up.railway.app/posts/delete-comment-by-post/" + post.getId());
                    restTemplate.delete("https://exciting-tranquility-production-14e6.up.railway.app/posts/delete-like-by-post/" + post.getId());
                }
            }

            // 4. Eliminar posts del usuario
//            postRepository.deleteByUserId(userId);
            restTemplate.delete("https://exciting-tranquility-production-14e6.up.railway.app/posts/delete-posts-by-userId/" + userId);
            // 5. Eliminar relaciones de seguimiento
            //followRepository.deleteAllByUserId(userId);
            restTemplate.delete("https://exciting-tranquility-production-14e6.up.railway.app/follow/deleteByUserId/" + userId);

            // 6. Eliminar perfil
            profileRepository.delete(perfil);
            profileRepository.flush();

            // 7. Eliminar usuario
            //userRepository.deleteById(userId);
            restTemplate.delete("https://exciting-tranquility-production-14e6.up.railway.app/auth/delete/" + userId);

        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar perfil y usuario: " + e.getMessage(), e);
        }
    }
}
