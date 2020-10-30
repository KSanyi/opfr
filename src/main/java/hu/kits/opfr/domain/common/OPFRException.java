package hu.kits.opfr.domain.common;

public class OPFRException extends RuntimeException {

    public OPFRException(String message) {
        super(message);
    }
    
    public static class OPFRResourceNotFoundException extends RuntimeException {
        
    }
    
}
