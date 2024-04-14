package gr.hua.distributed.it21774.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gr.hua.distributed.it21774.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {


    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private String email;

    private String firstName;

    private String lastName;

    private String afm;

    private String amka;


    public UserDetailsImpl(Long id,
                              String username,
                              String password,
                              Collection<? extends GrantedAuthority> authorities,
                              String email,
                              String firstName,
                              String lastName,
                              String afm,
                              String amka) {
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

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAfm(),
                user.getAmka()
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

    public String getAfm() {
        return afm;
    }

    public String getAmka() {
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
        UserDetailsImpl appUser = (UserDetailsImpl) obj;
        return Objects.equals(id, appUser.id);
    }

}



