package gr.hua.distributed.it21774.controller;

import gr.hua.distributed.it21774.config.JwtUtils;
import gr.hua.distributed.it21774.entity.AppUser;
import gr.hua.distributed.it21774.entity.Contract;
import gr.hua.distributed.it21774.entity.ERole;
import gr.hua.distributed.it21774.entity.Role;
import gr.hua.distributed.it21774.payload.request.SignupOrUpdateRequest;
import gr.hua.distributed.it21774.payload.response.MessageResponse;
import gr.hua.distributed.it21774.payload.response.PrefilledUserFormResponse;
import gr.hua.distributed.it21774.repository.AppUserRepository;
import gr.hua.distributed.it21774.repository.ContractRepository;
import gr.hua.distributed.it21774.repository.RoleRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class UserController {

    AppUserRepository appUserRepository;

    RoleRepository roleRepository;

    PasswordEncoder encoder;

    JwtUtils jwtUtils;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    public UserController(AppUserRepository appUserRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder encoder,
                          JwtUtils jwtUtils) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/usersall")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {

        return ResponseEntity.ok().body(appUserRepository.findAll());
    }

    @GetMapping("/usersall/{id}")
    public ResponseEntity<?> getUser(HttpServletRequest request,
                                     @PathVariable Long id) {

        Long requestingUserId = jwtUtils.getUserIdFromJwt(request);
        AppUser requestingUser = appUserRepository.findById(requestingUserId).get();;

        boolean isAdmin = false;
        for (Role tempRole: requestingUser.getRoles()) {
            if (tempRole.getRole().toString().equals("ROLE_ADMIN")) {
                isAdmin = true;
                break;
            }
        }

        if ( (requestingUserId != id) && (isAdmin == false) ) {
            return ResponseEntity.badRequest()
                    .body("You can't view another user's details");
        }

        AppUser resourceUser;

        try {
            resourceUser = appUserRepository.findById(id).get();
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest()
                    .body("User does not exist");
        }

        return ResponseEntity.ok().body(resourceUser);
    }

    @PostMapping("/registerUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody SignupOrUpdateRequest signUpOrUpdateRequest) {

        if (appUserRepository.existsByUsername(signUpOrUpdateRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken: "
                            + signUpOrUpdateRequest.getUsername()));
        }

        if (appUserRepository.existsByEmail(signUpOrUpdateRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use: "
                            + signUpOrUpdateRequest.getEmail()));
        }

        if (appUserRepository.existsByAfm(signUpOrUpdateRequest.getAfm())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Afm is already in use: "
                            + signUpOrUpdateRequest.getAfm()));
        }

        if (appUserRepository.existsByAmka(signUpOrUpdateRequest.getAmka())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Amka is already in use: "
                            + signUpOrUpdateRequest.getAmka()));
        }

        // Create new user's account
        AppUser appUser = new AppUser(signUpOrUpdateRequest.getUsername(),
                encoder.encode(signUpOrUpdateRequest.getPassword()),
                signUpOrUpdateRequest.getEmail(),
                signUpOrUpdateRequest.getFirstName(),
                signUpOrUpdateRequest.getLastName(),
                signUpOrUpdateRequest.getAfm(),
                signUpOrUpdateRequest.getAmka());

        Set<String> strRoles = signUpOrUpdateRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRole(ERole.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "lawyer":
                        Role lawyerRole = roleRepository.findByRole(ERole.ROLE_LAWYER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(lawyerRole);

                        break;
                    case "notary":
                        Role notaryRole = roleRepository.findByRole(ERole.ROLE_NOTARY)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(notaryRole);

                        break;
                    case "admin":
                        Role adminRole = roleRepository.findByRole(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    default:
                        Role clientRole = roleRepository.findByRole(ERole.ROLE_CLIENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(clientRole);
                }
            });
        }

        appUser.setRoles(roles);
        appUser.setId(0L);
        appUserRepository.save(appUser);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/updateUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> prefilledUserForm(@PathVariable Long id) {

        AppUser appUser;

        try {
            appUser = appUserRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist."));
        }

        Set<Role> roles = appUser.getRoles();
        Set<String> strRoles = new HashSet<>();

        for (Role role : roles) {
            strRoles.add(role.getRole().toString());
        }

        return ResponseEntity.ok(new PrefilledUserFormResponse(appUser.getUsername(),
                                                                "",
                                                                appUser.getEmail(),
                                                                appUser.getFirstName(),
                                                                appUser.getLastName(),
                                                                strRoles,
                                                                appUser.getAfm(),
                                                                appUser.getAmka()));
    }

    @PutMapping("/updateUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id,
                                        @Valid @RequestBody SignupOrUpdateRequest signUpOrUpdateRequest) {

        AppUser appUser;

        try {
            appUser = appUserRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist"));
        }

        if (appUserRepository.existsByUsername(signUpOrUpdateRequest.getUsername())
                && !(appUser.getUsername()).equals(signUpOrUpdateRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken: "
                            + signUpOrUpdateRequest.getUsername()));
        }

        if (appUserRepository.existsByEmail(signUpOrUpdateRequest.getEmail())
                && !(appUser.getEmail()).equals(signUpOrUpdateRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use: "
                            + signUpOrUpdateRequest.getEmail()));
        }

        if (appUserRepository.existsByAfm(signUpOrUpdateRequest.getAfm())
                && !((appUser.getAfm()) != signUpOrUpdateRequest.getAfm())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Afm is already in use: "
                            + signUpOrUpdateRequest.getAfm()));
        }

        if (appUserRepository.existsByAmka(signUpOrUpdateRequest.getAmka())
                && !((appUser.getAmka()) != signUpOrUpdateRequest.getAmka())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Amka is already in use: "
                            + signUpOrUpdateRequest.getAmka()));
        }

        // Set updated info
        appUser.setUsername(signUpOrUpdateRequest.getUsername());
        if (!signUpOrUpdateRequest.getPassword().equals("")) {
            appUser.setPassword(encoder.encode(signUpOrUpdateRequest.getPassword()));
        }
        appUser.setEmail(signUpOrUpdateRequest.getEmail());
        appUser.setFirstName(signUpOrUpdateRequest.getFirstName());
        appUser.setLastName(signUpOrUpdateRequest.getLastName());
        appUser.setAfm(signUpOrUpdateRequest.getAfm());
        appUser.setAmka(signUpOrUpdateRequest.getAmka());

        Set<String> strRoles = signUpOrUpdateRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByRole(ERole.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Error: Role " + ERole.ROLE_CLIENT + " is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "lawyer":
                        Role lawyerRole = roleRepository.findByRole(ERole.ROLE_LAWYER)
                                .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found"));
                        roles.add(lawyerRole);

                        break;
                    case "notary":
                        Role notaryRole = roleRepository.findByRole(ERole.ROLE_NOTARY)
                                .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found"));
                        roles.add(notaryRole);

                        break;
                    case "admin":
                        Role adminRole = roleRepository.findByRole(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found"));
                        roles.add(adminRole);

                        break;
                    default:
                        Role clientRole = roleRepository.findByRole(ERole.ROLE_CLIENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role " + role + " is not found"));
                        roles.add(clientRole);
                }
            });
        }

        appUser.setRoles(roles);
        appUserRepository.save(appUser);

        return ResponseEntity.ok(new MessageResponse("User updated"));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        AppUser appUser;

        try {
            appUser = appUserRepository.findById(id).get();

            if (appUser.getContract() != null) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Cannot delete user with active contract"));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist"));
        }

        appUserRepository.deleteById(id);

        return ResponseEntity.ok(new MessageResponse("User deleted"));
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
