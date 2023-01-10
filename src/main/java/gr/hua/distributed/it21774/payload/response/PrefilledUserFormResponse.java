package gr.hua.distributed.it21774.payload.response;

import java.util.Set;

public class PrefilledUserFormResponse {

    private String username;

    private String password;

    private String email;

    private String firstName;

    private String lastName;

    private Set<String> role;

    private int afm;

    private int amka;

    public PrefilledUserFormResponse() {
    }

    public PrefilledUserFormResponse(String username,
                                     String password,
                                     String email,
                                     String firstName,
                                     String lastName,
                                     Set<String> role,
                                     int afm,
                                     int amka) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.afm = afm;
        this.amka = amka;
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

    public void setEmail(String email) {
        this.email = email;
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

    public Set<String> getRole() {
        return role;
    }

    public void setRole(Set<String> role) {
        this.role = role;
    }

    public int getAfm() {
        return afm;
    }

    public void setAfm(int afm) {
        this.afm = afm;
    }

    public int getAmka() {
        return amka;
    }

    public void setAmka(int amka) {
        this.amka = amka;
    }
}
