package de.hohenheim.model;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Comment {

    @Id
    @GeneratedValue
    Integer commentID;

    String title;

    String content;

    Boolean subComment;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCommentID", nullable = true, foreignKey = @ForeignKey(name="commentID"))
    List<Comment> subComments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id", nullable = true, foreignKey = @ForeignKey(name="id"))
    SopraUser author;

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = true, foreignKey = @ForeignKey(name="groupId"))
    LearningGroup group;

    public LearningGroup getGroup() {
        return group;
    }

    public void setGroup(LearningGroup group) {
        this.group = group;
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

    public SopraUser getAuthor() {
        return author;
    }

    public void setAuthor(SopraUser author) {
        this.author = author;
    }

    public List<Comment> getSubComments() {
        return subComments;
    }

    public void setSubComments(List<Comment> subComments) {
        this.subComments = subComments;
    }

    public Boolean getSubComment() { return subComment; }

    public void setSubComment(Boolean subComment) {
        this.subComment = subComment;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }else if(o == this){
            return true;
        }else if(!(o instanceof Comment)){
            return false;
        }
        Comment other = (Comment)o;
        if(this.getCommentID().equals(other.getCommentID())){
            return true;
        }else{
            return false;
        }
    }
}
