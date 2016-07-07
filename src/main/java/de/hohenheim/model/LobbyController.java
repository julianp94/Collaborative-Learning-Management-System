package de.hohenheim.model;

import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
//@EnableWebMvc
public class LobbyController extends WebMvcConfigurerAdapter {

    @Autowired
    private LearningGroupRepository learningGroupRepository;

    @Autowired
    private SopraUserRepository userRepository;

    @Autowired
    private UserFeedEntryRepository userFeedRepository;

    @Autowired
    private MPCQuestionRepository mpcQuestionRepository;

    @Autowired
    private TextQuestionRepository textQuestionRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    private SopraUser getCurrentUser() {
        String userName = ((User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getUsername();
        List<SopraUser> currentUser = userRepository.findByUsername(userName);
        if(currentUser == null || currentUser.size() < 1){
            throw new RuntimeException("Unknown User logged in!");
        }
        return currentUser.get(0);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @RequestMapping(value = "/questionCreationPage", method = RequestMethod.POST)
    public String questionCreationPage(@RequestParam(value="topic", required = true) String topic, Model model){
        model.addAttribute("Topic", topic);
        return "questionCreation";
    }

    @RequestMapping(value = "/quizValidation")
    public String quizValidationList(Model model){
        QuestionAnswer unvalidatedAnswer = null;
        for(QuestionAnswer questionAnswer: questionAnswerRepository.findAll()){
            if(!questionAnswer.isMpcAnswer() && !questionAnswer.isValidated() && questionAnswer.getTextQuestion().getQuestionEditors().contains(getCurrentUser())){
                unvalidatedAnswer = questionAnswer;
                break;
            }
        }
        if(unvalidatedAnswer == null){
            return "redirect:/home";
        }
        model.addAttribute("unvalidatedAnswer", unvalidatedAnswer);
        return "textQuizValidation";
    }

    @RequestMapping(value = "/quizValidationResult")
    public String quizValidationResult(@RequestParam(value="answerID", required = true) String answerID,@RequestParam(value="answerCorrect", required = false, defaultValue="false") String answerCorrect){
        QuestionAnswer currentAnswer = questionAnswerRepository.findByAnswerID(Integer.parseInt(answerID));
        if(currentAnswer.getTextQuestion().getQuestionEditors().contains(getCurrentUser())){
            currentAnswer.setCorrect(Boolean.parseBoolean(answerCorrect));
            currentAnswer.setValidated(true);
            questionAnswerRepository.save(currentAnswer);
            if(Boolean.parseBoolean(answerCorrect)){
                currentAnswer.getStudent().addRankingPoints(1);
                userRepository.save(currentAnswer.getStudent());
            }
        }
        return "redirect:/quizValidation";
    }

    @RequestMapping(value = "/questionList")
    public String questionList(@RequestParam(value="topic", required = true) String topic, Model model){
        model.addAttribute("Topic", topic);
        List<MPCQuestion> mpcQuestions = new ArrayList<>();
        for(MPCQuestion question: mpcQuestionRepository.findAll()){
            if(topic.equals(question.getQuestionTopic())){
                mpcQuestions.add(question);
            }
        }
        List<TextQuestion> textQuestions = new ArrayList<>();
        for(TextQuestion question: textQuestionRepository.findAll()){
            if(topic.equals(question.getQuestionTopic())){
                textQuestions.add(question);
            }
        }
        model.addAttribute("mpcQuestions",mpcQuestions);
        model.addAttribute("textQuestions",textQuestions);
        model.addAttribute("currentUser",getCurrentUser());
        return "questions";
    }

    @RequestMapping(value = "/questionRemove", method = RequestMethod.POST)
    public String questionRemove(@RequestParam(value="questionID", required = true) String questionID, @RequestParam(value="questionType", required = false, defaultValue = "MPC") String questionType){

        String topic = "";
        if(questionType.equals("MPC")){
            MPCQuestion question = mpcQuestionRepository.findByQuestionID(Integer.parseInt(questionID));
            topic = question.getQuestionTopic();
            if(question.getAdminUser().equals(getCurrentUser())) {
                for(QuestionAnswer answer: questionAnswerRepository.findAll()){
                    if(question.equals(answer.getMpcQuestion())){
                        answer.getLobby().removeAnswers(answer);
                        lobbyRepository.save(answer.getLobby());
                        questionAnswerRepository.delete(answer.getId());
                    }
                }
                for(Lobby lobby: lobbyRepository.findAll()){
                    lobby.removeMpcQuestions(question);
                }
                question.setLikes(new ArrayList<>());
                mpcQuestionRepository.save(question);
                mpcQuestionRepository.delete(Integer.parseInt(questionID));
            }
        }else{
            TextQuestion question = textQuestionRepository.findByQuestionID(Integer.parseInt(questionID));
            topic = question.getQuestionTopic();
            if(question.getAdminUser().equals(getCurrentUser())) {
                for(QuestionAnswer answer: questionAnswerRepository.findAll()){
                    if(question.equals(answer.getTextQuestion())){
                        answer.getLobby().removeAnswers(answer);
                        lobbyRepository.save(answer.getLobby());
                        questionAnswerRepository.delete(answer.getId());
                    }
                }
                for(Lobby lobby: lobbyRepository.findAll()){
                    lobby.removeTextQuestions(question);
                }
                question.setLikes(new ArrayList<>());
                textQuestionRepository.save(question);
                textQuestionRepository.delete(Integer.parseInt(questionID));
            }
        }
        return "redirect:/questionList?topic="+topic;
    }

    @RequestMapping(value = "/questionLike")
    public String questionLike(@RequestParam(value="questionID", required = true) String questionID, @RequestParam(value="questionType", required = false, defaultValue = "MPC") String questionType){
        String topic = "";
        if(questionType.equals("MPC")){
            MPCQuestion question = mpcQuestionRepository.findByQuestionID(Integer.parseInt(questionID));
            question.addLike(getCurrentUser());
            topic = question.getQuestionTopic();
            mpcQuestionRepository.save(question);
        }else{
            TextQuestion question = textQuestionRepository.findByQuestionID(Integer.parseInt(questionID));
            question.addLike(getCurrentUser());
            topic = question.getQuestionTopic();
            textQuestionRepository.save(question);
        }
        return "redirect:/questionList?topic="+topic;
    }

    @RequestMapping(value = "/questionUnlike")
    public String questionUnlike(@RequestParam(value="questionID", required = true) String questionID, @RequestParam(value="questionType", required = false, defaultValue = "MPC") String questionType){
        String topic = "";
        if(questionType.equals("MPC")){
            MPCQuestion question = mpcQuestionRepository.findByQuestionID(Integer.parseInt(questionID));
            question.removeLike(getCurrentUser());
            topic = question.getQuestionTopic();
            mpcQuestionRepository.save(question);
        }else{
            TextQuestion question = textQuestionRepository.findByQuestionID(Integer.parseInt(questionID));
            question.removeLike(getCurrentUser());
            topic = question.getQuestionTopic();
            textQuestionRepository.save(question);
        }
        return "redirect:/questionList?topic="+topic;
    }

    @RequestMapping(value = "/quiz", method = RequestMethod.POST)
    public String quiz(@RequestParam(value="lobbyID", required = true) String lobbyID, Model model){
        Lobby currentLobby = lobbyRepository.findByLobbyID(Integer.parseInt(lobbyID));
        SopraUser currentUser = getCurrentUser();
        if(currentLobby == null || !currentLobby.getUsers().contains(currentUser)){
            return "redirect:/home";
        }
        model.addAttribute("lobbyID", currentLobby.getID());
        boolean allDone = true;
        if(currentLobby.isMpcTest()){
            for(MPCQuestion question: currentLobby.getMpcQuestions()){
                boolean questionDone = false;
                for(QuestionAnswer answer: currentLobby.getAnswers()){
                    if(question.equals(answer.getMpcQuestion()) && answer.getStudent().equals(currentUser)){
                        questionDone = true;
                    }
                }
                if(!questionDone){
                    allDone = false;
                }
                if(allDone){
                    return "redirect:/lobby?lobbyID="+lobbyID;
                }
            }
            model.addAttribute("mpcQuestions",currentLobby.getMpcQuestions());
            return "mpcQuiz";
        }else{
            for(TextQuestion question: currentLobby.getTextQuestions()){
                boolean questionDone = false;
                for(QuestionAnswer answer: currentLobby.getAnswers()){
                    if(question.equals(answer.getTextQuestion()) && answer.getStudent().equals(currentUser)){
                        questionDone = true;
                    }
                }
                if(!questionDone){
                    allDone = false;
                }
            }
            if(allDone){
                return "redirect:/lobby?lobbyID="+lobbyID;
            }
            model.addAttribute("textQuestions",currentLobby.getTextQuestions());
            return "textQuiz";
        }
    }

    @RequestMapping(value = "/quizResult")
    public String quizResult(@RequestParam(value="lobbyID", required = true) String lobbyID, @RequestParam(value="answers0", required = true) String answers0,
                             @RequestParam(value="questionIDs0", required = true) String questionIDs0, @RequestParam(value="answers1", required = false, defaultValue = "") String answers1,
                             @RequestParam(value="questionIDs1", required = false, defaultValue = "") String questionIDs1, @RequestParam(value="answers2", required = false, defaultValue = "") String answers2,
                             @RequestParam(value="questionIDs2", required = false, defaultValue = "") String questionIDs2, @RequestParam(value="answers3", required = false, defaultValue = "") String answers3,
                             @RequestParam(value="questionIDs3", required = false, defaultValue = "") String questionIDs3, @RequestParam(value="answers4", required = false, defaultValue = "") String answers4,
                             @RequestParam(value="questionIDs4", required = false, defaultValue = "") String questionIDs4, @RequestParam(value="answers5", required = false, defaultValue = "") String answers5,
                             @RequestParam(value="questionIDs5", required = false, defaultValue = "") String questionIDs5, @RequestParam(value="answers6", required = false, defaultValue = "") String answers6,
                             @RequestParam(value="questionIDs6", required = false, defaultValue = "") String questionIDs6, @RequestParam(value="answers7", required = false, defaultValue = "") String answers7,
                             @RequestParam(value="questionIDs7", required = false, defaultValue = "") String questionIDs7, @RequestParam(value="answers8", required = false, defaultValue = "") String answers8,
                             @RequestParam(value="questionIDs8", required = false, defaultValue = "") String questionIDs8, @RequestParam(value="answers9", required = false, defaultValue = "") String answers9,
                             @RequestParam(value="questionIDs9", required = false, defaultValue = "") String questionIDs9){
        Lobby currentLobby = lobbyRepository.findByLobbyID(Integer.parseInt(lobbyID));
        SopraUser currentUser = getCurrentUser();
        if(currentLobby == null || !currentLobby.getUsers().contains(currentUser)){
            return "redirect:/home";
        }
        List<String> questionIDList = new ArrayList<>();
        List<String> answerList = new ArrayList<>();
        if(!questionIDs0.equals("")){
            questionIDList.add(questionIDs0);
            answerList.add(answers0);
        }
        if(!questionIDs1.equals("")){
            questionIDList.add(questionIDs1);
            answerList.add(answers1);
        }
        if(!questionIDs2.equals("")){
            questionIDList.add(questionIDs2);
            answerList.add(answers2);
        }
        if(!questionIDs3.equals("")){
            questionIDList.add(questionIDs3);
            answerList.add(answers3);
        }
        if(!questionIDs4.equals("")){
            questionIDList.add(questionIDs4);
            answerList.add(answers4);
        }
        if(!questionIDs5.equals("")){
            questionIDList.add(questionIDs5);
            answerList.add(answers5);
        }
        if(!questionIDs6.equals("")){
            questionIDList.add(questionIDs6);
            answerList.add(answers0);
        }
        if(!questionIDs7.equals("")){
            questionIDList.add(questionIDs7);
            answerList.add(answers7);
        }
        if(!questionIDs8.equals("")){
            questionIDList.add(questionIDs8);
            answerList.add(answers8);
        }
        if(!questionIDs9.equals("")){
            questionIDList.add(questionIDs9);
            answerList.add(answers9);
        }
        String[] questionIDArray = questionIDList.toArray(new String[questionIDList.size()]);
        String[] answerArray = answerList.toArray(new String[answerList.size()]);

        boolean allDone = true;
        if(currentLobby.isMpcTest()){
            for(MPCQuestion question: currentLobby.getMpcQuestions()){
                boolean questionDone = false;
                for(QuestionAnswer answer: currentLobby.getAnswers()){
                    if(question.equals(answer.getMpcQuestion()) && answer.getStudent().equals(currentUser)){
                        questionDone = true;
                    }
                }
                if(!questionDone){
                    allDone = false;
                }
                if(allDone){
                    return "redirect:/lobby?lobbyID="+lobbyID;
                }
            }

            for(int i=0; i<questionIDArray.length;i++){
                MPCQuestion currentQuestion = mpcQuestionRepository.findByQuestionID(Integer.parseInt(questionIDArray[i]));
                if(currentQuestion == null){
                    throw new RuntimeException("Unknown Question");
                }
                QuestionAnswer answer = new QuestionAnswer();
                answer.setMpcQuestion(currentQuestion);
                answer.setMpcAnswer(true);
                answer.setQuestionAnswer(answerArray[i]);
                answer.setStudent(currentUser);
                answer.setLobby(currentLobby);
                if(answer.getQuestionAnswer().equals(currentQuestion.getQuestionAnswers()[0])){
                    answer.setCorrect(true);
                    currentUser.addRankingPoints(1);
                }else{
                    answer.setCorrect(false);
                }
                answer.setValidated(true);
                questionAnswerRepository.save(answer);
                currentLobby.addAnswers(answer);
                lobbyRepository.save(currentLobby);
            }
            UserFeedEntry entry = new UserFeedEntry();
            entry.setUser(currentUser);
            entry.setTitle("QUIZ DONE");
            entry.setContent(currentUser.getUsername()+" has answered a quiz!");
            currentUser.addUserFeed(entry);
            userFeedRepository.save(entry);
            userRepository.save(currentUser);
            return "redirect:/lobby?lobbyID="+lobbyID;
        }else{
            for(TextQuestion question: currentLobby.getTextQuestions()){
                boolean questionDone = false;
                for(QuestionAnswer answer: currentLobby.getAnswers()){
                    if(question.equals(answer.getTextQuestion()) && answer.getStudent().equals(currentUser)){
                        questionDone = true;
                    }
                }
                if(!questionDone){
                    allDone = false;
                }
            }
            if(allDone){
                return "redirect:/lobby?lobbyID="+lobbyID;
            }
            for(int i=0; i<questionIDArray.length;i++){
                TextQuestion currentQuestion = textQuestionRepository.findByQuestionID(Integer.parseInt(questionIDArray[i]));
                if(currentQuestion == null){
                    throw new RuntimeException("Unknown Question");
                }
                QuestionAnswer answer = new QuestionAnswer();
                answer.setTextQuestion(currentQuestion);
                answer.setMpcAnswer(false);
                answer.setQuestionAnswer(answerArray[i]);
                answer.setStudent(currentUser);
                answer.setLobby(currentLobby);
                answer.setValidated(false);
                questionAnswerRepository.save(answer);
                currentLobby.addAnswers(answer);
                lobbyRepository.save(currentLobby);
            }
            UserFeedEntry entry = new UserFeedEntry();
            entry.setUser(currentUser);
            entry.setTitle("QUIZ DONE");
            entry.setContent(currentUser.getUsername()+" has answered a quiz!");
            currentUser.addUserFeed(entry);
            userFeedRepository.save(entry);
            userRepository.save(currentUser);
            return "redirect:/lobby?lobbyID="+lobbyID;
        }
    }


    @RequestMapping(value = "/questionCreation", method = RequestMethod.POST)
    public String questionCreation(@RequestParam(value="topic", required = true) String topic, @RequestParam(value="questionText", required = true) String questionText,
                                   @RequestParam(value="questionType", required = false, defaultValue = "MPC") String questionType, @RequestParam(value="answer", required = false, defaultValue = "") String answer,
                                   @RequestParam(value="questionMods", required = false, defaultValue = "") String questionMods){
        if(questionType.equals("MPC")){
            MPCQuestion newQuestion = new MPCQuestion();
            newQuestion.setAdminUser(getCurrentUser());
            newQuestion.setQuestionTopic(topic);
            newQuestion.setQuestionText(questionText);
            String[] answers = answer.split(";");
            for(String currentAnswer: answers){
                newQuestion.addQuestionAnswer(currentAnswer);
            }
            mpcQuestionRepository.save(newQuestion);
        }else{
            TextQuestion newQuestion = new TextQuestion();
            newQuestion.setAdminUser(getCurrentUser());
            newQuestion.setQuestionTopic(topic);
            newQuestion.setQuestionText(questionText);
            String[] mods = answer.split(";");
            for(String mod: mods){
                List<SopraUser> currentUser = userRepository.findByUsername(mod);
                if(currentUser != null && currentUser.size()>0)
                    newQuestion.addQuestionEditors(currentUser.get(0));
            }
           textQuestionRepository.save(newQuestion);
        }
        UserFeedEntry entry = new UserFeedEntry();
        entry.setUser(getCurrentUser());
        entry.setTitle("QUESTION CREATED");
        entry.setContent(getCurrentUser().getUsername()+" created a question on topic "+topic+"!");
        getCurrentUser().addUserFeed(entry);
        userFeedRepository.save(entry);
        userRepository.save(getCurrentUser());
        return "redirect:/questionList?topic="+topic;
    }

    @RequestMapping(value = "/lobby")
    public String lobby(@RequestParam(value="lobbyID", required=true)String lobbyID,@RequestParam(value="error", required=false, defaultValue = "")String error, Model model) {
        if(lobbyRepository.findByLobbyID(Integer.parseInt(lobbyID)) == null){
            return "redirect:/home";
        }
        if(!lobbyRepository.findByLobbyID(Integer.parseInt(lobbyID)).getUsers().contains(getCurrentUser())){
            return "redirect:/home";
        }
        Lobby lobby = lobbyRepository.findByLobbyID(Integer.parseInt(lobbyID));
        SopraUser currentUser = getCurrentUser();
        boolean allDone = true;
        if(lobby.isMpcTest()){
            for(MPCQuestion question: lobby.getMpcQuestions()){
                boolean questionDone = false;
                for(QuestionAnswer answer: lobby.getAnswers()){
                    if(question.equals(answer.getMpcQuestion()) && answer.getStudent().equals(currentUser)){
                        questionDone = true;
                    }
                }
                if(!questionDone){
                    allDone = false;
                }
            }
        }else{
            for(TextQuestion question: lobby.getTextQuestions()){
                boolean questionDone = false;
                for(QuestionAnswer answer: lobby.getAnswers()){
                    if(question.equals(answer.getTextQuestion()) && answer.getStudent().equals(currentUser)){
                        questionDone = true;
                    }
                }
                if(!questionDone){
                    allDone = false;
                }
            }
        }
        model.addAttribute("lobby",lobby);
        model.addAttribute("openQuiz", !allDone);
        model.addAttribute("currentUser",currentUser);
        switch(error){
            case "UserNotFound":{
                model.addAttribute("errorMessage","User was not found!");
                break;
            }
            default:{
                model.addAttribute("errorMessage","");
                break;
            }
        }

        if (getCurrentUser().getId().equals(lobby.getAdminUser().getId())){
            model.addAttribute("userState","admin");
        }else{
            model.addAttribute("userState","user");
        }

        List<SopraUser> members = lobby.getUsers();

        model.addAttribute("members", members);

        return "lobby";
    }

    // Wenn der Nutzer nicht Besitzer der Gruppe ist kann er keine Nutzer hinzufügen
    // Wenn der hinzuzufügende User nicht existiert -> Fehlermeldung
    @RequestMapping(value = "/addUserToLobby", method = RequestMethod.POST)
    public String addUserToLobby(@RequestParam(value="lobbyID", required = true) String lobbyID, @RequestParam(value="userName", required = true) String userName){
        Lobby currentLobby =lobbyRepository.findByLobbyID(Integer.parseInt(lobbyID));
        if (!getCurrentUser().getId().equals(currentLobby.getAdminUser().getId())){
            return "redirect:/home";
        }
        List<SopraUser> newUser = userRepository.findByUsername(userName);
        if(newUser == null || newUser.isEmpty() || !currentLobby.getLearningGroup().users.contains(newUser.get(0))) {
            return "redirect:/lobby?lobbyID=" + currentLobby.getID() + "&error=UserNotFound";
        }else {
            currentLobby.users.add(newUser.get(0));
            lobbyRepository.save(currentLobby);
            UserFeedEntry entry = new UserFeedEntry();
            entry.setUser(newUser.get(0));
            entry.setTitle("LOBBY JOINED");
            entry.setContent(newUser.get(0).getUsername()+" joined a lobby of group "+currentLobby.getLearningGroup().getName()+"!");
            newUser.get(0).addUserFeed(entry);
            userFeedRepository.save(entry);
            userRepository.save(newUser.get(0));
            return "redirect:/lobby?lobbyID=" + currentLobby.getID();
        }
    }

    @RequestMapping(value = "/removeUserFromLobby", method = RequestMethod.POST)
    public String removeUserFromLobby(@RequestParam(value="lobbyID", required = true) String lobbyID, @RequestParam(value="userName", required = true) String userName){
        Lobby currentLobby =lobbyRepository.findByLobbyID(Integer.parseInt(lobbyID));
        if (!getCurrentUser().getId().equals(currentLobby.getAdminUser().getId())
                && !getCurrentUser().getUsername().equals(userName)){
            return "redirect:/home";
        }
        List<SopraUser> newUser = userRepository.findByUsername(userName);
        if(newUser.isEmpty()) {
            return "redirect:/lobby?lobbyID=" + currentLobby.getID() + "&error=UserNotFound";
        }else {
            currentLobby.users.remove(newUser.get(0));
            lobbyRepository.save(currentLobby);
            UserFeedEntry entry = new UserFeedEntry();
            entry.setUser(newUser.get(0));
            entry.setTitle("LOBBY LEFT");
            entry.setContent(newUser.get(0).getUsername()+" left a lobby of group "+currentLobby.getLearningGroup().getName()+"!");
            newUser.get(0).addUserFeed(entry);
            userFeedRepository.save(entry);
            userRepository.save(newUser.get(0));
            return "redirect:/lobby?lobbyID=" + currentLobby.getID();
        }
    }


    // Funktion für das Erstellen von Lobbies
    @RequestMapping(value = "/lobbyCreate", method = RequestMethod.POST)
    public String createLobby(@RequestParam(value="groupID", required = true) String groupID, @RequestParam(value="lobbyType", required = true) String lobbyType){
        LearningGroup currentGroup = learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
        if(currentGroup == null || !currentGroup.getUsers().contains(getCurrentUser())){
            return "redirect:/home";
        }
        SopraUser newAdmin = getCurrentUser();
        Lobby lobby = new Lobby();
        if(lobbyType.equals("MPC")){
            lobby.setMpcTest(true);
            List<MPCQuestion> questionRepository = new ArrayList<>(mpcQuestionRepository.findAll());
            Collections.shuffle(questionRepository);
            int counter = 10;
            for(MPCQuestion question: questionRepository){
                    if(currentGroup.getTopic().equals(question.getQuestionTopic())) {
                        lobby.addMpcQuestions(question);
                        counter--;
                        if(counter <= 0) {
                            break;
                        }
                    }
            }
        }else{
            lobby.setMpcTest(false);
            List<TextQuestion> questionRepository = new ArrayList<>(textQuestionRepository.findAll());
            Collections.shuffle(questionRepository);
            int counter = 10;
            for(TextQuestion question: questionRepository){
                if(currentGroup.getTopic().equals(question.getQuestionTopic())) {
                    lobby.addTextQuestions(question);
                    counter--;
                    if(counter <= 0) {
                        break;
                    }
                }
            }
        }

        lobby.setAdminUser(newAdmin);
        lobby.setLearningGroup(currentGroup);
        lobbyRepository.save(lobby);
        UserFeedEntry entry = new UserFeedEntry();
        entry.setUser(newAdmin);
        entry.setTitle("LOBBY CREATED");
        entry.setContent(newAdmin.getUsername()+" created a lobby for group "+currentGroup.getName()+"!");
        newAdmin.addUserFeed(entry);
        userFeedRepository.save(entry);
        userRepository.save(newAdmin);
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(900000);
                }catch (InterruptedException e) {}
                lobbyDelete(lobby.getID());
            }
        }.start();
        return "redirect:/lobby?lobbyID="+lobby.getID();

    }

    public void lobbyDelete(int lobbyId){
        Lobby lobby = lobbyRepository.findByLobbyID(lobbyId);
        lobby.setAnswers(new ArrayList<>());
        lobby.setMpcQuestions(new ArrayList<>());
        lobby.setTextQuestions(new ArrayList<>());
        lobby.setUsers(new ArrayList<>());
        lobbyRepository.save(lobby);
        lobbyRepository.delete(lobbyId);
    }
}