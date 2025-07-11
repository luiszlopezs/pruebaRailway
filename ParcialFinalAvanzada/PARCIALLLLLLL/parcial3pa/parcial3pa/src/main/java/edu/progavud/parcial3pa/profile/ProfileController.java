/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.profile;

import edu.progavud.parcial3pa.auth.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author hailen
 */
@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }
//
//@PostMapping("/update")
//public ResponseEntity<Map<String, Object>> updateProfile(
//        @RequestParam("username") String username,
//        @RequestParam("bio") String bio,
//        @RequestParam(value = "file", required = false) MultipartFile file
//) {
//    Map<String, Object> response = new HashMap<>();
//
//    try {
//        // Obtener el perfil actual por username
//        Profile existingProfile = profileService.findByUsername(username);
//
//        existingProfile.setBio(bio);
//
//        // Guardar imagen si se envía
//        if (file != null && !file.isEmpty()) {
//            // Obtener la ruta absoluta al directorio dentro del proyecto
//            String uploadDir = new File("postsImgs/profile_pictures/").getAbsolutePath();
//
//            // Crear nombre único para el archivo
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//            // Crear archivo y carpetas si no existen
//            File dest = new File(uploadDir, fileName);
//            dest.getParentFile().mkdirs(); // crea carpetas necesarias
//            file.transferTo(dest); // guardar archivo
//
//            // Guardar la ruta relativa accesible desde el navegador
//            existingProfile.setProfilePicture("/profile/postsImgs/profile_pictures/" + fileName);
//        }
//
//        // También puedes actualizar el username del user si lo permites
//        existingProfile.getUser().setUsername(username);
//
//        Profile updated = profileService.updateProfile(existingProfile);
//
//        response.put("success", true);
//        response.put("message", "Perfil actualizado correctamente");
//        response.put("profile", updated);
//        return ResponseEntity.ok(response);
//
//    } catch (Exception e) {
//        response.put("success", false);
//        response.put("message", "Error al actualizar: " + e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//    }
//}
    
    @PostMapping("/update")
public ResponseEntity<Map<String, Object>> updateProfile(
        @RequestParam("username") String username,
        @RequestParam("bio") String bio,
        @RequestParam(value = "profilePicture", required = false) String profilePictureUrl
) {
    Map<String, Object> response = new HashMap<>();

    try {
        Profile existingProfile = profileService.findByUsername(username);
        existingProfile.setBio(bio);

        // Asignar link de imagen si se proporcionó
        if (profilePictureUrl != null && !profilePictureUrl.isBlank()) {
            existingProfile.setProfilePicture(profilePictureUrl);
        }

        // También puedes actualizar el username del user si lo permites
        existingProfile.getUser().setUsername(username);

        Profile updated = profileService.updateProfile(existingProfile);

        response.put("success", true);
        response.put("message", "Perfil actualizado correctamente");
        response.put("profile", updated);
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "Error al actualizar: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}



    @GetMapping("/{username}")
    public ResponseEntity<Profile> getProfile(@PathVariable String username) {
        Profile profile = profileService.findByUsername(username);

        return ResponseEntity.ok(profile);
    }

@GetMapping("/postsImgs/profile_pictures/{filename:.+}")
public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
    Path imagePath = Paths.get("postsImgs/profile_pictures/" + filename); // ruta relativa al proyecto
    Resource resource = new UrlResource(imagePath.toUri());

    if (!resource.exists()) {
        return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok()
            .contentType(MediaType.IMAGE_JPEG) // o usa Files.probeContentType(imagePath)
            .body(resource);
}

@PostMapping("/increment-follow")
public void incrementarContadores(@RequestBody Map<String, String> payload) {
    String follower = payload.get("follower");
    String followed = payload.get("followed");
    profileService.incrementCounters(follower, followed);
}

@PostMapping("/decrement-follow")
public void decrementarContadores(@RequestBody Map<String, String> payload) {
    String follower = payload.get("follower");
    String followed = payload.get("followed");
    profileService.decrementCounters(follower, followed);
}

@DeleteMapping("/delete/{username}")
public ResponseEntity<Map<String, Object>> eliminarPerfil(@PathVariable String username) {
    Map<String, Object> response = new HashMap<>();
    try {
        profileService.deleteProfileAndUser(username);
        response.put("success", true);
        response.put("message", "Perfil y cuenta eliminados correctamente.");
        return ResponseEntity.ok(response);
    } catch (Exception e) {
        response.put("success", false);
        response.put("message", "Error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}








}
