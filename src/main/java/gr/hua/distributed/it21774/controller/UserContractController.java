package gr.hua.distributed.it21774.controller;

import gr.hua.distributed.it21774.entity.AppUser;
import gr.hua.distributed.it21774.entity.Contract;
import gr.hua.distributed.it21774.payload.request.AnswerRequest;
import gr.hua.distributed.it21774.payload.request.ConfirmRequest;
import gr.hua.distributed.it21774.payload.request.CreateContractRequest;
import gr.hua.distributed.it21774.payload.response.ContractResponse;
import gr.hua.distributed.it21774.payload.response.MessageResponse;
import gr.hua.distributed.it21774.repository.AppUserRepository;
import gr.hua.distributed.it21774.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@RestController
public class UserContractController {

    AppUserRepository appUserRepository;

    ContractRepository contractRepository;

    @Autowired
    public UserContractController(AppUserRepository appUserRepository,
                                  ContractRepository contractRepository) {
        this.appUserRepository = appUserRepository;
        this.contractRepository = contractRepository;
    }

    @GetMapping("/appUsers/{id}/contract")
    public ResponseEntity<?> getUserContract(@PathVariable int id) {

        Long theId = Long.valueOf(id);

        AppUser appUser;

        try {
            appUser = appUserRepository.findById(theId).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist."));
        }

        if (appUser.getContract() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You don't have any contracts."));
        }

        Contract contract = appUser.getContract();
        List<String> membersAndAnswers = new ArrayList<>();
        List<AppUser> users = contract.getAppUser();

        for ( AppUser tempUser : users) {
            membersAndAnswers.add(tempUser.getFirstName()
                    + " " + tempUser.getLastName() + ": "
                    +tempUser.getAnswer());
        }

        return ResponseEntity.ok(new ContractResponse(contract.getText(),
                                                      contract.getDateCreated(),
                                                        contract.getDateConfirmed(),
                                                        contract.getStatus(),
                                                        membersAndAnswers));
    }

    @PostMapping("/appUsers/{id}/contract/")
    @PreAuthorize("hasRole('LAWYER')")
    public ResponseEntity<?> createContract(@PathVariable int id,
                                            @Valid @RequestBody CreateContractRequest createContractRequest) {

        Long theId = Long.valueOf(id);

        AppUser appUser;

        try {
            appUser = appUserRepository.findById(theId).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist."));
        }

        if (appUser.getContract() != null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You already have a contract."));
        }

        // Get the local datetime and assign it to a String
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        String dateCreated = dateTimeFormatter.format(now);

        // Get the text from the request
        String text = createContractRequest.getText();

        // Create the contract
        Contract contract = new Contract(text, dateCreated, "", "");

        // Associate contract to users
        Set<Long> afm = createContractRequest.getAfm();

        for (Long tempAfm : afm) {

            AppUser appUser2;

            try {
                appUser2 = appUserRepository.findByAfm(tempAfm).get();
                if (appUser2.getContract() != null) {
                    return ResponseEntity.badRequest().body(new MessageResponse(appUser2.getUsername()
                                                                                + " already has a contract"));
                }
            } catch (NoSuchElementException e) {
                return ResponseEntity.badRequest().body(new MessageResponse("Afm does not exist: " + tempAfm));
            }
        }

        for (Long tempAfm : afm) {
            AppUser appUser3 = appUserRepository.findByAfm(tempAfm).get();
            contract.addAppUsers(appUser3);
        }

        contractRepository.save(contract);

        return ResponseEntity.ok("Contract Created");
    }

    @PutMapping("/contract/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ResponseEntity<?> confirmContract(@PathVariable int id,
                                             @Valid @RequestBody ConfirmRequest confirmRequest) {

        Long theId = Long.valueOf(id);

        Contract contract;

        try {
            contract = contractRepository.findById(theId).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Contract does not exist."));
        }

        List<AppUser> appUsers = contract.getAppUser();

        for (AppUser appUser : appUsers) {
            if (appUser.getAnswer().contains("no")) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Not all members have answered yet."));
            }
        }

        String status = confirmRequest.getStatus();

        // Get the local datetime and assign it to a String
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        String dateConfirmed = dateTimeFormatter.format(now);

        contract.setDateConfirmed(dateConfirmed);
        contract.setStatus(status);

        contractRepository.save(contract);

        return ResponseEntity.ok("Contract Confirmed");
    }

    @DeleteMapping("/contract/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteContract(@PathVariable int id) {

        Long theId = Long.valueOf(id);
        Contract contract;

        try {
            contract = contractRepository.findById(theId).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Contract does not exist."));
        }

        if (contract.getStatus().contains("true")) {

            contract.detachUsers();
            contractRepository.deleteById(theId);
        }
        else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Cannot delete active contract."));
        }

        return ResponseEntity.ok("Contract Deleted");
    }

    @PutMapping("/appUsers/{id}/contract")
    @PreAuthorize("hasRole('CLIENT') or hasRole('LAWYER')")
    public ResponseEntity<?> answerContract(@PathVariable int id,
                                            @Valid @RequestBody AnswerRequest answerRequest) {

        Long theId = Long.valueOf(id);

        AppUser appUser;

        try {
            appUser = appUserRepository.findById(theId).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist."));
        }

        if (appUser.getContract() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You don't have any contracts."));
        }

        if (appUser.getAnswer().contains("yes")) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("You have already answered."));
        }

        appUser.setAnswer(answerRequest.getAnswer().toString());
        appUserRepository.save(appUser);

        return ResponseEntity.ok(new MessageResponse("You have answered successfully."));
    }
}
