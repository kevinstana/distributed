package gr.hua.distributed.it21774.payload.response;

import gr.hua.distributed.it21774.entity.AppUser;

import java.util.List;

public class ContractResponse {

    private String text;

    private String dateCreated;

    private String dateApproved;

    private String status;

    private List<String> membersAndAnswers;

    public ContractResponse() {
    }

    public ContractResponse(String text,
                            String dateCreated,
                            String dateApproved,
                            String status,
                            List<String> membersAndAnswers) {
        this.text = text;
        this.dateCreated = dateCreated;
        this.dateApproved = dateApproved;
        this.status = status;
        this.membersAndAnswers = membersAndAnswers;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(String dateApproved) {
        this.dateApproved = dateApproved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getMembers() {
        return membersAndAnswers;
    }

    public void setMembers(List<String> membersAndAnswers) {
        this.membersAndAnswers = membersAndAnswers;
    }
}
