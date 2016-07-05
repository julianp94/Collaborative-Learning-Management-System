package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextQuestionRepository extends JpaRepository<TextQuestion, Integer> {

    TextQuestion findByQuestionID(Integer questionID);

}