package edu.progavud.parcial3pa.follow;

import edu.progavud.parcial3pa.profile.ProfileService;
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
    public ResponseEntity<Map<String, Object>> follow(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String from = payload.get("fromUsername");
            String to = payload.get("toUsername");
            
            System.out.println("Follow request: from=" + from + ", to=" + to);
            
            // Validar parámetros
            if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Nombres de usuario no válidos");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (from.equals(to)) {
                response.put("success", false);
                response.put("message", "Un usuario no puede seguirse a sí mismo");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean success = followService.followUser(from, to);
            
            response.put("success", success);
            if (success) {
                response.put("message", "Usuario seguido exitosamente");
            } else {
                response.put("message", "Error al seguir usuario");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error en follow controller: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Permite que un usuario deje de seguir a otro.
     *
     * @param payload mapa con los nombres de usuario de origen y destino
     * @return respuesta indicando si la operación fue exitosa
     */
    @PostMapping("/unfollow")
    public ResponseEntity<Map<String, Object>> unfollow(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String from = payload.get("fromUsername");
            String to = payload.get("toUsername");
            
            System.out.println("Unfollow request: from=" + from + ", to=" + to);
            
            // Validar parámetros
            if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Nombres de usuario no válidos");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (from.equals(to)) {
                response.put("success", false);
                response.put("message", "Un usuario no puede dejar de seguirse a sí mismo");
                return ResponseEntity.badRequest().body(response);
            }
            
            boolean success = followService.unfollowUser(from, to);
            
            response.put("success", success);
            if (success) {
                response.put("message", "Usuario dejado de seguir exitosamente");
            } else {
                response.put("message", "Error al dejar de seguir usuario");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error en unfollow controller: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Verifica si un usuario está siguiendo a otro.
     *
     * @param from nombre del usuario que sigue
     * @param to nombre del usuario que es seguido
     * @return respuesta indicando si existe la relación de seguimiento
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> isFollowing(
            @RequestParam String from,
            @RequestParam String to) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validar parámetros
            if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
                response.put("following", false);
                response.put("message", "Nombres de usuario no válidos");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (from.equals(to)) {
                response.put("following", false);
                response.put("message", "Un usuario no se sigue a sí mismo");
                return ResponseEntity.ok(response);
            }
            
            boolean following = followService.isFollowing(from, to);
            response.put("following", following);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error verificando seguimiento: " + e.getMessage());
            e.printStackTrace();
            
            response.put("following", false);
            response.put("message", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
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
            if (userId == null || userId <= 0) {
                response.put("success", false);
                response.put("message", "ID de usuario no válido");
                return ResponseEntity.badRequest().body(response);
            }
            
            followService.deleteAllByUserId(userId);
            
            response.put("success", true);
            response.put("message", "Follows eliminados correctamente.");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error eliminando follows por userId: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "Error al eliminar follows: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}