package de.hohenheim.model;



import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class UploadedFile {

    @Id
    @GeneratedValue
    Integer fileID;

    @ManyToOne
    SopraUser adminUser;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    String file;

    public Integer getID() { return fileID; }

    public SopraUser getAdminUser() { return adminUser; }

    public void setAdminUser(SopraUser user) {
        adminUser = user; }

    public void setId(Integer id) { this.fileID = id; }

    // Überprüft ob die 2 zu überprüfenden Lerngruppen die gleichen sind
    @Override
    public boolean equals(Object o){
        if(this.fileID == null){
            return false;
        }else if(o == null){
            return false;
        }else if(o == this){
            return true;
        }else if(!(o instanceof UploadedFile)){
            return false;
        }
        UploadedFile other = (UploadedFile)o;
        if(this.fileID.equals(other.fileID)){
            return true;
        }else{
            return false;
        }
    }
}