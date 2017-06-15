package net.ddns.office.drive.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;

/**
 * Created by NPOST on 2017-06-07.
 */
@Entity
public class Authorities {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "AUTHORITIES_ID")
    private Long id;

    @NotEmpty(message = "Username is required.")
    private String username;

    @Email(message = "Please provide a valid email address.")
    @NotEmpty(message = "Email is required")
    @Column(name = "EMAIL")
    private String email;

    @NotEmpty(message = "authority is required.")
    private String authority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @JsonBackReference
    private User user;

    public Authorities() {
    }

    public Authorities(String username, String email, String authority, User user) {
        this.username = username;
        this.email = email;
        this.authority = authority;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
