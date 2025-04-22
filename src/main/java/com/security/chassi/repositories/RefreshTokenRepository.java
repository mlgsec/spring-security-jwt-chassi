package com.security.chassi.repositories;

import com.security.chassi.services.RefreshToken;
import com.security.chassi.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken t WHERE t.expiryDate <= ?1")
    void deleteAllExpiredSince(LocalDateTime now);
}
