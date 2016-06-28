package de.hohenheim.model;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by Julian on 26.06.2016.
 */
@Entity
public class UserFeedEntry extends Comment {

    @ManyToOne
    SopraUser user;

    public SopraUser getUser() {
        return user;
    }

    public void setUser(SopraUser user) {
        this.user = user;
    }

}
