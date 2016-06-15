package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Comment findByCommentID(Integer commentID);

    //List<Comment> findByGroupID(Integer groupID);

}