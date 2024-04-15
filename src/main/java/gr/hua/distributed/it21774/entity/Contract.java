package gr.hua.distributed.it21774.entity;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
    @Size(min = 10)
    private String text;

    @NotNull
    @Column(name = "date_created")
    private Long dateCreated;

    @Column(name = "date_approved")
    private Long dateApproved;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContractStatusEnum status;

    @OneToMany(mappedBy = "contract",
               fetch = FetchType.LAZY,
               cascade = { CascadeType.DETACH,
                           CascadeType.MERGE,
                           CascadeType.PERSIST,
                           CascadeType.REFRESH })
//    @JsonBackReference
//    @JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property="@UUID")
    @JsonIgnore
    private List<User> user;

    public Contract() {
    }

    public Contract(String text,
                    Long dateCreated,
                    Long dateApproved,
                    ContractStatusEnum status) {
        this.text = text;
        this.dateCreated = dateCreated;
        this.dateApproved = dateApproved;
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

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(Long dateApproved) {
        this.dateApproved = dateApproved;
    }

    public ContractStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ContractStatusEnum status) {
        this.status = status;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }

    public void addUsers(User user) {

        if (this.user == null) {
            this.user = new ArrayList<>();
        }

        this.user.add(user);

        user.setContract(this);
        user.setAnswer("No");
    }

    public void detachUsers() {
        for (User tempUser : this.getUser()) {
            tempUser.setContract(null);
            tempUser.setAnswer("");
        }

        this.setUser(null);
    }
}
