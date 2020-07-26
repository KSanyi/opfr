package hu.kits.opfr.domain.user.password;

public interface PasswordHasher {

    String createNewPasswordHash(String password);
    
    boolean checkPassword(String passwordHash, String candidatePassword);
    
}
