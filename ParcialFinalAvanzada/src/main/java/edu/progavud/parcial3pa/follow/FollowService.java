package edu.progavud.parcial3pa.follow;

import edu.progavud.parcial3pa.auth.User;
import edu.progavud.parcial3pa.auth.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio que contiene la lógica para gestionar las relaciones de seguimiento entre usuarios.
 */
@Service
@Transactional
public class FollowService {

            /** Repositorio para acceder y modificar relaciones de seguimiento. */
    @Autowired
    private FollowRepository followRepository;



        /** Cliente HTTP para realizar solicitudes a otros servicios. */
    @Autowired
    private RestTemplate restTemplate;

            /**
     * Crea una relación de seguimiento entre dos usuarios si aún no existe.
     * También envía una solicitud a un servicio externo para actualizar los contadores de seguimiento.
     *
     * @param fromUsername nombre del usuario que sigue
     * @param toUsername nombre del usuario que será seguido
     * @return true si la operación fue exitosa, false si los usuarios no existen o son iguales
     */
    @Transactional
    public boolean followUser(String fromUsername, String toUsername) {
        //User from = userRepository.findByUsername(fromUsername).orElse(null);
        //User to = userRepository.findByUsername(toUsername).orElse(null);
        
        User from = restTemplate.getForObject("https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + fromUsername, User.class);
        User to = restTemplate.getForObject("https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + toUsername, User.class);

        if (from == null || to == null || from.equals(to)) {
            return false;
        }

        if (!followRepository.existsByFollowerAndFollowed(from, to)) {
            followRepository.save(new Follow(from, to));

            // REST Template para actualizar contadores
            restTemplate.postForObject("https://exciting-tranquility-production-14e6.up.railway.app/profile/increment-follow", Map.of(
                    "follower", from.getUsername(),
                    "followed", to.getUsername()
            ), Void.class);
        }
        return true;
    }

            /**
     * Elimina una relación de seguimiento entre dos usuarios.
     * También envía una solicitud a un servicio externo para actualizar los contadores de seguimiento.
     *
     * @param fromUsername nombre del usuario que deja de seguir
     * @param toUsername nombre del usuario que es dejado de seguir
     * @return true si la operación fue exitosa, false si los usuarios no existen o son iguales
     */
    @Transactional
    public boolean unfollowUser(String fromUsername, String toUsername) {
        User from = restTemplate.getForObject("https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + fromUsername, User.class);
        
        User to = restTemplate.getForObject("https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + toUsername, User.class);

        if (from == null || to == null || from.equals(to)) {
            return false;
        }

        followRepository.deleteByFollowerAndFollowed(from, to);

        restTemplate.postForObject("https://exciting-tranquility-production-14e6.up.railway.app/profile/decrement-follow", Map.of(
                "follower", from.getUsername(),
                "followed", to.getUsername()
        ), Void.class);

        return true;
    }

    
        /**
     * Verifica si un usuario está siguiendo a otro.
     *
     * @param fromUsername nombre del usuario que podría estar siguiendo
     * @param toUsername nombre del usuario que podría estar siendo seguido
     * @return true si existe la relación de seguimiento, false en caso contrario
     */
    public boolean isFollowing(String fromUsername, String toUsername) {
        User from = restTemplate.getForObject("https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + fromUsername, User.class);
        
        User to = restTemplate.getForObject("https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + toUsername, User.class);

        if (from == null || to == null || from.equals(to)) {
            return false;
        }

        return followRepository.existsByFollowerAndFollowed(from, to);
    }
    
        /**
     * Elimina todas las relaciones de seguimiento en las que el usuario es seguidor o seguido.
     *
     * @param userId ID del usuario
     */
        public void deleteAllByUserId(Long userId) {
        followRepository.deleteAllByUserId(userId);
    }
        
        

}
