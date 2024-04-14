package gr.hua.distributed.it21774.payload.response;

import gr.hua.distributed.it21774.entity.ContractStatusEnum;

import java.util.List;

public class ContractResponse {

    private String text;

    private Long dateCreated;

    private Long dateApproved;

    private ContractStatusEnum status;

    private List<String> membersAndAnswers;

    public ContractResponse() {
    }

    public ContractResponse(String text,
                            Long dateCreated,
                            Long dateApproved,
                            ContractStatusEnum status,
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

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(Long dateApproved) {
        this.dateApproved = dateApproved;
    }

    public ContractStatusEnum getStatus() {
        return status;
    }

    public void setStatus(ContractStatusEnum status) {
        this.status = status;
    }

    public List<String> getMembers() {
        return membersAndAnswers;
    }

    public void setMembers(List<String> membersAndAnswers) {
        this.membersAndAnswers = membersAndAnswers;
    }
}
