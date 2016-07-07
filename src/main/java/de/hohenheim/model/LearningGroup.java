package de.hohenheim.model;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class LearningGroup {

    @Id
    @GeneratedValue
    Integer groupId;

    String name;

    String topic = "";

    String description;

    @ManyToMany
    @JoinTable(
                name="GROUPPARTICIPANTS",
                joinColumns=@JoinColumn(name="GROUP_ID"),
                inverseJoinColumns=@JoinColumn(name="USER_ID"))
    List<SopraUser> users = new ArrayList<>();

    public List<UploadedFile> getFiles() {
        return files;
    }

    public void setFiles(List<UploadedFile> files) {
        this.files = files;
    }

    public void addFiles(UploadedFile file) {
        files.add(file);
    }

    @ManyToMany
    @JoinTable(
            name="GROUPFILES",
            joinColumns=@JoinColumn(name="GROUP_ID"),
            inverseJoinColumns=@JoinColumn(name="FILE_ID"))
    List<UploadedFile> files = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name="COMMENTASSIGNMENT",
            joinColumns=@JoinColumn(name="GROUP_ID"),
            inverseJoinColumns=@JoinColumn(name="COMMENT_ID"))
    List<SopraUser> comments = new ArrayList<>();

    @ManyToOne
    SopraUser adminUser;

    public Integer getId() { return groupId; }

    public SopraUser getAdminUser() { return adminUser; }

    public void setAdminUser(SopraUser user) {
        if(!users.contains(user)){
           users.add(user);
        }
        adminUser = user; }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Integer id) { this.groupId = id; }

    public String getName() { return name; }

    public void setName(String name){ this.name = name; }

    public List<SopraUser> getUsers() { return users; }

    public void setUsers(List<SopraUser> users) { this.users = users; }

    // Überprüft ob die 2 zu überprüfenden Lerngruppen die gleichen sind
    @Override
    public boolean equals(Object o){
        if(this.name == null){
            return false;
        }else if(o == null){
            return false;
        }else if(o == this){
            return true;
        }else if(!(o instanceof LearningGroup)){
            return false;
        }
        LearningGroup other = (LearningGroup)o;
        if(this.name.equals(other.name)){
            return true;
        }else{
            return false;
        }
    }
}