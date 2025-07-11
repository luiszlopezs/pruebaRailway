package edu.progavud.parcial3pa.follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    // Métodos para trabajar con usernames
    boolean existsByFromUsernameAndToUsername(String fromUsername, String toUsername);
    
    @Modifying
    @Transactional
    void deleteByFromUsernameAndToUsername(String fromUsername, String toUsername);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.fromUsername = :username")
    int deleteByFromUsername(@Param("username") String username);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.toUsername = :username")
    int deleteByToUsername(@Param("username") String username);
    
    // Métodos para contar por username
    int countByFromUsername(String fromUsername);
    int countByToUsername(String toUsername);

    // Métodos para trabajar con userIds 
    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.fromUserId = :userId")
    int deleteByFromUserId(@Param("userId") Long userId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.toUserId = :userId")
    int deleteByToUserId(@Param("userId") Long userId);
    
    // Métodos para contar por userId
    int countByFromUserId(Long fromUserId);
    int countByToUserId(Long toUserId);

    // Método combinado para eliminar todo lo relacionado con un usuario
    @Modifying
    @Transactional
    @Query("DELETE FROM Follow f WHERE f.fromUserId = :userId OR f.toUserId = :userId")
    int deleteAllByUserId(@Param("userId") Long userId);
}