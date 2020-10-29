package hu.kits.opfr.domain.email;

import java.util.List;

public record Email(String recipient, String subject, List<String> ccs, String content) {

    @Override
    public String toString() {
        return String.format("To: %s subject: %s", recipient, subject);
    }
    
}
