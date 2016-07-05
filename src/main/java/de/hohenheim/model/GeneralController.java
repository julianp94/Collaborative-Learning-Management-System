package de.hohenheim.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
//@EnableWebMvc
public class GeneralController extends WebMvcConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private LearningGroupRepository learningGroupRepository;

    @Autowired
    private SopraUserRepository userRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

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


    @RequestMapping(value = "/admin/home")
    public String adminHome() {
        return "redirect:/home";
    }


    @RequestMapping(value = "/register")
    public String register(@RequestParam(value="error", required = false) String error, Model model) {
        if(error != null){
            model.addAttribute("error", true);
        }else{
            model.addAttribute("error", false);
        }
        return "register";
    }

    @RequestMapping(value = "/registrate", method = RequestMethod.POST)
    public String registering(@RequestParam(value="userName", required = true) String userName, @RequestParam(value="userPassword", required = true) String userPassword,
                              @RequestParam(value="userMail", required = true) String userMail) {
        List<SopraUser> currentUser = userRepository.findByUsername(userName);
        if(currentUser != null && currentUser.size() > 0){
            return "register?error=NameTaken";
        }

        Collection<GrantedAuthority> authUser = new ArrayList<>();
        authUser.add(new SimpleGrantedAuthority("ROLE_USER"));
        userDetailsManager.createUser(new User(userName, passwordEncoder.encode(userPassword), authUser));

        SopraUser newUser = new SopraUser();
        newUser.setUsername(userName);
        newUser.setEmailAdress(userMail);
        userRepository.save(newUser);
        return "login";
    }


   // @RequestMapping(value = "/home")
   // public String userHome() {
  //      return "userhome";
   // }

    // Seite für das erstellen von Gruppen, zeigt alle Gruppen an in denen man Mitglied ist
    @RequestMapping(value = "/groupList")
    public String groupDirectory(Model model) {
        List<LearningGroup> groupIDs = new ArrayList<LearningGroup>();
        for(LearningGroup group: learningGroupRepository.findAll()){
            if(group.getUsers().contains(getCurrentUser())){
                groupIDs.add(group);
            }
        }
        model.addAttribute("groupIDs", groupIDs);
        List<Lobby> lobbies = new ArrayList<>();
        for(Lobby lobby: lobbyRepository.findAll()){
            if(lobby.getUsers().contains(getCurrentUser())){
                lobbies.add(lobby);
            }
        }
        model.addAttribute("lobbies", lobbies);
        return "groups";
    }

    /**
     * Request mapping for the disputeResolutionOverview page or administrationOverview page depending on user role.
     * Propagates to respective methods
     *
     * @return home url?
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String displayHome() {
        if (((User) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/home";
        } else {
            return "redirect:/home";
        }
    }
}