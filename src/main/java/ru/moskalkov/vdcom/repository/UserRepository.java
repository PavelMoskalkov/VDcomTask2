package ru.moskalkov.vdcom.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.lang.NonNull;
import ru.moskalkov.vdcom.entity.User;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    @NonNull
    List<User> findAll();

    boolean existsByEmail(String email);
}
