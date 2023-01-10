package gr.hua.distributed.it21774.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import gr.hua.distributed.it21774.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "text")
    private String text;

    @Column(name = "date_created")
    private String dateCreated;

    @Column(name = "date_confirmed")
    private String dateConfirmed;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "contract",
               fetch = FetchType.LAZY,
               cascade = { CascadeType.DETACH,
                           CascadeType.MERGE,
                           CascadeType.PERSIST,
                           CascadeType.REFRESH })
    @JsonManagedReference
    //@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property="@UUID")
    private List<AppUser> appUser;

    public Contract() {
    }

    public Contract(String text,
                    String dateCreated,
                    String dateConfirmed,
                    String status) {
        this.text = text;
        this.dateCreated = dateCreated;
        this.dateConfirmed = dateConfirmed;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateConfirmed() {
        return dateConfirmed;
    }

    public void setDateConfirmed(String dateConfirmed) {
        this.dateConfirmed = dateConfirmed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AppUser> getAppUser() {
        return appUser;
    }

    public void setAppUser(List<AppUser> appUser) {
        this.appUser = appUser;
    }

    public void addAppUsers(AppUser appUser) {

        if (this.appUser == null) {
            this.appUser = new ArrayList<>();
        }

        this.appUser.add(appUser);

        appUser.setContract(this);
        appUser.setAnswer("no");
    }

    public void detachUsers() {
        for (AppUser tempUser : this.getAppUser()) {
            tempUser.setContract(null);
            tempUser.setAnswer("");
        }

        this.setAppUser(null);
    }
}
