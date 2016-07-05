package de.hohenheim.model;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Lobby {

    @Id
    @GeneratedValue
    Integer lobbyID;

    public boolean isMpcTest() {
        return mpcTest;
    }

    public void setMpcTest(boolean mpcTest) {
        this.mpcTest = mpcTest;
    }

    boolean mpcTest = true;

    @ManyToMany
    @JoinTable(
                name="LOBBYMEMBER",
                joinColumns=@JoinColumn(name="LOBBY_ID"),
                inverseJoinColumns=@JoinColumn(name="USER_ID"))
    List<SopraUser> users = new ArrayList<>();

    public List<MPCQuestion> getMpcQuestions() {
        return mpcQuestions;
    }

    public void setMpcQuestions(List<MPCQuestion> mpcQuestions) {
        this.mpcQuestions = mpcQuestions;
    }

    public void addMpcQuestions(MPCQuestion mpcQuestion) {
       mpcQuestions.add(mpcQuestion);
    }

    public List<QuestionAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswer> answers) {
        this.answers = answers;
    }

    public void addAnswers(QuestionAnswer answer) {
        answers.add(answer);
    }

    public List<TextQuestion> getTextQuestions() {
        return textQuestions;
    }

    public void setTextQuestions(List<TextQuestion> textQuestions) {
        this.textQuestions = textQuestions;
    }

    public void addTextQuestions(TextQuestion textQuestion) {
        textQuestions.add(textQuestion);
    }

    @ManyToMany
    @JoinTable(
            name="QUIZANSWERS",
            joinColumns=@JoinColumn(name="LOBBY_ID"),
            inverseJoinColumns=@JoinColumn(name="ANSWER_ID"))
    List<QuestionAnswer> answers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name="QUIZMPC",
            joinColumns=@JoinColumn(name="LOBBY_ID"),
            inverseJoinColumns=@JoinColumn(name="QUESTION_ID"))
    List<MPCQuestion> mpcQuestions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name="QUIZTEXT",
            joinColumns=@JoinColumn(name="LOBBY_ID"),
            inverseJoinColumns=@JoinColumn(name="QUESTION_ID"))
    List<TextQuestion> textQuestions = new ArrayList<>();


    @ManyToOne
    SopraUser adminUser;

    public LearningGroup getLearningGroup() {
        return learningGroup;
    }

    public void setLearningGroup(LearningGroup learningGroup) {
        this.learningGroup = learningGroup;
    }

    @ManyToOne
    LearningGroup learningGroup;

    public Integer getID() { return lobbyID; }

    public SopraUser getAdminUser() { return adminUser; }

    public void setAdminUser(SopraUser user) {
        if(!users.contains(user)){
           users.add(user);
        }
        adminUser = user; }

    public void setId(Integer id) { this.lobbyID = id; }

    public List<SopraUser> getUsers() { return users; }

    public void setUsers(List<SopraUser> users) { this.users = users; }

    // Überprüft ob die 2 zu überprüfenden Lerngruppen die gleichen sind
    @Override
    public boolean equals(Object o){
        if(this.lobbyID == null){
            return false;
        }else if(o == null){
            return false;
        }else if(o == this){
            return true;
        }else if(!(o instanceof Lobby)){
            return false;
        }
        Lobby other = (Lobby)o;
        if(this.lobbyID.equals(other.lobbyID)){
            return true;
        }else{
            return false;
        }
    }
}