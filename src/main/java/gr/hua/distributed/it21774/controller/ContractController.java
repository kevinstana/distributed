package gr.hua.distributed.it21774.controller;

import gr.hua.distributed.it21774.config.JwtUtils;
import gr.hua.distributed.it21774.entity.AppUser;
import gr.hua.distributed.it21774.entity.Contract;
import gr.hua.distributed.it21774.payload.request.CreateContractRequest;
import gr.hua.distributed.it21774.payload.response.ContractResponse;
import gr.hua.distributed.it21774.payload.response.MessageResponse;
import gr.hua.distributed.it21774.repository.AppUserRepository;
import gr.hua.distributed.it21774.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials="true")
@RestController
public class ContractController {

    AppUserRepository appUserRepository;

    ContractRepository contractRepository;

    JwtUtils jwtUtils;

    @Autowired
    public ContractController(AppUserRepository appUserRepository,
                              ContractRepository contractRepository,
                              JwtUtils jwtUtils) {
        this.appUserRepository = appUserRepository;
        this.contractRepository = contractRepository;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/contracts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NOTARY')")
    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    @GetMapping("/users/{id}/contract")
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
                    .body(new MessageResponse("You don't have any contracts"));
        }

        Contract contract = requestingUser.getContract();
        List<String> membersAndAnswers = new ArrayList<>();
        List<AppUser> users = contract.getAppUser();

        for ( AppUser tempUser : users) {
            membersAndAnswers.add(tempUser.getFirstName()
                    + " " + tempUser.getLastName() + ": "
                    +tempUser.getAnswer());
        }

        return ResponseEntity.ok(new ContractResponse(contract.getText(),
                                                      contract.getDateCreated(),
                                                      contract.getDateApproved(),
                                                      contract.getStatus(),
                                                      membersAndAnswers));
    }

    @PostMapping("/users/{id}/contract")
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

        Set<Long> afm = createContractRequest.getAfm();

        // Check for duplicate afm
        if (afm.size() < 4) {
            return ResponseEntity.badRequest().body(new MessageResponse("The afms must be unique"));
        }

        for (Long tempAfm : afm) {

            AppUser contractMember;

            try {
                contractMember = appUserRepository.findByAfm(tempAfm).get();
                if (contractMember.getContract() != null) {
                    return ResponseEntity.badRequest()
                            .body(new MessageResponse(
                                    contractMember.getFirstName()
                                    + " "
                                    + contractMember.getLastName()
                                    + " already has a contract"));
                }
            } catch (NoSuchElementException e) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Afm does not exist: " + tempAfm));
            }

            // Associate contract to users
            contract.addAppUsers(contractMember);
        }

        contractRepository.save(contract);

        return ResponseEntity.ok()
                .body(new MessageResponse("Contract Created"));
    }

    @PutMapping("/contracts/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ResponseEntity<?> approveContract(@PathVariable Long id) {

        Contract contract;

        try {
            contract = contractRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Contract does not exist"));
        }

        if (contract.getStatus().equals("Approved")) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("You have already approved this contract"));
        }

        List<AppUser> appUsers = contract.getAppUser();

        for (AppUser appUser : appUsers) {
            if (appUser.getAnswer().equals("No")) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Not all members have answered yet"));
            }
        }

        // Get the local time and assign it to a String
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        String dateApproved = dateTimeFormatter.format(now);

        contract.setDateApproved(dateApproved);
        contract.setStatus("Approved");
        contractRepository.save(contract);

        return ResponseEntity.ok().body(new MessageResponse("Contract Approved"));
    }

    @DeleteMapping("/contracts/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteContract(@PathVariable Long id) {

        Contract contract;

        try {
            contract = contractRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Contract does not exist."));
        }

        if (contract.getStatus().equals("Approved")) {

            contract.detachUsers();
            contractRepository.deleteById(id);
        }
        else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Cannot delete active contract"));
        }

        return ResponseEntity.ok()
                .body(new MessageResponse("Contract Deleted"));
    }

    @DeleteMapping("/forceDeleteContract/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> forceDeleteContract(@PathVariable Long id) {

        Contract contract;

        try {
            contract = contractRepository.findById(id).get();
        }
        catch (EmptyResultDataAccessException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Contract does not exist"));
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Contract does not exist"));
        }

        contract.detachUsers();
        contractRepository.deleteById(id);

        return ResponseEntity.ok()
                .body(new MessageResponse("Contract deleted"));
    }

    @PutMapping("/users/{id}/contract")
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

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
