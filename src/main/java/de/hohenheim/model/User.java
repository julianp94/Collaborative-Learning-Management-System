package de.hohenheim.model;

import javax.persistence.*;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by Julian on 04.06.2016.
 */

@Entity
@DiscriminatorValue(value = "USER")
public class User extends Followable{


    @Column(name = "NAME")
    private String name;
    @Column(name = "VORNAME")
    private String vorname;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "EMAIL")
    private String eMail;


    public String getName() {
        return name;
    }

    public String getVorname() {
        return vorname;
    }

    public String getPassword() {
        return password;
    }

    public String geteMail() {
        return eMail;
    }

    public void setName(String username) {
        this.name = username;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }


}
