package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningGroupRepository extends JpaRepository<LearningGroup, Integer> {

    LearningGroup findByGroupId (Integer groupId);

}