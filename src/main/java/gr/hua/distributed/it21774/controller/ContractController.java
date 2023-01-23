package gr.hua.distributed.it21774.controller;

import gr.hua.distributed.it21774.config.JwtUtils;
import gr.hua.distributed.it21774.entity.AppUser;
import gr.hua.distributed.it21774.entity.Contract;
import gr.hua.distributed.it21774.payload.response.ContractResponse;
import gr.hua.distributed.it21774.payload.response.MessageResponse;
import gr.hua.distributed.it21774.repository.AppUserRepository;
import gr.hua.distributed.it21774.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/contracts")
public class ContractController {

    ContractRepository contractRepository;

    AppUserRepository appUserRepository;

    JwtUtils jwtUtils;

    @Autowired
    public ContractController(ContractRepository contractRepository,
                              AppUserRepository appUserRepository,
                              JwtUtils jwtUtils) {
        this.contractRepository = contractRepository;
        this.appUserRepository = appUserRepository;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN') or hasRole('NOTARY')")
    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ResponseEntity<?> getOneContract(@PathVariable Long id) {

        Contract contract;

        try {
            contract = contractRepository.findById(id).get();
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Contract does not exist"));
        }

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

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
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

    @DeleteMapping("/{id}/force-delete")
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

}
