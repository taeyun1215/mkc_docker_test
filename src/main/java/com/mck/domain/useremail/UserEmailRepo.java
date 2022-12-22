package com.mck.domain.useremail;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserEmailRepo extends CrudRepository<UserEmail, String> {
    Optional<UserEmail> findByEmail(String email);
}
