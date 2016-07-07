package de.hohenheim.model;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class TextQuestion {

    @Id
    @GeneratedValue
    Integer questionID;

    String questionTopic = "";

    String questionText;

    @ManyToOne
    SopraUser adminUser;

    @ManyToMany
    @JoinTable(
            name="TESTQUIZLIKES",
            joinColumns=@JoinColumn(name="QUIZ_ID"),
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

    public Integer getId() { return questionID; }

    public SopraUser getAdminUser() { return adminUser; }

    public void setAdminUser(SopraUser user) {
        if(!questionEditors.contains(user))
            questionEditors.add(user);
        adminUser = user;
    }

    public void setId(Integer id) { this.questionID = id; }

    public String getQuestionTopic() {
        return questionTopic;
    }

    public void setQuestionTopic(String questionTopic) {
        this.questionTopic = questionTopic;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    // Überprüft ob die 2 zu überprüfenden Lerngruppen die gleichen sind
    @Override
    public boolean equals(Object o){
        if(this.questionID == null){
            return false;
        }else if(o == null){
            return false;
        }else if(o == this){
            return true;
        }else if(!(o instanceof TextQuestion)){
            return false;
        }
        TextQuestion other = (TextQuestion)o;
        if(this.questionID.equals(other.questionID)){
            return true;
        }else{
            return false;
        }
    }

    public List<SopraUser> getQuestionEditors() {
        return questionEditors;
    }

    public void setQuestionEditors(List<SopraUser> questionEditors) {
        this.questionEditors = questionEditors;
    }

    public void addQuestionEditors(SopraUser questionEditor) {
        if(!questionEditors.contains(questionEditor))
            questionEditors.add(questionEditor);
    }

    @ManyToMany
    @JoinTable(
            name="QUESTIONMEMBERS",
            joinColumns=@JoinColumn(name="QUESTION_ID"),
            inverseJoinColumns=@JoinColumn(name="USER_ID"))
    List<SopraUser> questionEditors = new ArrayList<>();

}