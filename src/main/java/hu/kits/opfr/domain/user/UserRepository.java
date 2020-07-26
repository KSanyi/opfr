package hu.kits.opfr.domain.user;

import java.util.List;
import java.util.Optional;

import hu.kits.opfr.common.Pair;

public interface UserRepository {

    List<UserData> loadAllUsers();
    
    void saveNewUser(UserData userData, String passwordHash);
    
    Optional<Pair<UserData, String>> findUserWithPasswordHash(String userId);

    void changePassword(String userId, String newPasswordHash);

    void updateUser(String userId, UserData user);

    void deleteUser(String userId);

    UserData loadUser(String userId);

}
