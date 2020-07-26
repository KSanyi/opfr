package hu.kits.opfr.domain.user;

public record UserData(String userId, String name, Role role, String phone, String email, boolean isActive) {

}
