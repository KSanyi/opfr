package hu.kits.opfr.domain.user.password;

public class DummyPasswordHasher implements PasswordHasher {

    @Override
    public String createNewPasswordHash(String password) {
        return password;
    }

    @Override
    public boolean checkPassword(String passwordHash, String candidatePassword) {
        return passwordHash.equals(candidatePassword);
    }

}
