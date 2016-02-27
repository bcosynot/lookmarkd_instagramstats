package com.lookmarkd.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "fos_user")
public class FOSUser implements Serializable {

    private static final long serialVersionUID = 4635206975487297268L;

    @Id
    @Column(name="id")
    private int id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "instagram_access_token", nullable = true)
    private String instagramAccessToken;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInstagramAccessToken() {
        return instagramAccessToken;
    }

    public void setInstagramAccessToken(String instagramAccessToken) {
        this.instagramAccessToken = instagramAccessToken;
    }
}
