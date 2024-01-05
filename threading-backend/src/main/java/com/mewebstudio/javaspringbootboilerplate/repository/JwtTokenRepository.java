package com.mewebstudio.javaspringbootboilerplate.repository;

import com.mewebstudio.javaspringbootboilerplate.entity.JwtToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
// import java.util.UUID;

// public interface JwtTokenRepository extends CrudRepository<JwtToken, UUID> {
public interface JwtTokenRepository extends CrudRepository<JwtToken, Long> {
    Optional<JwtToken> findByTokenOrRefreshToken(String token, String refreshToken);

    // Optional<JwtToken> findByUserIdAndRefreshToken(UUID id, String refreshToken);
    Optional<JwtToken> findByUserIdAndRefreshToken(Long id, String refreshToken);
}
