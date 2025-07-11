package edu.progavud.parcial3pa.follow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Controlador REST encargado de manejar las operaciones relacionadas con seguir y dejar de seguir usuarios.
 */
@RestController
@RequestMapping("/follow")
@CrossOrigin(origins = "*")
public class FollowController {
        /** Servicio que contiene la lógica para seguir y dejar de seguir usuarios. */
    @Autowired
    private FollowService followService;

        /**
     * Permite que un usuario siga a otro.
     *
     * @param payload mapa con los nombres de usuario de origen y destino
     * @return respuesta indicando si la operación fue exitosa
     */
    @PostMapping("/follow")
    public Map<String, Object> follow(@RequestBody Map<String, String> payload) {
        String from = payload.get("fromUsername");
        String to = payload.get("toUsername");

        boolean success = followService.followUser(from, to);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return response;
    }

            /**
     * Permite que un usuario deje de seguir a otro.
     *
     * @param payload mapa con los nombres de usuario de origen y destino
     * @return respuesta indicando si la operación fue exitosa
     */
    @PostMapping("/unfollow")
    public Map<String, Object> unfollow(@RequestBody Map<String, String> payload) {
        String from = payload.get("fromUsername");
        String to = payload.get("toUsername");

        boolean success = followService.unfollowUser(from, to);
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        return response;
    }
    /**
     * Verifica si un usuario está siguiendo a otro.
     *
     * @param from nombre del usuario que sigue
     * @param to nombre del usuario que es seguido
     * @return respuesta indicando si existe la relación de seguimiento
     */
    @GetMapping("/status")
    public Map<String, Object> isFollowing(
            @RequestParam String from,
            @RequestParam String to) {

        boolean following = followService.isFollowing(from, to);
        Map<String, Object> response = new HashMap<>();
        response.put("following", following);
        return response;
    }

            /**
     * Elimina todas las relaciones de seguimiento asociadas a un usuario por su ID.
     *
     * @param userId ID del usuario
     * @return respuesta indicando si la operación fue exitosa o si ocurrió un error
     */
    @DeleteMapping("/deleteByUserId/{userId}")
    public ResponseEntity<Map<String, Object>> deleteFollowsByUserId(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            followService.deleteAllByUserId(userId);
            response.put("success", true);
            response.put("message", "Follows eliminados correctamente.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar follows: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

