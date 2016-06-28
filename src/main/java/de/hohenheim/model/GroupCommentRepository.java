package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;


public interface GroupCommentRepository extends JpaRepository<GroupComment, Integer> {

    GroupComment findByCommentID(Integer commentID);

    //List<GroupComment> findByGroupID(Integer groupID);

}