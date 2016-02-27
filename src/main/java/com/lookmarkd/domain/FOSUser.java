package com.lookmarkd.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "fos_user")
public class FOSUser implements Serializable {

    @Id
    private long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "instagram_access_token", nullable = true)
    private String instagramAccessToken;


}
