package de.hohenheim.security;

import de.hohenheim.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;


@Transactional
@Component
public class TestSetup implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserDetailsManager userDetailsManager;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private SopraUserRepository userRepository;
    @Autowired
    private LearningGroupRepository learningGroupRepository;
    @Autowired
    private MPCQuestionRepository mpcQuestionRepository;
    @Autowired
    private TextQuestionRepository textQuestionRepository;

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Collection<GrantedAuthority> authsAdmin = new ArrayList<>();
        authsAdmin.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        userDetailsManager.createUser(new User("admin", passwordEncoder.encode("admin"), authsAdmin));

        Collection<GrantedAuthority> authsHans = new ArrayList<>();
        authsHans.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetailsManager.createUser(new User("hans", passwordEncoder.encode("hugo"), authsHans));

        SopraUser user1 = new SopraUser();
        user1.setUsername("admin");
        userRepository.save(user1);

        SopraUser user2 = new SopraUser();
        user2.setUsername("hans");
        userRepository.save(user2);

        Collection<GrantedAuthority> authsJulian = new ArrayList<>();
        authsJulian.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetailsManager.createUser(new User("Julian", passwordEncoder.encode("test1"), authsJulian));

        SopraUser user3 = new SopraUser();
        user3.setUsername("Julian");
        user3.setFirstName("Julian");
        user3.setPostalCode("73230");
        user3.setLastName("Prokosch");
        user3.addHobbies("Musik");
        user3.addHobbies("Tennis");
        user3.setEmailAdress("julian.prokosch@web.de");
        user3.setStreetName("Birkhahnweg");
        user3.setCity("Kirchheim");
        user3.setStreetNumber("22");
        user3.setTelefonNumber("07021/123456");
        user3.setGender(SopraUser.Gender.male);
        userRepository.save(user3);

        Collection<GrantedAuthority> authsBenny = new ArrayList<>();
        authsBenny.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetailsManager.createUser(new User("Benny", passwordEncoder.encode("test2"), authsBenny));

        SopraUser user4 = new SopraUser();
        user4.setUsername("Benny");
        userRepository.save(user4);

        Collection<GrantedAuthority> authsCedrik = new ArrayList<>();
        authsCedrik.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetailsManager.createUser(new User("Cedrik", passwordEncoder.encode("test3"), authsCedrik));

        SopraUser user5 = new SopraUser();
        user5.setUsername("Cedrik");
        userRepository.save(user5);


        LearningGroup temporaryLearningGroup = new LearningGroup();
        temporaryLearningGroup.setName("TestMatheGroup");
        temporaryLearningGroup.setAdminUser(user1);
        temporaryLearningGroup.setTopic("Mathe");
        temporaryLearningGroup.setDescription("Hier gibt es Mathematik Zeug");
        temporaryLearningGroup.getUsers().add(user5);
        temporaryLearningGroup.getUsers().add(user3);
        temporaryLearningGroup.getUsers().add(user2);
        learningGroupRepository.save(temporaryLearningGroup);

        MPCQuestion newQuestion = new MPCQuestion();
        newQuestion.setAdminUser(user1);
        newQuestion.setQuestionTopic("Mathe");
        newQuestion.setQuestionText("Was ist 1+2?");
        newQuestion.addQuestionAnswer("3");
        newQuestion.addQuestionAnswer("1");
        newQuestion.addQuestionAnswer("5");
        newQuestion.addQuestionAnswer("6");
        mpcQuestionRepository.save(newQuestion);

        MPCQuestion newQuestion2 = new MPCQuestion();
        newQuestion2.setAdminUser(user1);
        newQuestion2.setQuestionTopic("Mathe");
        newQuestion2.setQuestionText("Was ist 5*3?");
        newQuestion2.addQuestionAnswer("15");
        newQuestion2.addQuestionAnswer("13");
        newQuestion2.addQuestionAnswer("52");
        newQuestion2.addQuestionAnswer("9");
        mpcQuestionRepository.save(newQuestion2);

        MPCQuestion newQuestion3 = new MPCQuestion();
        newQuestion3.setAdminUser(user1);
        newQuestion3.setQuestionTopic("Mathe");
        newQuestion3.setQuestionText("Was ist 1*1?");
        newQuestion3.addQuestionAnswer("1");
        newQuestion3.addQuestionAnswer("3");
        newQuestion3.addQuestionAnswer("52");
        newQuestion3.addQuestionAnswer("15");
        mpcQuestionRepository.save(newQuestion3);

        MPCQuestion newQuestion4 = new MPCQuestion();
        newQuestion4.setAdminUser(user1);
        newQuestion4.setQuestionTopic("Mathe");
        newQuestion4.setQuestionText("Was ist 2^2?");
        newQuestion4.addQuestionAnswer("4");
        newQuestion4.addQuestionAnswer("5");
        newQuestion4.addQuestionAnswer("6");
        newQuestion4.addQuestionAnswer("X");
        mpcQuestionRepository.save(newQuestion4);

        MPCQuestion newQuestion5 = new MPCQuestion();
        newQuestion5.setAdminUser(user1);
        newQuestion5.setQuestionTopic("Mathe");
        newQuestion5.setQuestionText("Was ist 2/2?");
        newQuestion5.addQuestionAnswer("1");
        newQuestion5.addQuestionAnswer("12");
        newQuestion5.addQuestionAnswer("22");
        newQuestion5.addQuestionAnswer("23");
        mpcQuestionRepository.save(newQuestion5);

        MPCQuestion newQuestion6 = new MPCQuestion();
        newQuestion6.setAdminUser(user1);
        newQuestion6.setQuestionTopic("Mathe");
        newQuestion6.setQuestionText("Was ist 5*5?");
        newQuestion6.addQuestionAnswer("25");
        newQuestion6.addQuestionAnswer("18");
        newQuestion6.addQuestionAnswer("42");
        newQuestion6.addQuestionAnswer("43");
        mpcQuestionRepository.save(newQuestion6);

        MPCQuestion newQuestion7 = new MPCQuestion();
        newQuestion7.setAdminUser(user1);
        newQuestion7.setQuestionTopic("Mathe");
        newQuestion7.setQuestionText("Löse a+b=2");
        newQuestion7.addQuestionAnswer("a=1 b=1");
        newQuestion7.addQuestionAnswer("a=3 b=5");
        newQuestion7.addQuestionAnswer("a=10 b=0");
        newQuestion7.addQuestionAnswer("a=1 b=7");
        mpcQuestionRepository.save(newQuestion7);

        MPCQuestion newQuestion8 = new MPCQuestion();
        newQuestion8.setAdminUser(user1);
        newQuestion8.setQuestionTopic("Mathe");
        newQuestion8.setQuestionText("Was ist 3*2?");
        newQuestion8.addQuestionAnswer("6");
        newQuestion8.addQuestionAnswer("13");
        newQuestion8.addQuestionAnswer("99");
        newQuestion8.addQuestionAnswer("9");
        mpcQuestionRepository.save(newQuestion8);

        MPCQuestion newQuestion9 = new MPCQuestion();
        newQuestion9.setAdminUser(user1);
        newQuestion9.setQuestionTopic("Mathe");
        newQuestion9.setQuestionText("Was ist 6+6?");
        newQuestion9.addQuestionAnswer("12");
        newQuestion9.addQuestionAnswer("66");
        newQuestion9.addQuestionAnswer("6");
        newQuestion9.addQuestionAnswer("0");
        mpcQuestionRepository.save(newQuestion9);

        MPCQuestion newQuestion10 = new MPCQuestion();
        newQuestion10.setAdminUser(user1);
        newQuestion10.setQuestionTopic("Mathe");
        newQuestion10.setQuestionText("Was ist 10*10?");
        newQuestion10.addQuestionAnswer("100");
        newQuestion10.addQuestionAnswer("10");
        newQuestion10.addQuestionAnswer("1");
        newQuestion10.addQuestionAnswer("9");
        mpcQuestionRepository.save(newQuestion10);

        MPCQuestion newQuestion11 = new MPCQuestion();
        newQuestion11.setAdminUser(user1);
        newQuestion11.setQuestionTopic("Mathe");
        newQuestion11.setQuestionText("Was ist 11*1?");
        newQuestion11.addQuestionAnswer("11");
        newQuestion11.addQuestionAnswer("111");
        newQuestion11.addQuestionAnswer("1111");
        newQuestion11.addQuestionAnswer("1");
        mpcQuestionRepository.save(newQuestion11);

        TextQuestion newTextQuestion1 = new TextQuestion();
        newTextQuestion1.setAdminUser(user1);
        newTextQuestion1.setQuestionTopic("Mathe");
        newTextQuestion1.setQuestionText("Nenne die Dreiecksungleichung!");
        newTextQuestion1.addQuestionEditors(user2);
        newTextQuestion1.addQuestionEditors(user3);
        textQuestionRepository.save(newTextQuestion1);

        TextQuestion newTextQuestion2 = new TextQuestion();
        newTextQuestion2.setAdminUser(user1);
        newTextQuestion2.setQuestionTopic("Mathe");
        newTextQuestion2.setQuestionText("Warum studiert man Mathe?");
        newTextQuestion2.addQuestionEditors(user2);
        newTextQuestion2.addQuestionEditors(user3);
        textQuestionRepository.save(newTextQuestion2);

        TextQuestion newTextQuestion3 = new TextQuestion();
        newTextQuestion3.setAdminUser(user1);
        newTextQuestion3.setQuestionTopic("Mathe");
        newTextQuestion3.setQuestionText("Ein Zug fährt mit 100km/h auf einen 30km entfernten Bahnhof zu. Welche Temperatur hat das Brot in der Tasche eines Reisenden bei der Ankunft?");
        newTextQuestion3.addQuestionEditors(user2);
        newTextQuestion3.addQuestionEditors(user3);
        textQuestionRepository.save(newTextQuestion3);
    }


}