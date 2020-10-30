package hu.kits.opfr;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hu.kits.opfr.common.Pair;
import hu.kits.opfr.domain.user.Role;
import hu.kits.opfr.domain.user.UserData;
import hu.kits.opfr.domain.user.UserRepository;
import hu.kits.opfr.domain.user.Users;

public class FakeUserRepository implements UserRepository {

    private final List<UserData> users = new ArrayList<>(List.of(
            new UserData("testUser1", "Test user 1", Role.MEMBER, "+362012345678", "test1@opfr.hu", true),
            new UserData("testUser2", "Test user 2", Role.MEMBER, "+367012345678", "test2@opfr.hu", true)));
    
    @Override
    public Users loadAllUsers() {
        return new Users(users);
    }

    @Override
    public void saveNewUser(UserData userData, String passwordHash) {
        users.add(userData);
    }

    @Override
    public Optional<Pair<UserData, String>> findUserWithPasswordHash(String userId) {
        return Optional.empty();
    }

    @Override
    public void changePassword(String userId, String newPasswordHash) {
    }

    @Override
    public void updateUser(String userId, UserData userData) {
        deleteUser(userId);
        saveNewUser(userData, "");
    }

    @Override
    public void deleteUser(String userId) {
        users.removeIf(user -> user.userId().equals(userId));
    }

    @Override
    public UserData loadUser(String userId) {
        return users.stream().filter(user -> user.userId().equals(userId)).findAny().get();
    }

}
