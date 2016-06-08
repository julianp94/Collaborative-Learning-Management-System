package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Julian on 04.06.2016.
 */
public interface UserRepository extends JpaRepository<User, Integer> {
}
