package hu.kits.opfr.domain.user;

@SuppressWarnings("serial")
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException() {
        super("");
    }
    
}
