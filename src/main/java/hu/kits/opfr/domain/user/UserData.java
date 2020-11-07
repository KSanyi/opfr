package hu.kits.opfr.domain.user;

public record UserData(String userId, String name, Role role, String phone, String email, Status status) {

    public static UserData unknown(String userId) {
        return new UserData(userId, userId, Role.MEMBER, "???", "???", Status.INACTIVE);
    }
    
    public static enum Status {
        REGISTERED, ACTIVE, INACTIVE
    }
    
}
