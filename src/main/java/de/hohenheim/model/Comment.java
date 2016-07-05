package de.hohenheim.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cedrik on 26.06.2016.
 */
@Entity
public class Comment {

    @Id
    @GeneratedValue
    Integer commentID;

    String title;

    String content;

    Long creationTimeStamp;

    public Comment(){
        creationTimeStamp = System.currentTimeMillis();
    }

    public Long getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public void setCreationTimeStamp(Long creationTimeStamp) {
        this.creationTimeStamp = creationTimeStamp;
    }

    public String printTimeStamp(){
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yy HH:mm");
        return ft.format(new Date(creationTimeStamp));
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCommentID() {
        return commentID;
    }

    public void setCommentID(Integer commentID) {
        this.commentID = commentID;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }else if(o == this){
            return true;
        }else if(!(o instanceof GroupComment)){
            return false;
        }
        GroupComment other = (GroupComment)o;
        if(this.getCommentID().equals(other.getCommentID())){
            return true;
        }else{
            return false;
        }
    }
}
