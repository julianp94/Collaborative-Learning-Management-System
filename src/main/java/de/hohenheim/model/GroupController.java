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
public class GroupController extends WebMvcConfigurerAdapter {

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

    // Wenn die Lerngruppe nicht existiert -> Home
    // Wenn der User nicht in der Lerngruppe ist -> Home
    // Wenn der User Gruppenadmin ist bekommt er die Möglichkeit User hinzuzufügen
    @RequestMapping(value = "/group")
    public String groupHome(@RequestParam(value="groupID", required=true)String groupID,@RequestParam(value="error", required=false, defaultValue = "")String error, Model model) {
        if(learningGroupRepository.findByGroupId(Integer.parseInt(groupID)) == null){
            return "redirect:/home";
        }
        if(!learningGroupRepository.findByGroupId(Integer.parseInt(groupID)).getUsers().contains(getCurrentUser())){
            return "redirect:/home";
        }
        model.addAttribute("groupID",groupID);
        LearningGroup currentGroup = learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
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
        if (getCurrentUser().getId().equals(learningGroupRepository.findByGroupId(Integer.parseInt(groupID)).getAdminUser().getId())){
            model.addAttribute("userState","admin");
        }else{
            model.addAttribute("userState","user");
        }

        List<Comment> comments = new ArrayList<Comment>();
        for(Comment comment: commentRepository.findAll()){
            if(comment.group.getId().equals(Integer.parseInt(groupID)) && !comment.getSubComment()){
               comments.add(comment);
            }
        }
        Comment parentComment = new Comment();
        parentComment.setSubComments(comments);
        model.addAttribute("comments", parentComment);

        List<SopraUser> members = new ArrayList<SopraUser>();
        for(SopraUser user: userRepository.findAll()){
            if(currentGroup.getUsers().contains(user)){
                members.add(user);
            }
        }
        model.addAttribute("members", members);

        return "group";
    }

    @RequestMapping(value="/leaveGroup", method = RequestMethod.POST)
    public String leaveGroup(@RequestParam(value="groupID", required = true) String groupID){
        return "redirect:/home";
    }

    @RequestMapping(value="/removeUserFromGroup", method = RequestMethod.POST)
    public String leaveGroup(@RequestParam(value="groupID", required = true) String groupID, @RequestParam(value="userName", required = true) String userName){
        return "redirect:/home";
    }


    // Wenn der Nutzer nicht Besitzer der Gruppe ist kann er keine Nutzer hinzufügen
    // Wenn der hinzuzufügende User nicht existiert -> Fehlermeldung
    @RequestMapping(value = "/addUserToGroup", method = RequestMethod.POST)
    public String addUserToGroup(@RequestParam(value="groupID", required = true) String groupID, @RequestParam(value="userName", required = true) String userName){
        LearningGroup currentGroup =learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
        if (!getCurrentUser().getId().equals(learningGroupRepository.findByGroupId(Integer.parseInt(groupID)).getAdminUser().getId())){
            return "redirect:/home";
        }
        List<SopraUser> newUser = userRepository.findByUsername(userName);
        if(newUser.isEmpty()) {
            return "redirect:/group?groupID=" + currentGroup.getId() + "&error=UserNotFound";
        }else {
            currentGroup.users.add(newUser.get(0));
            learningGroupRepository.save(currentGroup);
            System.out.println("Added "+newUser.get(0).getUsername()+" to Group "+currentGroup.getName());
            return "redirect:/group?groupID=" + currentGroup.getId();
        }
    }

    // Funktion für das Erstellen von Gruppen
    @RequestMapping(value = "/groupCreate", method = RequestMethod.POST)
    public String createGroup(@RequestParam(value="groupName", required = true) String groupName){
        LearningGroup temporaryLearningGroup = new LearningGroup();
        SopraUser newAdmin = getCurrentUser();
        temporaryLearningGroup.setName(groupName);
        temporaryLearningGroup.setAdminUser(newAdmin);
        learningGroupRepository.save(temporaryLearningGroup);
        return "redirect:/group?groupID="+temporaryLearningGroup.getId();
    }

    //Funktion für das Erstellen von Kommentaren
    @RequestMapping(value = "/addComment", method = RequestMethod.POST)
    public String addComment(@RequestParam(value="groupID", required = true) String groupID, @RequestParam(value="commentTitle", required = true) String commentTitle,
            @RequestParam(value="commentContent", required = true) String commentContent, @RequestParam(value="commentParentID", required = false) String commentParentID){
        LearningGroup currentGroup =learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
        if (!learningGroupRepository.findByGroupId(Integer.parseInt(groupID)).getUsers().contains(getCurrentUser())){
            return "redirect:/home";
        }
        Comment newComment = new Comment();
        newComment.setAuthor(getCurrentUser());
        newComment.setTitle(commentTitle);
        newComment.setContent(commentContent);
        newComment.setGroup(currentGroup);
        if(commentParentID != null){
            newComment.setSubComment(true);
            commentRepository.save(newComment);
            Comment parentComment = commentRepository.findByCommentID(Integer.parseInt(commentParentID));
            if(parentComment != null){
                parentComment.subComments.add(newComment);
                commentRepository.save(parentComment);
            }
        }else{
            newComment.setSubComment(false);
            commentRepository.save(newComment);
        }
        System.out.println("Added comment: "+newComment.getTitle()+" to Group "+currentGroup.getName());
        return "redirect:/group?groupID=" + currentGroup.getId();
    }
}