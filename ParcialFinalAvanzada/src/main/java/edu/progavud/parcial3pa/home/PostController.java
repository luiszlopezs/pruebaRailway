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

@RestController
@RequestMapping("/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

//    @PostMapping("/upload")
//    public ResponseEntity<?> uploadPost(
//            @RequestParam("userId") Long userId,
//            @RequestParam("file") MultipartFile file,
//            @RequestParam("description") String description
//    ) {
//        Map<String, Object> response = postService.uploadPost(userId, file, description);
//        return response.get("success").equals(true)
//                ? ResponseEntity.ok(response)
//                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//    }

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

//    @GetMapping
//    public ResponseEntity<List<Post>> getAllPosts() {
//        return ResponseEntity.ok(postService.getAllPosts());
//    }
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllPosts(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(postService.getAllPosts(userId));
    }

    @GetMapping("/galleryPost")
    public List<Post> obtenerPosts(@RequestParam(required = false) Long userId) {
        return postService.obtenerPostsPorUsuario(userId);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id, @RequestParam Long userId) {
        return postService.toggleLike(id, userId);
    }

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
}
