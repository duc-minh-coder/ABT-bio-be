package com.NMCNPM.ABT_bio.repository;

import com.NMCNPM.ABT_bio.entity.Users;
import com.NMCNPM.ABT_bio.enums.UserStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    boolean existsByContactEmail(String contactEmail);

    boolean existsByContactEmailAndIdNot(String contactEmail, UUID id);

    Optional<Users> findByContactEmail(String contactEmail);

    @Query("""
    SELECT u
    FROM Users u
    JOIN u.identity i
    JOIN u.role r
    WHERE (:email IS NULL OR LOWER(u.contactEmail) LIKE LOWER(CONCAT('%', :email, '%')))
      AND (:status IS NULL OR u.status = :status)
      AND (:role IS NULL OR r = :role)
""")
    Page<Users> searchUsers(
            @Param("email") String email,
            @Param("status") UserStatusEnum status,
            @Param("role") String role,
            Pageable pageable
    );

//    @Query("""
//        SELECT u
//        FROM Users u
//        JOIN u.identity i
//        JOIN u.userRole r
//        WHERE (:status IS NULL OR u.status = :status)
//          AND (:roleCode IS NULL OR r.code = :roleCode)
//    """)
//    Page<Users> searchUsers(
//            @Param("email") String email,
//            @Param("status") UserStatusEnum status,
//            @Param("roleCode") String roleCode,
//            Pageable pageable
//    );


    // 1. Tổng user (tính cả DELETED)
    @Query("SELECT COUNT(u.id) FROM Users u")
    long countAllUsers();

    // 2. Active (đã kích hoạt)
    @Query("""
        SELECT COUNT(u.id)
        FROM Users u
        WHERE u.identity.verified = true
    """)
    long countActivatedUsers();

    // 3. Inactive (chưa kích hoạt)
    @Query("""
        SELECT COUNT(u.id)
        FROM Users u
        WHERE u.identity.verified = false
    """)
    long countInactiveUsers();

    // 4. Banned
    long countByStatus(UserStatusEnum status);

    // 5. Admin
    @Query("""
        SELECT COUNT(u.id)
        FROM Users u
        WHERE u.role = 'ADMIN'
    """)
    long countAdmins();
    ;

    List<Users> findAllByIdentity_Email(String email);
}
