package de.hohenheim.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import java.util.List;

@Controller
//@EnableWebMvc
public class GeneralController extends WebMvcConfigurerAdapter {

    @Autowired
    private LearningGroupRepository learningGroupRepository;

    @Autowired
    private SopraUserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

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

    /*@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/css/**").addResourceLocations("/static/css/")
        .setCachePeriod(3600)
        .resourceChain(true)
        .addResolver(new PathResourceResolver());
    }*/

    @RequestMapping(value = "/admin/home")
    public String adminHome() {
        return "adminhome";
    }

    @RequestMapping(value = "/home")
    public String userHome() {
        return "userhome";
    }

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