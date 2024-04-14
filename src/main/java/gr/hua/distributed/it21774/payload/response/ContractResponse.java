package gr.hua.distributed.it21774.payload.response;

import gr.hua.distributed.it21774.entity.ContractStatusEnum;

import java.util.List;

public class ContractResponse {

    private String text;

    private Long dateCreated;

    private Long dateApproved;

    private ContractStatusEnum status;

    private List<Member> members;

    public ContractResponse() {
    }

    public ContractResponse(String text,
                            Long dateCreated,
                            Long dateApproved,
                            ContractStatusEnum status,
                            List<Member> members) {
        this.text = text;
        this.dateCreated = dateCreated;
        this.dateApproved = dateApproved;
        this.status = status;
        this.members = members;
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

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
