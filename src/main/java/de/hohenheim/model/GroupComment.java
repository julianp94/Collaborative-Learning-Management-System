package de.hohenheim.model;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class GroupComment extends Comment{

    Boolean subComment;

    @OneToMany(cascade = CascadeType.ALL , fetch = FetchType.LAZY)
    @JoinColumn(name = "parentCommentID", nullable = true, foreignKey = @ForeignKey(name="commentID"))
    List<GroupComment> subComments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "id", nullable = true, foreignKey = @ForeignKey(name="id"))
    SopraUser author;

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = true, foreignKey = @ForeignKey(name="groupId"))
    LearningGroup group;

    @ManyToMany
    @JoinTable(
            name="COMMENTLIKES",
            joinColumns=@JoinColumn(name="COMMENT_ID"),
            inverseJoinColumns=@JoinColumn(name="USER_ID"))
    List<SopraUser> likes = new ArrayList<>();

    public List<SopraUser> getLikes() {
        return likes;
    }

    public void setLikes(List<SopraUser> likes) {
        this.likes = likes;
    }

    public void addLike(SopraUser user){
        if(!likes.contains(user))
            likes.add(user);
    }

    public void removeLike(SopraUser user){
        if(likes.contains(user))
            likes.remove(user);
    }

    public LearningGroup getGroup() {
        return group;
    }

    public void setGroup(LearningGroup group) {
        this.group = group;
    }

    public SopraUser getAuthor() {
        return author;
    }

    public void setAuthor(SopraUser author) {
        this.author = author;
    }

    public List<GroupComment> getSubComments() {
        return subComments;
    }

    public void setSubComments(List<GroupComment> subComments) {
        this.subComments = subComments;
    }

    public Boolean getSubComment() { return subComment; }

    public void setSubComment(Boolean subComment) {
        this.subComment = subComment;
    }

}
