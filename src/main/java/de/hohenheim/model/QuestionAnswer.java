package de.hohenheim.model;


import javax.persistence.*;


@Entity
public class QuestionAnswer {

    @Id
    @GeneratedValue
    Integer answerID;

    public boolean isMpcAnswer() {
        return mpcAnswer;
    }

    public void setMpcAnswer(boolean mpcAnswer) {
        this.mpcAnswer = mpcAnswer;
    }

    boolean mpcAnswer;

    public TextQuestion getTextQuestion() {
        return textQuestion;
    }

    public void setTextQuestion(TextQuestion textQuestion) {
        this.textQuestion = textQuestion;
    }

    public MPCQuestion getMpcQuestion() {
        return mpcQuestion;
    }

    public void setMpcQuestion(MPCQuestion mpcQuestion) {
        this.mpcQuestion = mpcQuestion;
    }

    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    String questionAnswer;

    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    @ManyToOne
    Lobby lobby;

    @ManyToOne
    TextQuestion textQuestion;

    @ManyToOne
    MPCQuestion mpcQuestion;

    @ManyToOne
    SopraUser student;

    boolean validated = false;

    boolean correct = false;

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Integer getId() { return answerID; }

    public SopraUser getStudent() { return student; }

    public void setStudent(SopraUser user) {
        student = user; }

    public void setId(Integer id) { this.answerID = id; }


    // Überprüft ob die 2 zu überprüfenden Lerngruppen die gleichen sind
    @Override
    public boolean equals(Object o){
        if(this.answerID == null){
            return false;
        }else if(o == null){
            return false;
        }else if(o == this){
            return true;
        }else if(!(o instanceof QuestionAnswer)){
            return false;
        }
        QuestionAnswer other = (QuestionAnswer)o;
        if(this.answerID.equals(other.answerID)){
            return true;
        }else{
            return false;
        }
    }
}