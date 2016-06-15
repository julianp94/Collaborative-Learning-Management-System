package de.hohenheim.controller;

/**
 * Created by Julian on 18.05.2016.
 */

        import de.hohenheim.model.LearningGroup;
        import de.hohenheim.model.LearningGroupRepository;
        import de.hohenheim.model.User;
        import de.hohenheim.model.UserRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestParam;

        import java.util.ArrayList;
        import java.util.List;

@Controller
public class GreetingController {

    @Autowired
    LearningGroupRepository learningGroupRepository;

    @Autowired
    UserRepository userRepository;


    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
        LearningGroup lg1 = new LearningGroup();
        User user1 = new User();
        User user2 = new User();
        List<User> users = new ArrayList<>();
        users.add(user1);
        userRepository.save(user1);
        userRepository.save(user2);
        users.add(user2);
        lg1.setUsers(users);
        lg1.setName("Lerngruppe 1");
        learningGroupRepository.save(lg1);
        model.addAttribute("name", name);
        return "greeting";
    }

}
