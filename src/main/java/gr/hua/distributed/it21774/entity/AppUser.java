package gr.hua.distributed.it21774.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import gr.hua.distributed.it21774.payload.request.SignupOrUpdateRequest;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "app_user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "afm"),
                @UniqueConstraint(columnNames = "amka")
        })
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "username")
    private String username;

    @NotNull
    @Size(min = 8, max = 120)
    @Column(name = "password")
    @JsonIgnore
    private String password;

    @NotNull
    @Size(max = 50)
    @Email
    @Column(name = "email")
    private String email;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Size(min = 2, max = 20)
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "afm")
    private Long afm;

    @NotNull
    @Column(name = "amka")
    private Long amka;

    @Column(name = "answer")
    private String answer;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = { CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH}
    )
    @JoinTable(	name = "app_user_role",
            joinColumns = @JoinColumn(name = "app_user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    //@JsonBackReference
    private Set<Role> roles = new HashSet<>();

    // lazy fetch causes json problems
    @ManyToOne(//fetch = FetchType.LAZY,
               cascade = { CascadeType.DETACH,
                           CascadeType.MERGE,
                           CascadeType.PERSIST,
                           CascadeType.REFRESH })
    @JoinColumn(name = "contract_id")
    //@JsonBackReference
    private Contract contract;

    public AppUser() {
    }

    public AppUser(String username,
                   String password,
                   String email,
                   String firstName,
                   String lastName,
                   Set<Role> roles,
                   Long afm,
                   Long amka) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
        this.afm = afm;
        this.amka = amka;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAfm() {
        return afm;
    }

    public void setAfm(Long afm) {
        this.afm = afm;
    }

    public Long getAmka() {
        return amka;
    }

    public void setAmka(Long amka) {
        this.amka = amka;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public void setUpdates(AppUser appUser, SignupOrUpdateRequest signupOrUpdateRequest) {
        this.username = signupOrUpdateRequest.getUsername();
        this.email = signupOrUpdateRequest.getEmail();
        this.firstName = signupOrUpdateRequest.getFirstName();
        this.lastName = signupOrUpdateRequest.getLastName();
        this.afm = signupOrUpdateRequest.getAfm();
        this.amka = signupOrUpdateRequest.getAmka();
        if ( !signupOrUpdateRequest.getPassword().isEmpty()) {
            this.password = signupOrUpdateRequest.getPassword();
        }
    }
}
