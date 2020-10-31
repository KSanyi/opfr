package hu.kits.opfr.domain.common;

public class OPFRException extends RuntimeException {

    public OPFRException(String message) {
        super(message);
    }
    
    public static class OPFRResourceNotFoundException extends OPFRException {
        
        public OPFRResourceNotFoundException() {
            super("");
        }
        
        public OPFRResourceNotFoundException(String message) {
            super(message);
        }
    }
    
    public static class OPFRConflictException extends OPFRException {
        
        public OPFRConflictException(String message) {
            super(message);
        }
    }
    
    public static class OPFRAuthorizationException extends OPFRException {
        
        public OPFRAuthorizationException(String message) {
            super(message);
        }
    }
    
}
