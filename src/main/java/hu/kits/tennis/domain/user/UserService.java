package hu.kits.tennis.domain.user;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.tennis.common.Pair;
import hu.kits.tennis.domain.user.password.PasswordGenerator;
import hu.kits.tennis.domain.user.password.PasswordHasher;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    
    public UserService(UserRepository userRepository, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
    }
    
    public List<User> loadAllUsers() {
        
        List<User> users = userRepository.loadAllUsers();
        
        logger.debug("{} users loaded", users.size());
        
        return users;
    }
    
    public User findUser(String userId) {
        return userRepository.loadUser(userId);
    }
    
    public void saveNewUser(User user, String password) {
        
        logger.info("Saving new user: {}", user);
        
        userRepository.saveNewUser(user, password);
    }
    
    public void updateUser(String userId, User user) {
        
        logger.info("Updating user: {} with new data: {}", userId, user);
        
        userRepository.updateUser(userId, user);
    }
    
    public void deleteUser(String userId) {
        
        logger.info("Deleting user: {}", userId);
        
        userRepository.deleteUser(userId);
    }

    public User authenticateUser(String userId, String password) throws AuthenticationException {
        
        logger.debug("Authentication request for user '{}'", userId);
        
        Optional<Pair<User, String>> userWithPasswordHash = userRepository.findUserWithPasswordHash(userId);
        if(userWithPasswordHash.isPresent()) {
            
            User user = userWithPasswordHash.get().getFirst();
            String passwordHash = userWithPasswordHash.get().getSecond();
            
            if(passwordHasher.checkPassword(passwordHash, password)) {
                if(user.isActive()) {
                    logger.info("Authentication success for user '{}'", userId);
                    return user;
                } else {
                    logger.info("Authentication failure. User '{}' is inactive", userId);
                    throw new AuthenticationException("User is inactive");
                }
            } else {
                logger.info("Authentication failure. Wrong password for user '{}'", userId);
                throw new AuthenticationException();
            }
        } else {
            logger.info("Authentication failure. User with user id '{}' not found", userId);
            throw new AuthenticationException();
        }
    }
    
    public void changePassword(String userId, String oldPassword, String newPassword) throws AuthenticationException {
        
        logger.info("Password change request for user '{}'", userId);
        
        authenticateUser(userId, oldPassword);
        
        userRepository.changePassword(userId, passwordHasher.createNewPasswordHash(newPassword));
        
        logger.info("Successful password change for '{}'", userId);
    }
    
    public void generateNewPassword(String userId) {
        
        logger.info("New password generation request for user '{}'", userId);
        
        String newPassword = PasswordGenerator.generateRandomPassword();
        
        userRepository.changePassword(userId, passwordHasher.createNewPasswordHash(newPassword));
        
        logger.info("Successful password generation for '{}'", userId);
        
        logger.debug("New password: {}", newPassword);
    }

}
