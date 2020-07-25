package hu.kits.tennis.domain.user;

@SuppressWarnings("serial")
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException() {
        super("");
    }
    
}
