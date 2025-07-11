package edu.progavud.parcial3pa.follow;

import edu.progavud.parcial3pa.auth.User;
import edu.progavud.parcial3pa.auth.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    /** Repositorio para acceder a los datos de los usuarios. */
    @Autowired
    private UserRepository userRepository;

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
        try {
            // Usar userRepository local primero, luego RestTemplate como fallback
            User from = userRepository.findByUsername(fromUsername)
                    .orElseGet(() -> {
                        try {
                            return restTemplate.getForObject(
                                "https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + fromUsername, 
                                User.class);
                        } catch (Exception e) {
                            System.err.println("Error obteniendo usuario from: " + e.getMessage());
                            return null;
                        }
                    });

            User to = userRepository.findByUsername(toUsername)
                    .orElseGet(() -> {
                        try {
                            return restTemplate.getForObject(
                                "https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + toUsername, 
                                User.class);
                        } catch (Exception e) {
                            System.err.println("Error obteniendo usuario to: " + e.getMessage());
                            return null;
                        }
                    });

            if (from == null || to == null) {
                System.err.println("No se pudieron obtener los usuarios: from=" + from + ", to=" + to);
                return false;
            }

            if (from.getId().equals(to.getId())) {
                System.err.println("Un usuario no puede seguirse a sí mismo");
                return false;
            }

            // Verificar si ya existe la relación
            if (followRepository.existsByFollowerAndFollowed(from, to)) {
                System.out.println("La relación de seguimiento ya existe");
                return true; // Ya existe, consideramos exitoso
            }

            // Crear la relación de seguimiento
            followRepository.save(new Follow(from, to));
            System.out.println("Relación de seguimiento creada exitosamente");

            // Actualizar contadores de forma asíncrona para evitar bloqueos
            try {
                restTemplate.postForObject(
                    "https://exciting-tranquility-production-14e6.up.railway.app/profile/increment-follow", 
                    Map.of("follower", from.getUsername(), "followed", to.getUsername()), 
                    Void.class);
                System.out.println("Contadores actualizados exitosamente");
            } catch (Exception e) {
                System.err.println("Error actualizando contadores: " + e.getMessage());
                // No fallar la operación si solo fallan los contadores
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error en followUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
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
        try {
            // Usar userRepository local primero, luego RestTemplate como fallback
            User from = userRepository.findByUsername(fromUsername)
                    .orElseGet(() -> {
                        try {
                            return restTemplate.getForObject(
                                "https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + fromUsername, 
                                User.class);
                        } catch (Exception e) {
                            System.err.println("Error obteniendo usuario from: " + e.getMessage());
                            return null;
                        }
                    });

            User to = userRepository.findByUsername(toUsername)
                    .orElseGet(() -> {
                        try {
                            return restTemplate.getForObject(
                                "https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + toUsername, 
                                User.class);
                        } catch (Exception e) {
                            System.err.println("Error obteniendo usuario to: " + e.getMessage());
                            return null;
                        }
                    });

            if (from == null || to == null) {
                System.err.println("No se pudieron obtener los usuarios: from=" + from + ", to=" + to);
                return false;
            }

            if (from.getId().equals(to.getId())) {
                System.err.println("Un usuario no puede dejar de seguirse a sí mismo");
                return false;
            }

            // Verificar si existe la relación antes de eliminar
            if (!followRepository.existsByFollowerAndFollowed(from, to)) {
                System.out.println("La relación de seguimiento no existe");
                return true; // No existe, consideramos exitoso
            }

            // Eliminar la relación
            followRepository.deleteByFollowerAndFollowed(from, to);
            System.out.println("Relación de seguimiento eliminada exitosamente");

            // Actualizar contadores de forma asíncrona
            try {
                restTemplate.postForObject(
                    "https://exciting-tranquility-production-14e6.up.railway.app/profile/decrement-follow", 
                    Map.of("follower", from.getUsername(), "followed", to.getUsername()), 
                    Void.class);
                System.out.println("Contadores decrementados exitosamente");
            } catch (Exception e) {
                System.err.println("Error decrementando contadores: " + e.getMessage());
                // No fallar la operación si solo fallan los contadores
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error en unfollowUser: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si un usuario está siguiendo a otro.
     *
     * @param fromUsername nombre del usuario que podría estar siguiendo
     * @param toUsername nombre del usuario que podría estar siendo seguido
     * @return true si existe la relación de seguimiento, false en caso contrario
     */
    public boolean isFollowing(String fromUsername, String toUsername) {
        try {
            // Usar userRepository local primero, luego RestTemplate como fallback
            User from = userRepository.findByUsername(fromUsername)
                    .orElseGet(() -> {
                        try {
                            return restTemplate.getForObject(
                                "https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + fromUsername, 
                                User.class);
                        } catch (Exception e) {
                            System.err.println("Error obteniendo usuario from: " + e.getMessage());
                            return null;
                        }
                    });

            User to = userRepository.findByUsername(toUsername)
                    .orElseGet(() -> {
                        try {
                            return restTemplate.getForObject(
                                "https://exciting-tranquility-production-14e6.up.railway.app/auth/by-username/" + toUsername, 
                                User.class);
                        } catch (Exception e) {
                            System.err.println("Error obteniendo usuario to: " + e.getMessage());
                            return null;
                        }
                    });

            if (from == null || to == null) {
                System.err.println("No se pudieron obtener los usuarios para verificar seguimiento");
                return false;
            }

            if (from.getId().equals(to.getId())) {
                return false; // Un usuario no se sigue a sí mismo
            }

            return followRepository.existsByFollowerAndFollowed(from, to);
        } catch (Exception e) {
            System.err.println("Error verificando seguimiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina todas las relaciones de seguimiento en las que el usuario es seguidor o seguido.
     *
     * @param userId ID del usuario
     */
    public void deleteAllByUserId(Long userId) {
        try {
            followRepository.deleteAllByUserId(userId);
            System.out.println("Todas las relaciones de seguimiento eliminadas para userId: " + userId);
        } catch (Exception e) {
            System.err.println("Error eliminando relaciones de seguimiento: " + e.getMessage());
            throw new RuntimeException("Error al eliminar relaciones de seguimiento", e);
        }
    }
}