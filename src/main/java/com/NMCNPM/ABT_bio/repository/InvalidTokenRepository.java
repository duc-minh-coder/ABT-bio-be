package com.NMCNPM.ABT_bio.repository;


import com.NMCNPM.ABT_bio.entity.InvalidatedToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidatedToken, String> {
    @Transactional
    int deleteByExpiryTimeBefore(Date now);

    @Modifying
    @Query(value = """
        INSERT INTO invalidated_token (id, expiry_time)
        VALUES (:id, :expiryTime)
        ON CONFLICT (id) DO NOTHING
    """, nativeQuery = true)
    void insertIgnore(@Param("id") String id,
                      @Param("expiryTime") Date expiryTime);

    @Modifying
    @Query("DELETE FROM InvalidatedToken i WHERE i.expiryTime < :currentTime")
    void deleteAllExpiredSince(Date currentTime);
}
