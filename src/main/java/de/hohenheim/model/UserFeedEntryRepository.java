package de.hohenheim.model;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserFeedEntryRepository extends JpaRepository<UserFeedEntry, Integer> {

    UserFeedEntry findByCommentID(Integer commentID);

}