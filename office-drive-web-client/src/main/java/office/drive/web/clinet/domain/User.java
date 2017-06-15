package office.drive.web.clinet.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NPOST on 2017-06-07.
 */
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long id;

    @NotEmpty(message = "Name is required.")
    @Column(name = "USER_NAME")
    private String name;

    @Email(message = "Please provide a valid email address.")
    @NotEmpty(message = "Email is required.")
    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @NotEmpty(message = "Password is required.")
    private String password;

    @NotEmpty(message = "enable is required that is 0 or 1.")
    private boolean enabled;

    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Authorities> authorities = new ArrayList<>();

    public User() {
    }

    public User(User user) {
        this.id = user.id;
        this.name = user.name;
        this.email = user.email;
        this.password = user.password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabledMy() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Authorities> getAuthoritiesList() {
        return authorities;
    }

    public void setAuthoritiesList(List<Authorities> authorities) {
        this.authorities = authorities;
    }
}
