package gr.hua.distributed.it21774.controller;

import gr.hua.distributed.it21774.config.JwtUtils;
import gr.hua.distributed.it21774.entity.User;
import gr.hua.distributed.it21774.entity.Contract;
import gr.hua.distributed.it21774.entity.ContractStatusEnum;
import gr.hua.distributed.it21774.payload.response.ContractResponse;
import gr.hua.distributed.it21774.payload.response.Member;
import gr.hua.distributed.it21774.payload.response.MessageResponse;
import gr.hua.distributed.it21774.repository.UserRepository;
import gr.hua.distributed.it21774.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/contracts")
public class ContractController {

    ContractRepository contractRepository;

    UserRepository appUserRepository;

    JwtUtils jwtUtils;

    @Autowired
    public ContractController(ContractRepository contractRepository,
                              UserRepository appUserRepository,
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

        List<Member> members = new ArrayList<>();
        List<User> users = contract.getUser();

        for (User tempUser : users) {
            String fullName = tempUser.getFirstName() + " " + tempUser.getLastName();
            String answer = tempUser.getAnswer();
            members.add(new Member(fullName, answer));
        }

        return ResponseEntity.ok(new ContractResponse(contract.getText(),
                contract.getDateCreated(),
                contract.getDateApproved(),
                contract.getStatus(),
                members));
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

        if (contract.getStatus().equals(ContractStatusEnum.APPROVED)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("You have already approved this contract"));
        }

        List<User> users = contract.getUser();

        for (User user : users) {
            if (user.getAnswer().equals("No")) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Not all members have answered yet"));
            }
        }

        Long dateApproved = System.currentTimeMillis();

        contract.setDateApproved(dateApproved);
        contract.setStatus(ContractStatusEnum.APPROVED);
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

        if (contract.getStatus().equals(ContractStatusEnum.APPROVED)) {

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
