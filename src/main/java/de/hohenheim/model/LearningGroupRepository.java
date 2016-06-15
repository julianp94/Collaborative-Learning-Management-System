package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LearningGroupRepository extends JpaRepository<LearningGroup, Integer> {

    LearningGroup findByGroupId (Integer groupId);

}