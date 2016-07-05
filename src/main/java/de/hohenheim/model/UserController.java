package de.hohenheim.model;

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

import java.util.List;

@Controller
//@EnableWebMvc
public class UserController extends WebMvcConfigurerAdapter {

    @Autowired
    private LearningGroupRepository learningGroupRepository;

    @Autowired
    private SopraUserRepository userRepository;

    @Autowired
    private UserFeedEntryRepository userFeedRepository;

    private SopraUser getCurrentUser() {
        String userName = ((User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getUsername();
        List<SopraUser> currentUser = userRepository.findByUsername(userName);
        if(currentUser == null){
            return null;
        }
        return currentUser.get(0);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @RequestMapping(value = "/home")
    public String userProfile(Model model) {
        SopraUser user = getCurrentUser();
        model.addAttribute("User",user);
        model.addAttribute("userState","owner");
        return "user";
    }

    // Wenn die Lerngruppe nicht existiert -> Home
    // Wenn der User nicht in der Lerngruppe ist -> Home
    // Wenn der User Gruppenadmin ist bekommt er die Möglichkeit User hinzuzufügen
    @RequestMapping(value = "/user")
    public String userProfile(@RequestParam(value="userName", required=true)String userName, Model model) {
        if(userRepository.findByUsername(userName) == null || userRepository.findByUsername(userName).size()<1){
            return "redirect:/home";
        }
        SopraUser user = userRepository.findByUsername(userName).get(0);
        model.addAttribute("User",user);

        if (getCurrentUser().getId().equals(user.getId())){
            model.addAttribute("userState","owner");
        }else{
            model.addAttribute("userState","user");
        }

        return "user";
    }

    /**
     * Creates a list representing all current registered Users
     * @param model
     * @return
     */
    @RequestMapping(value = "/userList")
    public String groupDirectory(Model model) {
        List<SopraUser> users = userRepository.findAll();
        model.addAttribute("Users", users);
        return "users";
    }

    /**
     * Update personal user info
     *
     * @return
     */
    @RequestMapping(value = "/userUpdatePersonal", method = RequestMethod.POST)
    public String userUpdatePersonal(@RequestParam(value="userName", required = true) String userName, @RequestParam(value="firstName", required = true) String firstName,
                              @RequestParam(value="lastName", required = true) String lastName, @RequestParam(value="birthDate", required = true) String birthDate,
                              @RequestParam(value="gender", required = true) String gender, @RequestParam(value="telefonNumber", required = true) String telefonNumber, Model model){
        if(userRepository.findByUsername(userName) == null || userRepository.findByUsername(userName).size()<1){
            return "redirect:/home";
        }
        SopraUser user = userRepository.findByUsername(userName).get(0);
        if(!user.getUsername().equals(getCurrentUser().getUsername())){
            return "redirect:/home";
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.parseBirthDate(birthDate);
        user.setGender(SopraUser.Gender.valueOf(gender));
        user.setTelefonNumber(telefonNumber);
        userRepository.save(user);
        model.addAttribute("User",user);
        model.addAttribute("userState","owner");
        return "user";
    }

    /**
     * Update personal user info
     *
     * @return
     */
    @RequestMapping(value = "/userUpdateAddress", method = RequestMethod.POST)
    public String userUpdateAddress(@RequestParam(value="userName", required = true) String userName, @RequestParam(value="streetName", required = true) String streetName,
                              @RequestParam(value="streetNumber", required = true) String streetNumber, @RequestParam(value="postalCode", required = true) String postalCode,
                              @RequestParam(value="city", required = true) String city, Model model){
        if(userRepository.findByUsername(userName) == null || userRepository.findByUsername(userName).size()<1){
            return "redirect:/home";
        }
        SopraUser user = userRepository.findByUsername(userName).get(0);
        if(!user.getUsername().equals(getCurrentUser().getUsername())){
            return "redirect:/home";
        }
        user.setStreetName(streetName);
        user.setStreetNumber(streetNumber);
        user.setPostalCode(postalCode);
        user.setCity(city);
        userRepository.save(user);
        model.addAttribute("User",user);
        model.addAttribute("userState","owner");
        return "user";
    }

    @RequestMapping(value = "/userUpdateAddHobby", method = RequestMethod.POST)
    public String userUpdateAddHobby(@RequestParam(value="userName", required = true) String userName, @RequestParam(value="hobby", required = true) String hobby, Model model){
        if(userRepository.findByUsername(userName) == null || userRepository.findByUsername(userName).size()<1){
            return "redirect:/home";
        }
        SopraUser user = userRepository.findByUsername(userName).get(0);
        if(!user.getUsername().equals(getCurrentUser().getUsername())){
            return "redirect:/home";
        }
        user.addHobbies(hobby);
        userRepository.save(user);
        model.addAttribute("User",user);
        model.addAttribute("userState","owner");
        return "user";
    }

    @RequestMapping(value = "/userUpdateRemoveHobby", method = RequestMethod.POST)
    public String userUpdateRemoveHobby(@RequestParam(value="userName", required = true) String userName, @RequestParam(value="hobby", required = true) String hobby, Model model){
        if(userRepository.findByUsername(userName) == null || userRepository.findByUsername(userName).size()<1){
            return "redirect:/home";
        }
        SopraUser user = userRepository.findByUsername(userName).get(0);
        if(!user.getUsername().equals(getCurrentUser().getUsername())){
            return "redirect:/home";
        }
        user.removeHobbies(hobby);
        userRepository.save(user);
        model.addAttribute("User",user);
        model.addAttribute("userState","owner");
        return "user";
    }


}