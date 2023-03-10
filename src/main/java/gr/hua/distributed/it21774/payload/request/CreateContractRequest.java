package gr.hua.distributed.it21774.payload.request;

import java.util.HashSet;
import java.util.Set;

public class CreateContractRequest {

    // Contains the afms of the involved parties
    private Set<String> afm = new HashSet<>();

    private String text;

    public Set<String> getAfm() {
        return afm;
    }

    public void setAfm(Set<String> afm) {
        this.afm = afm;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
