package hu.kits.opfr.domain.user;

public class Requests {

    public static record UserCreationRequest(UserData userData, String password) {}
    
    public static record UserDataUpdateRequest(UserData userData) {}
    
    public static record PasswordChangeRequest(String oldPassword, String newPassword) {}
    
}
