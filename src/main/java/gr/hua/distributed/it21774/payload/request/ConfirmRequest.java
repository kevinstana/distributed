package gr.hua.distributed.it21774.payload.request;

public class ConfirmRequest {

    private String status;

    public ConfirmRequest() {
    }

    public ConfirmRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
