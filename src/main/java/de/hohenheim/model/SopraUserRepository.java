package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SopraUserRepository extends JpaRepository<SopraUser, Integer> {

    List<SopraUser> findByUsername (String username);

}
