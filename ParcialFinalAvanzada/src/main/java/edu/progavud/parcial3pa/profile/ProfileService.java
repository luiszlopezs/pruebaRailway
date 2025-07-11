/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.progavud.parcial3pa.profile;

import edu.progavud.parcial3pa.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate;
//    private final RestTemplate restTemplate;

    // URL base del microservicio de usuarios (ajusta si usas gateway o puerto distinto)
//    private final String USER_SERVICE_URL = "http://localhost:8091/auth/users";
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
                .orElseThrow(() -> new RuntimeException("No se encontró el perfil con username: " + updatedProfile.getUser().getUsername()));

        existingProfile.setBio(updatedProfile.getBio());
        existingProfile.setProfilePicture(updatedProfile.getProfilePicture());

        // Actualizar el username del usuario relacionado
        if (updatedProfile.getUser() != null && updatedProfile.getUser().getUsername() != null) {
            existingProfile.getUser().setUsername(updatedProfile.getUser().getUsername());
        }

        return profileRepository.save(existingProfile);
    }

    public Profile findByUsername(String username) {
        // Buscar el perfil existente
        Optional<Profile> existente = profileRepository.findByUsername(username);

        if (existente.isPresent()) {
            return existente.get();
        }

        // Si no existe el perfil, obtener el usuario con RestTemplate
        User user = restTemplate.getForObject("https://auth-service.up.railway.app/auth/by-username/" + username, User.class);

        if (user == null || user.getId() == null) {
            throw new RuntimeException("No se pudo obtener el usuario desde auth-service");
        }

        // Crear perfil nuevo
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

//Elimina perfil, y llama al servicio de Auth para eliminar la cuenta asociada
public void deleteProfileAndUser(String username) {
    Optional<Profile> perfilOpt = profileRepository.findByUsername(username);

    if (perfilOpt.isEmpty()) {
        throw new RuntimeException("Perfil no encontrado");
    }

    Profile perfil = perfilOpt.get();
    Long userId = perfil.getUser().getId();

    // 1. Eliminar perfil localmente
    profileRepository.delete(perfil);
    
    restTemplate.delete("https://auth-service.up.railway.app/follow/deleteByUserId/" + userId);

    // 2. Llamar al auth-service para eliminar la cuenta
    String authServiceUrl = "https://auth-service.up.railway.app/auth/delete/" + userId;
    restTemplate.delete(authServiceUrl);
}




//    /**
//     * Elimina el perfil y hace una llamada REST para eliminar el usuario en
//     * otro microservicio.
//     */
//    public Profile deleteAccount(Profile profile) {
//        Profile existingProfile = profileRepository.findById(profile.getId())
//                .orElseThrow(() -> new RuntimeException("No se encontró el perfil con ID: " + profile.getId()));
//
//        User user = existingProfile.getUser();
//
//        // Eliminar perfil localmente
//        profileRepository.delete(existingProfile);
//
//        // Eliminar usuario por REST si está asociado
//        if (user != null) {
//            try {
//                restTemplate.delete(USER_SERVICE_URL + "/" + user.getId());
//            } catch (Exception e) {
//                // Puedes decidir si lanzar excepción o continuar si el user-service falla
//                throw new RuntimeException("Perfil eliminado, pero error al eliminar usuario: " + e.getMessage());
//            }
//        }
//
//        return existingProfile;
//    }
}
