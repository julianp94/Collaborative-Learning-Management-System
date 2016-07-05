package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MPCQuestionRepository extends JpaRepository<MPCQuestion, Integer> {

   MPCQuestion findByQuestionID(Integer questionID);

}