package hu.kits.tennis.domain.user;

import java.util.List;
import java.util.Optional;

import hu.kits.tennis.common.Pair;

public interface UserRepository {

    List<User> loadAllUsers();
    
    void saveNewUser(User user, String password);
    
    Optional<Pair<User, String>> findUserWithPasswordHash(String userId);

    void changePassword(String userId, String newPasswordHash);

    void updateUser(String userId, User user);

    void deleteUser(String userId);

    User loadUser(String userId);

}
