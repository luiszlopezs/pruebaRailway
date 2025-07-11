/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.home;

import edu.progavud.parcial3pa.auth.User;
import edu.progavud.parcial3pa.home.Post;
import edu.progavud.parcial3pa.home.PostService;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST encargado de manejar las operaciones relacionadas con las
 * publicaciones de los usuarios.
 */
@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {

    /**
     * Servicio que contiene la lógica relacionada con las publicaciones.
     */
    @Autowired
    private PostService postService;

    /**
     * Crea una nueva publicación usando un enlace de imagen.
     *
     * @param userId ID del usuario que crea la publicación
     * @param imageUrl URL de la imagen que se desea publicar
     * @param description descripción de la publicación
     * @return respuesta indicando si la operación fue exitosa o si ocurrió un
     * error
     */
    @PostMapping("/upload-link")
    public ResponseEntity<?> uploadPostDesdeLink(@RequestParam Long userId,
            @RequestParam String imageUrl,
            @RequestParam String description) {
        try {
            postService.crearPostDesdeLink(userId, imageUrl, description);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error al subir post"));
        }
    }

    /**
     * Obtiene todas las publicaciones, opcionalmente filtradas por usuario.
     *
     * @param userId ID del usuario (opcional)
     * @return lista de publicaciones en formato de mapa
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPosts(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(postService.getAllPosts(userId));
    }

    /**
     * Obtiene las publicaciones en formato de galería, opcionalmente filtradas
     * por usuario.
     *
     * @param userId ID del usuario (opcional)
     * @return lista de publicaciones
     */
    @GetMapping("/galleryPost")
    public List<Post> obtenerPosts(@RequestParam(required = false) Long userId) {
        return postService.obtenerPostsPorUsuario(userId);
    }

    /**
     * Alterna el estado de "me gusta" para una publicación por parte de un
     * usuario.
     *
     * @param id ID de la publicación
     * @param userId ID del usuario que da o quita el "me gusta"
     * @return respuesta con el estado actualizado del "me gusta"
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id, @RequestParam Long userId) {
        return postService.toggleLike(id, userId);
    }

    /**
     * Agrega un comentario a una publicación específica.
     *
     * @param postId ID de la publicación que se va a comentar
     * @param payload mapa que contiene el ID del usuario y el texto del
     * comentario
     * @return respuesta indicando si la operación fue exitosa o si ocurrió un
     * error
     */
    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> commentPost(
            @PathVariable Long postId,
            @RequestBody Map<String, String> payload
    ) {
        try {
            Long userId = Long.parseLong(payload.get("userId"));
            String text = payload.get("text");

            Map<String, Object> response = postService.commentPost(postId, userId, text);
            return response.get("success").equals(true)
                    ? ResponseEntity.ok(response)
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Datos inválidos o incompletos"));
        }
    }

    @DeleteMapping("/delete-likes/{userId}")
    public ResponseEntity<Void> deleteLikesByUser(@PathVariable Long userId) {
        postService.deleteLikesByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete-comments/{userId}")
    public ResponseEntity<Void> deleteCommentsByUser(@PathVariable Long userId) {
        postService.deleteCommentsByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-posts/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable Long userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/delete-comment-by-post/{postId}")
    public ResponseEntity<Void> deleteCommentsByPost(@PathVariable Long postId) {
        postService.deleteCommentsByPostId(postId);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    @DeleteMapping("/delete-like-by-post/{postId}")
    public ResponseEntity<Void> deleteLikesByPost(@PathVariable Long postId) {
        postService.deleteLikesByPostId(postId);
        return ResponseEntity.noContent().build(); // HTTP 204
    }

    @DeleteMapping("/delete-posts-by-userId/{userId}")
    public ResponseEntity<Void> deletePostsByUserId(@PathVariable Long userId) {
        postService.deletePostsByUserId(userId);
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }
}
