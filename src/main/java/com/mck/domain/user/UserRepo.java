package com.mck.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface UserRepo extends JpaRepository<User, Long> {

    // by gh
    User findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
    boolean existsByEmail(String email);

    // by ty
//    Optional<User> findById(Long user_id);
    Optional<User> findByEmail(String email);
//    Optional<User> findByUserName(String userName);
//
//    @Modifying
//    @Query(value = "UPDATE users u SET u.nickname = :nickname, u.password = :password WHERE u.id = :id", nativeQuery = true)
//    void editUser(@Param("nickname") String nickname, @Param("password") String password, @Param("id") Long user_id);

}
