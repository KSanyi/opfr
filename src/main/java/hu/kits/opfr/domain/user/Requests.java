package hu.kits.opfr.domain.user;

public class Requests {

    public static record PasswordChangeRequest(String oldPassword, String newPassword) {}
    
}
