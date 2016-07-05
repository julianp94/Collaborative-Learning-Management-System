package de.hohenheim.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Entity
public class MPCQuestion {

    @Id
    @GeneratedValue
    Integer questionID;

    String questionTopic = "";

    String questionText;

    @ManyToOne
    SopraUser adminUser;

    public Integer getId() { return questionID; }

    public SopraUser getAdminUser() { return adminUser; }

    public void setAdminUser(SopraUser user) {
        adminUser = user; }

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
        }else if(!(o instanceof MPCQuestion)){
            return false;
        }
        MPCQuestion other = (MPCQuestion)o;
        if(this.questionID.equals(other.questionID)){
            return true;
        }else{
            return false;
        }
    }

    public String[] getQuestionAnswers() {
        return questionAnswers;
    }

    public List<String> getQuestionAnswersAsList() {
        List<String> result = new ArrayList<String>(Arrays.asList(questionAnswers));
        Collections.shuffle(result);
        return result;
    }

    public void addQuestionAnswer(String answer){
        List<String> questionAnswerList = new ArrayList<String>(Arrays.asList(questionAnswers));
        if(!questionAnswerList.contains(answer))
            questionAnswerList.add(answer);
        questionAnswers = questionAnswerList.toArray(new String[questionAnswerList.size()]);
    }

    public void setQuestionAnswers(String[] questionAnswers) {
        this.questionAnswers = questionAnswers;
    }

    String[] questionAnswers = new String[0];
}