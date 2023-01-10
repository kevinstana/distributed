package gr.hua.distributed.it21774.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gr.hua.distributed.it21774.entity.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AppUserDetailsImpl implements UserDetails {

    // Define fields

    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private String email;

    private String firstName;

    private String lastName;

    private int afm;

    private int amka;

    // Constructor

    public AppUserDetailsImpl(Long id,
                              String username,
                              String password,
                              Collection<? extends GrantedAuthority> authorities,
                              String email,
                              String firstName,
                              String lastName,
                              int afm,
                              int amka) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.afm = afm;
        this.amka = amka;
    }

    public static AppUserDetailsImpl build(AppUser appUser) {
        List<GrantedAuthority> authorities = appUser.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());

        return new AppUserDetailsImpl(
                appUser.getId(),
                appUser.getUsername(),
                appUser.getPassword(),
                authorities,
                appUser.getEmail(),
                appUser.getFirstName(),
                appUser.getLastName(),
                appUser.getAfm(),
                appUser.getAmka()
        );

    }

    public Long getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAfm() {
        return afm;
    }

    public int getAmka() {
        return amka;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        AppUserDetailsImpl appUser = (AppUserDetailsImpl) obj;
        return Objects.equals(id, appUser.id);
    }

}



