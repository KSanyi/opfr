package hu.kits.opfr.domain.email;

import java.util.List;

public record Email(String recipient, String subject, List<String> ccs, String content, CalendarAttachment calendarAttachment) {

    public Email(String recipient, String subject, List<String> ccs, String content) {
        this(recipient, subject, ccs, content, null);
    }
    
    @Override
    public String toString() {
        return String.format("To: %s subject: %s", recipient, subject);
    }
    
}
