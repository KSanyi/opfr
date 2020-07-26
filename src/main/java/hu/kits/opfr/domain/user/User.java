package hu.kits.opfr.domain.user;

public record User(String userId, String name, Role role, String phone, String email, boolean isActive) {

}
