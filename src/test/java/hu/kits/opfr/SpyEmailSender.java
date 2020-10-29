package hu.kits.opfr;

import hu.kits.opfr.domain.email.Email;
import hu.kits.opfr.domain.email.EmailSender;

public class SpyEmailSender implements EmailSender {

    private Email lastEmailSent;
    
    @Override
    public void sendEmail(Email email) {
        lastEmailSent = email;
    }

    public Email getLastEmailSent() {
        return lastEmailSent;
    }

}
