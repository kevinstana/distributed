package gr.hua.distributed.it21774.payload.response;

public class Member {
    private String fullName;
    private String answer;

    public Member() {}

    public Member(String fullName, String answer) {
        this.fullName = fullName;
        this.answer = answer;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getAnswer() {
        return this.answer;
    }
}
