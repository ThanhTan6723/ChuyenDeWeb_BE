package org.example.chuyendeweb_be.user.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository {
    void invalidate(String refreshToken);
    boolean isValid(String refreshToken);
}