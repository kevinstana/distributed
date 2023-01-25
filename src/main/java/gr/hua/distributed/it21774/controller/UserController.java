package gr.hua.distributed.it21774.controller;

import gr.hua.distributed.it21774.config.JwtUtils;
import gr.hua.distributed.it21774.entity.AppUser;
import gr.hua.distributed.it21774.entity.Contract;
import gr.hua.distributed.it21774.entity.ERole;
import gr.hua.distributed.it21774.entity.Role;
import gr.hua.distributed.it21774.payload.request.CreateContractRequest;
import gr.hua.distributed.it21774.payload.request.SignupOrUpdateRequest;
import gr.hua.distributed.it21774.payload.response.ContractResponse;
import gr.hua.distributed.it21774.payload.response.MessageResponse;
import gr.hua.distributed.it21774.payload.response.PrefilledUserFormResponse;
import gr.hua.distributed.it21774.repository.AppUserRepository;
import gr.hua.distributed.it21774.repository.ContractRepository;
import gr.hua.distributed.it21774.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {

    ContractRepository contractRepository;

    AppUserRepository appUserRepository;

    RoleRepository roleRepository;

    PasswordEncoder encoder;

    JwtUtils jwtUtils;

    @Autowired
    public UserController(ContractRepository contractRepository,
                          AppUserRepository appUserRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder encoder,
                          JwtUtils jwtUtils) {
        this.contractRepository = contractRepository;
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok().body(appUserRepository.findAll());
    }

    @GetMapping("/{id}")
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
                    .body(new MessageResponse("You can't view another user's details"));
        }

        AppUser resourceUser;

        try {
            resourceUser = appUserRepository.findById(id).get();
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User does not exist"));
        }

        Set<String> strRoles = new HashSet<>();

        for (Role role : resourceUser.getRoles()) {
            strRoles.add(role.getRole().toString().substring(5));
        }

        return ResponseEntity.ok().body(new PrefilledUserFormResponse(resourceUser.getUsername(),
                "",
                resourceUser.getEmail(),
                resourceUser.getFirstName(),
                resourceUser.getLastName(),
                strRoles,
                resourceUser.getAfm(),
                resourceUser.getAmka()));
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody SignupOrUpdateRequest signUpOrUpdateRequest) {

        if (appUserRepository.existsByUsername(signUpOrUpdateRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Username is already in use"));
        }

        if (appUserRepository.existsByEmail(signUpOrUpdateRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Email is already in use"));
        }

        if (appUserRepository.existsByAfm(signUpOrUpdateRequest.getAfm())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Afm is already in use"));
        }

        if (appUserRepository.existsByAmka(signUpOrUpdateRequest.getAmka())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Amka is already in use"));
        }

        try{
            Long.valueOf(signUpOrUpdateRequest.getAfm());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid afm"));
        }

        try{
            Long.valueOf(signUpOrUpdateRequest.getAmka());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid amka"));
        }

        Set<Role> roles;

        try {
            roles = assignRoles(signUpOrUpdateRequest.getRole());
        }
        catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }

        // Create new user's account
        AppUser appUser = new AppUser(signUpOrUpdateRequest.getUsername(),
                encoder.encode(signUpOrUpdateRequest.getPassword()),
                signUpOrUpdateRequest.getEmail(),
                signUpOrUpdateRequest.getFirstName(),
                signUpOrUpdateRequest.getLastName(),
                roles,
                signUpOrUpdateRequest.getAfm(),
                signUpOrUpdateRequest.getAmka());

        appUser.setId(0L);
        appUserRepository.save(appUser);

        return ResponseEntity.ok()
                .body(new MessageResponse("User registered successfully"));
    }

    @PutMapping("/{id}")
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

        try {
            AppUser existingUser = appUserRepository.findByUsername(signUpOrUpdateRequest.getUsername()).get();
            if (existingUser.getId() != id) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Username is already in use"));
            }
        } catch (NoSuchElementException e) {
        }

        try {
            AppUser existingUser = appUserRepository.findByEmail(signUpOrUpdateRequest.getEmail()).get();
            if (existingUser.getId() != id) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Email is already in use"));
            }
        } catch (NoSuchElementException e) {
        }

        // Check if afm and amka are valid
        try{
            Long.valueOf(signUpOrUpdateRequest.getAfm());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid afm"));
        }

        try{
            Long.valueOf(signUpOrUpdateRequest.getAmka());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid amka"));
        }

        // Check if afm and amka exist
        try {
            AppUser existingUser = appUserRepository.findByAfm(signUpOrUpdateRequest.getAfm()).get();
            if (existingUser.getId() != id) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Afm is already in use"));
            }
        } catch (NoSuchElementException e) {
        }

        try {
            AppUser existingUser = appUserRepository.findByAmka(signUpOrUpdateRequest.getAmka()).get();
            if (existingUser.getId() != id) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Amka is already in use"));
            }
        } catch (NoSuchElementException e) {
        }

        Set<Role> roles;

        try {
            roles = assignRoles(signUpOrUpdateRequest.getRole());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }

        String newPassword = signUpOrUpdateRequest.getPassword();
        if ( !(newPassword.isEmpty())
                && (newPassword.length() >= 8)) {
            signUpOrUpdateRequest.setPassword(encoder.encode(newPassword));
        }
        else {
            signUpOrUpdateRequest.setPassword("");
        }

        appUser.setUpdates(appUser, signUpOrUpdateRequest);
        appUser.setRoles(roles);

        appUserRepository.save(appUser);

        return ResponseEntity.ok(new MessageResponse("User updated"));
    }

    @DeleteMapping("/{id}")
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

        for (Role tempRole : appUser.getRoles()) {
            if (tempRole.getRole().toString().equals("ROLE_ADMIN")) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Can't delete admin"));
            }
        }

        appUserRepository.deleteById(id);

        return ResponseEntity.ok(new MessageResponse("User deleted"));
    }

    @GetMapping("/{id}/contract")
    @PreAuthorize("hasRole('LAWYER') or hasRole('CLIENT')")
    public ResponseEntity<?> getUserContract(HttpServletRequest request,
                                             @PathVariable Long id) {

        Long requestingUserId = jwtUtils.getUserIdFromJwt(request);

        if (requestingUserId != id) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("You can't view another user's contract"));
        }

        AppUser requestingUser = appUserRepository.findById(requestingUserId).get();

        if (requestingUser.getContract() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You don't have a contract"));
        }

        Contract contract = requestingUser.getContract();
        List<String> membersAndAnswers = new ArrayList<>();
        List<AppUser> users = contract.getAppUser();

        for ( AppUser tempUser : users) {
            membersAndAnswers.add(tempUser.getFirstName()
                    + " " + tempUser.getLastName() + ": "
                    +tempUser.getAnswer());
        }

        membersAndAnswers.add(requestingUser.getAnswer());

        return ResponseEntity.ok(new ContractResponse(contract.getText(),
                contract.getDateCreated(),
                contract.getDateApproved(),
                contract.getStatus(),
                membersAndAnswers));
    }

    @PostMapping("/{id}/contract")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<?> createContract(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody CreateContractRequest createContractRequest) {

        Long requestingUserId = jwtUtils.getUserIdFromJwt(request);

        if (requestingUserId != id) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("You can't create another user's contract"));
        }

        AppUser requestingUser = appUserRepository.findById(requestingUserId).get();

        if (requestingUser.getContract() != null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You already have a contract"));
        }

        // Get the local time and assign it to a String
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String dateCreated = dateTimeFormatter.format(now);
        String text = createContractRequest.getText();

        Contract contract = new Contract(text, dateCreated, "", "In Progress");
        contract.setId(0L);

        for (String tempAfm : createContractRequest.getAfm()) {
            try {
                Long.valueOf(tempAfm);
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid afm: " + tempAfm));
            }
        }

        Set<String> afm = createContractRequest.getAfm();

        // Check for duplicate afm
        if (afm.size() != 4) {
            return ResponseEntity.badRequest().body(new MessageResponse("The afms must be unique"));
        }

        int lawyerCount = 0;
        int clientCount = 0;

        for (String tempAfm : afm) {

            AppUser contractMember;

            try {
                contractMember = appUserRepository.findByAfm(tempAfm).get();
                if (contractMember.getContract() != null) {
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse(
                                    contractMember.getAfm()
                                            + " already has a contract"));
                }

                boolean isClient = false;
                boolean isLawyer = false;
                for (Role tempRole : contractMember.getRoles()) {
                    if (tempRole.getRole().toString().equals("ROLE_CLIENT")) {
                        clientCount++;
                        isClient = true;
                    }
                    if (tempRole.getRole().toString().equals("ROLE_LAWYER")) {
                        lawyerCount++;
                        isLawyer = true;
                    }
                }

                if (!isClient && !isLawyer) {
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse("There must be 2 lawyers and 2 clients"));
                }

            } catch (NoSuchElementException e) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Afm does not exist: " + tempAfm));
            }


            // Associate contract to users
            contract.addAppUsers(contractMember);
        }

        if (clientCount < 2 || lawyerCount < 2) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("There must be 2 lawyers and 2 clients"));
        }

        contractRepository.save(contract);

        return ResponseEntity.ok()
                .body(new MessageResponse("Contract Created"));
    }

    @PutMapping("/{id}/contract")
    @PreAuthorize("hasRole('LAWYER') or hasRole('CLIENT')")
    public ResponseEntity<?> answerContract(HttpServletRequest request,
                                            @PathVariable Long id) {

        Long requestingUserId = jwtUtils.getUserIdFromJwt(request);

        if (requestingUserId != id) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("You can't answer another user's contract"));
        }

        AppUser requestingUser = appUserRepository.findById(id).get();

        if (requestingUser.getContract() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You don't have any contracts"));
        }

        if (requestingUser.getAnswer().equals("Yes")) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You have already answered"));
        }

        requestingUser.setAnswer("Yes");
        appUserRepository.save(requestingUser);

        Contract contract = requestingUser.getContract();
        List<AppUser> contractMembers = contract.getAppUser();

        /*
        ΠΡΟΓΡΑΜΜΑΤΙΣΜΟΣ 1 incoming!!
        */
        int i = 0;
        for (AppUser tempUser : contractMembers) {
            if (tempUser.getAnswer().equals("Yes")) {
                i += 1;
            }
        }

        if (i == 4) {
            contract.setStatus("Answered");
            contractRepository.save(contract);
        }

        return ResponseEntity.ok(new MessageResponse("You have answered successfully"));
    }

    public Set<Role> assignRoles(Set<String> roles) throws RuntimeException {

        Set<Role> assignRoles = new HashSet<>();

        if (roles.isEmpty()) {
            Role userRole = roleRepository.findByRole(ERole.ROLE_CLIENT)
                    .orElseThrow(() -> new RuntimeException("Role " + ERole.ROLE_CLIENT + " is not found"));
            assignRoles.add(userRole);
        } else {
            roles.forEach(role -> {
                switch (role) {
                    case "lawyer":
                        Role lawyerRole = roleRepository.findByRole(ERole.ROLE_LAWYER)
                                .orElseThrow(() -> new RuntimeException("Role " + role + " is not found"));
                        assignRoles.add(lawyerRole);

                        break;
                    case "notary":
                        Role notaryRole = roleRepository.findByRole(ERole.ROLE_NOTARY)
                                .orElseThrow(() -> new RuntimeException("Role " + role + " is not found"));
                        assignRoles.add(notaryRole);

                        break;
                    case "admin":
                        Role adminRole = roleRepository.findByRole(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role " + role + " is not found"));
                        assignRoles.add(adminRole);

                        break;
                    case "client":
                        Role clientRole = roleRepository.findByRole(ERole.ROLE_CLIENT)
                                .orElseThrow(() -> new RuntimeException("Role " + role + " is not found"));
                        assignRoles.add(clientRole);
                        break;
                    default:
                        throw new RuntimeException("Role " + role + " is not found");
                }
            });
        }

        return assignRoles;
    }
 }
