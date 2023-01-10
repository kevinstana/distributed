package gr.hua.distributed.it21774.controller;

import gr.hua.distributed.it21774.entity.AppUser;
import gr.hua.distributed.it21774.entity.ERole;
import gr.hua.distributed.it21774.entity.Role;
import gr.hua.distributed.it21774.payload.request.SignupOrUpdateRequest;
import gr.hua.distributed.it21774.payload.response.MessageResponse;
import gr.hua.distributed.it21774.payload.response.PrefilledUserFormResponse;
import gr.hua.distributed.it21774.repository.AppUserRepository;
import gr.hua.distributed.it21774.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping
public class UserController {

    AppUserRepository appUserRepository;

    RoleRepository roleRepository;

    PasswordEncoder encoder;

    @Autowired
    public UserController(AppUserRepository appUserRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder encoder) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    @GetMapping("/userForm/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> prefilledUserUpdateForm(@PathVariable int id) {

        Long theId = Long.valueOf(id);
        AppUser appUser;

        try {
            appUser = appUserRepository.findById(theId).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist."));
        }

        Set<Role> roles = appUser.getRoles();
        Set<String> strRoles = new HashSet<>();

        roles.forEach(role -> {
            switch (role.getRole().toString()) {
                case "ROLE_ADMIN":
                    strRoles.add("admin");
                    System.out.println(role.toString());
                    break;
                case "ROLE_LAWYER":
                    strRoles.add("lawyer");
                    break;
                case "ROLE_NOTARY":
                    strRoles.add("notary");
                    break;
                case "ROLE_CLIENT":
                    strRoles.add("client");
            }
        });

        return ResponseEntity.ok(new PrefilledUserFormResponse(appUser.getUsername(),
                                                        "",
                                                                appUser.getEmail(),
                                                                appUser.getFirstName(),
                                                                appUser.getLastName(),
                                                                strRoles,
                                                                appUser.getAfm(),
                                                                appUser.getAmka()));
    }

    @PutMapping("/userForm/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable int id,
                                        @Valid @RequestBody SignupOrUpdateRequest signUpOrUpdateRequest) {

        Long theId = Long.valueOf(id);
        AppUser appUser;

        try {
            appUser = appUserRepository.findById(theId).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist."));
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
                && ((appUser.getAfm()) != signUpOrUpdateRequest.getAfm())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Afm is already in use: "
                            + signUpOrUpdateRequest.getAfm()));
        }

        if (appUserRepository.existsByAmka(signUpOrUpdateRequest.getAmka())
                && ((appUser.getAmka()) != signUpOrUpdateRequest.getAmka())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Amka is already in use: "
                            + signUpOrUpdateRequest.getAmka()));
        }

        appUser.setUsername(signUpOrUpdateRequest.getUsername());
        appUser.setPassword(encoder.encode(signUpOrUpdateRequest.getPassword()));
        appUser.setEmail(signUpOrUpdateRequest.getEmail());
        appUser.setFirstName(signUpOrUpdateRequest.getFirstName());
        appUser.setLastName(signUpOrUpdateRequest.getLastName());
        appUser.setAfm(signUpOrUpdateRequest.getAfm());
        appUser.setAmka(signUpOrUpdateRequest.getAmka());

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
        appUserRepository.save(appUser);

        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }

    @DeleteMapping("/appUser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {

        Long theId = Long.valueOf(id);
        AppUser appUser;

        try {
            appUser = appUserRepository.findById(theId).get();

            if (appUser.getContract() != null) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Cannot delete user with active contract."));
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist."));
        }

        appUserRepository.deleteById(theId);

        return ResponseEntity.ok(new MessageResponse("User deleted."));
    }
}
