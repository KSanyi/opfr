package hu.kits.opfr.domain.user;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.opfr.common.Pair;
import hu.kits.opfr.domain.common.OPFRException;
import hu.kits.opfr.domain.email.EmailCreator;
import hu.kits.opfr.domain.email.EmailSender;
import hu.kits.opfr.domain.user.Requests.PasswordChangeRequest;
import hu.kits.opfr.domain.user.Requests.UserCreationRequest;
import hu.kits.opfr.domain.user.Requests.UserDataUpdateRequest;
import hu.kits.opfr.domain.user.Requests.UserRegistrationRequest;
import hu.kits.opfr.domain.user.UserData.Status;
import hu.kits.opfr.domain.user.password.PasswordGenerator;
import hu.kits.opfr.domain.user.password.PasswordHasher;

public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final PasswordHasher passwordHasher;
    
    public UserService(UserRepository userRepository, EmailSender emailSender, PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.passwordHasher = passwordHasher;
    }
    
    public List<UserData> loadAllUsers() {
        
        List<UserData> users = userRepository.loadAllUsers().list();
        
        logger.debug("{} users loaded", users.size());
        
        return users;
    }
    
    public List<UserData> loadNewlyRegisteredUsers() {

        Users users = userRepository.loadAllUsers();
        
        return users.loadNewlyRegistered();
    }
    
    public UserData findUser(String userId) {
        return userRepository.loadUser(userId);
    }
    
    public void register(UserRegistrationRequest userRegistrationRequest) {
        
        UserData userData = new UserData(userRegistrationRequest.userId(), 
                userRegistrationRequest.name(), 
                Role.MEMBER, 
                userRegistrationRequest.phone(), 
                userRegistrationRequest.email(), 
                Status.REGISTERED);
        
        logger.info("Registering new user: {}", userData);
        
        Users users = userRepository.loadAllUsers();
        
        if(users.hasUserWithEmail(userData.email())) {
            logger.info("User existis with this email address: {}", userData.email());
            throw new OPFRException.OPFRConflictException("User exists with this email address");
        }
        
        userRepository.saveNewUser(userData, userRegistrationRequest.password());
        
        emailSender.sendEmail(EmailCreator.createRegistrationNotificationEmail(users.adminEmails(), userData));
    }
    
    public void activateUser(String userId) {
        userRepository.activateUser(userId);
        
        logger.info("Activating user {}", userId);
        
        UserData userData = userRepository.loadUser(userId);
        
        emailSender.sendEmail(EmailCreator.createActivationNotificationEmail(userData));
        
        logger.info("User {} is activated", userId);
    }
    
    public void saveNewUser(UserCreationRequest userCreationRequest) {
        
        logger.info("Saving new user: {}", userCreationRequest.userData());
        
        String passwordHash = passwordHasher.createNewPasswordHash(userCreationRequest.password());
        
        userRepository.saveNewUser(userCreationRequest.userData(), passwordHash);
    }
    
    public void updateUser(String userId, UserDataUpdateRequest userDataUpdateRequest) {
        
        logger.info("Updating user: {} with new data: {}", userId, userDataUpdateRequest.userData());
        
        userRepository.updateUser(userId, userDataUpdateRequest.userData());
    }
    
    public void deleteUser(String userId) {
        
        logger.info("Deleting user: {}", userId);
        
        userRepository.deleteUser(userId);
    }

    public UserData authenticateUser(String userId, String password) throws AuthenticationException {
        
        logger.debug("Authentication request for user '{}'", userId);
        
        Optional<Pair<UserData, String>> userWithPasswordHash = userRepository.findUserWithPasswordHash(userId);
        if(userWithPasswordHash.isPresent()) {
            
            UserData user = userWithPasswordHash.get().getFirst();
            String passwordHash = userWithPasswordHash.get().getSecond();
            
            if(passwordHasher.checkPassword(passwordHash, password)) {
                if(user.status() == Status.ACTIVE) {
                    logger.info("Authentication success for user '{}'", userId);
                    return user;
                } else {
                    logger.info("Authentication failure. User '{}' status is: {}", userId, user.status());
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
    
    public void changePassword(String userId, PasswordChangeRequest passwordChangeRequest) throws AuthenticationException {
        
        String oldPassword = passwordChangeRequest.oldPassword();
        String newPassword = passwordChangeRequest.newPassword(); 
        
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
