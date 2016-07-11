package de.hohenheim.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GroupController extends WebMvcConfigurerAdapter {

    @Autowired
    private LearningGroupRepository learningGroupRepository;

    @Autowired
    private SopraUserRepository userRepository;

    @Autowired
    private GroupCommentRepository commentRepository;

    @Autowired
    private UserFeedEntryRepository userFeedRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private LobbyRepository lobbyRepository;

    private final ResourceLoader resourceLoader;

    @Autowired
    public GroupController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        new File(ROOT).mkdir();
    }
// Uploads werden im Ordner des ausführenden Programms + /upload gespeichert
    public static final String ROOT = System.getProperty("user.dir")+"/upload";

// Abfrage ob User im System vorhanden ist
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
//Fileupload
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam(value="groupID", required=true)String groupID,
                                   RedirectAttributes redirectAttributes) {
        if (!file.isEmpty()) {
            try {
                Files.copy(file.getInputStream(), Paths.get(ROOT, file.getOriginalFilename()));
                LearningGroup currentGroup = learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
                if(currentGroup == null|| !currentGroup.getUsers().contains(getCurrentUser())){
                    return "redirect:/home";
                }
                UploadedFile newFile = new UploadedFile();
                newFile.setAdminUser(getCurrentUser());
                newFile.setFile(file.getOriginalFilename());
                fileRepository.save(newFile);
                currentGroup.addFiles(newFile);
                learningGroupRepository.save(currentGroup);
                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded " + file.getOriginalFilename() + "!");
            } catch (IOException |RuntimeException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "Failed to upload " + file.getOriginalFilename());
            }
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to upload " + file.getOriginalFilename() + " because it was empty");
        }

        return "redirect:/group?groupID="+groupID;
    }
//File anzeigen lassen
    @RequestMapping(method = RequestMethod.GET, value = "/uploads/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {

        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
        LearningGroup group = learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
        model.addAttribute("groupID",groupID);
        List<Lobby> lobbies = new ArrayList<>();
        for(Lobby lobby: lobbyRepository.findAll()){
            if(lobby.getLearningGroup().equals(group) && lobby.getUsers().contains(getCurrentUser())){
                lobbies.add(lobby);
            }
        }
        model.addAttribute("lobbies", lobbies);
        model.addAttribute("group", group);
        model.addAttribute("currentUser",getCurrentUser());
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

        List<GroupComment> comments = new ArrayList<GroupComment>();
        for(GroupComment comment: commentRepository.findAll()){
            if(comment.group.getId().equals(Integer.parseInt(groupID)) && !comment.getSubComment()){
               comments.add(comment);
            }
        }
        GroupComment parentComment = new GroupComment();
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
            UserFeedEntry entry = new UserFeedEntry();
            entry.setUser(newUser.get(0));
            entry.setTitle("GROUP JOINED");
            entry.setContent(newUser.get(0).getUsername()+" joined the group "+currentGroup.getName()+"!");
            newUser.get(0).addUserFeed(entry);
            userFeedRepository.save(entry);
            userRepository.save(newUser.get(0));
            System.out.println("Added "+newUser.get(0).getUsername()+" to Group "+currentGroup.getName());
            return "redirect:/group?groupID=" + currentGroup.getId();
        }
    }
    // Wenn der Nutzer nicht Besitzer der Gruppe ist kann er keine Nutzer entfernen
    // Wenn der zu entfernende User nicht existiert -> Fehlermeldung
    @RequestMapping(value = "/removeUserFromGroup", method = RequestMethod.POST)
    public String removeUserFromGroup(@RequestParam(value="groupID", required = true) String groupID, @RequestParam(value="userName", required = true) String userName){
        LearningGroup currentGroup =learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
        if (!getCurrentUser().getId().equals(learningGroupRepository.findByGroupId(Integer.parseInt(groupID)).getAdminUser().getId())
                && !getCurrentUser().getUsername().equals(userName)){
            return "redirect:/home";
        }
        List<SopraUser> newUser = userRepository.findByUsername(userName);
        if(newUser.isEmpty()) {
            return "redirect:/group?groupID=" + currentGroup.getId() + "&error=UserNotFound";
        }else {
            currentGroup.users.remove(newUser.get(0));
            learningGroupRepository.save(currentGroup);
            UserFeedEntry entry = new UserFeedEntry();
            entry.setUser(newUser.get(0));
            entry.setTitle("GROUP LEFT");
            entry.setContent(newUser.get(0).getUsername()+" left the group "+currentGroup.getName()+"!");
            newUser.get(0).addUserFeed(entry);
            userFeedRepository.save(entry);
            userRepository.save(newUser.get(0));
            System.out.println("Removed "+newUser.get(0).getUsername()+" from Group "+currentGroup.getName());
            return "redirect:/group?groupID=" + currentGroup.getId();
        }
    }
//Bearbeiten des Topics/Description
    @RequestMapping(value = "/groupDataUpdate", method = RequestMethod.POST)
    public String groupDataUpdate(@RequestParam(value="groupID", required = true) String groupID, @RequestParam(value="groupTopic", required = true) String groupTopic, @RequestParam(value="groupDescription", required = true) String groupDescription){
        LearningGroup currentGroup =learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
        if (!getCurrentUser().getId().equals(learningGroupRepository.findByGroupId(Integer.parseInt(groupID)).getAdminUser().getId())){
            return "redirect:/home";
        }
        currentGroup.setTopic(groupTopic);
        currentGroup.setDescription(groupDescription);
        learningGroupRepository.save(currentGroup);

        return "redirect:/group?groupID="+currentGroup.getId();
    }

    // Funktion für das Erstellen von Gruppen
    @RequestMapping(value = "/groupCreate", method = RequestMethod.POST)
    public String createGroup(@RequestParam(value="groupName", required = true) String groupName){
        LearningGroup temporaryLearningGroup = new LearningGroup();
        SopraUser newAdmin = getCurrentUser();
        temporaryLearningGroup.setName(groupName);
        temporaryLearningGroup.setAdminUser(newAdmin);
        learningGroupRepository.save(temporaryLearningGroup);
        UserFeedEntry entry = new UserFeedEntry();
        entry.setUser(newAdmin);
        entry.setTitle("GROUP CREATED");
        entry.setContent(newAdmin.getUsername()+" created the group "+temporaryLearningGroup.getName()+"!");
        newAdmin.addUserFeed(entry);
        userFeedRepository.save(entry);
        userRepository.save(newAdmin);
        return "redirect:/group?groupID="+temporaryLearningGroup.getId();
    }
//Likes erstellen oder entfernen
    @RequestMapping(value = "/commentLike")
    public String commentLike(@RequestParam(value="commentID", required = true) String commentID){
        GroupComment comment = commentRepository.findByCommentID(Integer.parseInt(commentID));
        if(comment == null){
            return "redirect:/home";
        }
        comment.addLike(getCurrentUser());
        commentRepository.save(comment);
        return "redirect:/group?groupID=" + comment.getGroup().getId();
    }

    @RequestMapping(value = "/commentUnlike")
    public String commentUnlike(@RequestParam(value="commentID", required = true) String commentID){
        GroupComment comment = commentRepository.findByCommentID(Integer.parseInt(commentID));
        if(comment == null){
            return "redirect:/home";
        }
        comment.removeLike(getCurrentUser());
        commentRepository.save(comment);
        return "redirect:/group?groupID=" + comment.getGroup().getId();
    }
//Wenn User Admin ist, kann er Kommentare entfernen
    @RequestMapping(value = "/removeComment")
    public String removeComment(@RequestParam(value="commentID", required = true) String commentID){
        GroupComment comment = commentRepository.findByCommentID(Integer.parseInt(commentID));
        if(comment == null){
            return "redirect:/home";
        }
        if(comment.getGroup().getAdminUser().equals(getCurrentUser())) {
            removeComment(comment);
        }
        return "redirect:/group?groupID=" + comment.getGroup().getId();
    }
//Alle Subcomments und Likes werden mitgelöscht
    private void removeComment(GroupComment comment){
        List<GroupComment> subComments = new ArrayList<>(comment.getSubComments());
        comment.setSubComments(new ArrayList<>());
        for(GroupComment subComment: subComments){
            removeComment(subComment);
        }
        comment.setLikes(new ArrayList<>());
        commentRepository.delete(comment.getCommentID());
    }

    //Funktion für das Erstellen von Kommentaren
    @RequestMapping(value = "/addComment", method = RequestMethod.POST)
    public String addComment(@RequestParam(value="groupID", required = true) String groupID, @RequestParam(value="commentTitle", required = true) String commentTitle,
            @RequestParam(value="commentContent", required = true) String commentContent, @RequestParam(value="commentParentID", required = false) String commentParentID){
        LearningGroup currentGroup =learningGroupRepository.findByGroupId(Integer.parseInt(groupID));
        if (!learningGroupRepository.findByGroupId(Integer.parseInt(groupID)).getUsers().contains(getCurrentUser())){
            return "redirect:/home";
        }
        GroupComment newComment = new GroupComment();
        newComment.setAuthor(getCurrentUser());
        newComment.setTitle(commentTitle);
        newComment.setContent(commentContent);
        newComment.setGroup(currentGroup);
        if(commentParentID != null){
            newComment.setSubComment(true);
            commentRepository.save(newComment);
            GroupComment parentComment = commentRepository.findByCommentID(Integer.parseInt(commentParentID));
            if(parentComment != null){
                parentComment.subComments.add(newComment);
                commentRepository.save(parentComment);
            }
        }else{
            newComment.setSubComment(false);
            commentRepository.save(newComment);
        }
        SopraUser currentUser = getCurrentUser();
        UserFeedEntry entry = new UserFeedEntry();
        entry.setUser(currentUser);
        entry.setTitle("COMMENT");
        entry.setContent(currentUser.getUsername()+" wrote a comment in the group "+currentGroup.getName()+"!");
        currentUser.addUserFeed(entry);
        userFeedRepository.save(entry);
        userRepository.save(currentUser);
        System.out.println("Added comment: "+newComment.getTitle()+" to Group "+currentGroup.getName());
        return "redirect:/group?groupID=" + currentGroup.getId();
    }
}