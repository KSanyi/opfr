package hu.kits.opfr.infrastructure.http;

import java.util.List;

import hu.kits.opfr.domain.user.AuthenticationException;
import hu.kits.opfr.domain.user.Requests.PasswordChangeRequest;
import hu.kits.opfr.domain.user.Requests.UserCreationRequest;
import hu.kits.opfr.domain.user.Requests.UserDataUpdateRequest;
import hu.kits.opfr.domain.user.Requests.UserRegistrationRequest;
import hu.kits.opfr.domain.user.UserData;
import hu.kits.opfr.domain.user.UserService;
import io.javalin.http.Context;

class UserHandler {

    //private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final UserService userService;

    UserHandler(UserService userService) {
        this.userService = userService;
    }
    
    void listAllUsers(Context context) {
        List<UserData> users = userService.loadAllUsers();
        context.json(users);
    }
    
    void listNewlyRegisteredUsers(Context context) {
        List<UserData> users = userService.loadNewlyRegisteredUsers();
        context.json(users);
    }
    
    void authenticateUser(Context context) {
        String userId = context.pathParam("userId");
        
        String password = RequestParser.parsePassword(context.body());
        
        try {
            UserData user = userService.authenticateUser(userId, password);
            context.json(user);    
        } catch(AuthenticationException ex) {
            context.status(401).result("Unauthorized");
        }
    }
    
    void saveNewUser(Context context) {
        UserCreationRequest userCreationRequest = RequestParser.parseUserCreationRequest(context.body());
        
        userService.saveNewUser(userCreationRequest);
    }
    
    void register(Context context) {
        UserRegistrationRequest userRegistrationRequest = RequestParser.parseUserRegistrationRequest(context.body());
        
        userService.register(userRegistrationRequest);
    }
    
    void activate(Context context) {
        String userId = context.pathParam("userId");
        
        userService.activateUser(userId);
    }
    
    void updateUser(Context context) {
        String userId = context.pathParam("userId");
        
        UserDataUpdateRequest userDataUpdateRequest = RequestParser.parseUserDataUpdateRequest(context.body());
        
        if(!userId.equals(userDataUpdateRequest.userData().userId())) {
            throw new HttpServer.BadRequestException("UserId can not be changed");
        }
        
        userService.updateUser(userId, userDataUpdateRequest);
    }
    
    void deleteUser(Context context) {
        String userId = context.pathParam("userId");
        userService.deleteUser(userId);
    }
    
    void generateNewPassword(Context context) {
        String userId = context.pathParam("userId");
        userService.generateNewPassword(userId);
    }
    
    void changePassword(Context context) {
        String userId = context.pathParam("userId");
        PasswordChangeRequest passwordChangeRequest = RequestParser.parsePasswordChangeRequest(context.body());
        
        try {
            userService.changePassword(userId, passwordChangeRequest);
        } catch(AuthenticationException ex) {
            context.status(401).result("Unauthorized");
        }
    }
    
}
