package gr.hua.distributed.it21774.payload.request;

public class AnswerRequest {

    private String answer;

    public AnswerRequest() {
    }

    public AnswerRequest(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
