package edu.progavud.parcial3pa.follow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    public boolean followUser(String from, String to) {
        try {
            // Verificar que no se esté siguiendo a sí mismo
            if (from.equals(to)) {
                return false;
            }

            // Verificar que no exista ya la relación
            if (followRepository.existsByFromUsernameAndToUsername(from, to)) {
                return false;
            }

            Follow follow = new Follow();
            follow.setFromUsername(from);
            follow.setToUsername(to);
            followRepository.save(follow);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean unfollowUser(String from, String to) {
        try {
            followRepository.deleteByFromUsernameAndToUsername(from, to);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFollowing(String from, String to) {
        return followRepository.existsByFromUsernameAndToUsername(from, to);
    }

    /**
     * Elimina todos los seguimientos relacionados con un usuario específico
     * Esto incluye:
     * - Seguimientos donde el usuario sigue a otros (fromUserId = userId)
     * - Seguimientos donde otros siguen al usuario (toUserId = userId)
     */
    public int deleteAllByUserId(Long userId) {
        int deletedAsFollower = followRepository.deleteByFromUserId(userId);
        int deletedAsFollowed = followRepository.deleteByToUserId(userId);
        return deletedAsFollower + deletedAsFollowed;
    }

    /**
     * Elimina todos los seguimientos relacionados con un username específico
     * Esto incluye:
     * - Seguimientos donde el usuario sigue a otros (fromUsername = username)
     * - Seguimientos donde otros siguen al usuario (toUsername = username)
     */
    public int deleteAllByUsername(String username) {
        int deletedAsFollower = followRepository.deleteByFromUsername(username);
        int deletedAsFollowed = followRepository.deleteByToUsername(username);
        return deletedAsFollower + deletedAsFollowed;
    }

    /**
     * Obtiene estadísticas de seguimiento para un usuario
     * Útil para verificar qué se va a eliminar antes de hacerlo
     */
    public Map<String, Integer> getFollowStatsByUserId(Long userId) {
        Map<String, Integer> stats = new HashMap<>();
        
        int followingCount = followRepository.countByFromUserId(userId);
        int followersCount = followRepository.countByToUserId(userId);
        
        stats.put("following", followingCount); // Cuántos sigue el usuario
        stats.put("followers", followersCount); // Cuántos lo siguen
        stats.put("total", followingCount + followersCount); // Total de registros que se eliminarán
        
        return stats;
    }

    /**
     * Obtiene estadísticas de seguimiento para un username
     */
    public Map<String, Integer> getFollowStatsByUsername(String username) {
        Map<String, Integer> stats = new HashMap<>();
        
        int followingCount = followRepository.countByFromUsername(username);
        int followersCount = followRepository.countByToUsername(username);
        
        stats.put("following", followingCount);
        stats.put("followers", followersCount);
        stats.put("total", followingCount + followersCount);
        
        return stats;
    }
}