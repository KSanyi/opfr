package hu.kits.opfr.infrastructure.http;

import java.util.List;

import org.json.JSONObject;

import hu.kits.opfr.domain.user.AuthenticationException;
import hu.kits.opfr.domain.user.User;
import hu.kits.opfr.domain.user.UserService;
import hu.kits.opfr.infrastructure.http.jsonmapper.JsonMapper;
import io.javalin.http.Context;

class UserHandler {

    //private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final UserService userService;

    UserHandler(UserService userService) {
        this.userService = userService;
    }
    
    void listAllUsers(Context context) {
        
        List<User> users = userService.loadAllUsers();
        context.json(users);
    }
    
    void authenticateUser(Context context) {
        
        String userId = context.pathParam("userId");
        JSONObject jsonObject = new JSONObject(context.body());
        String password = jsonObject.getString("password");
        
        try {
            User user = userService.authenticateUser(userId, password);
            context.json(user);    
        } catch(AuthenticationException ex) {
            context.status(401).result("Unauthorized");
        }
    }
    
    void saveNewUser(Context context) {
        
        JSONObject jsonObject = new JSONObject(context.body());
        
        User user = JsonMapper.parseUser(jsonObject);
        String password = jsonObject.getString("password");
        
        userService.saveNewUser(user, password);
    }
    
    void updateUser(Context context) {
        
        String userId = context.pathParam("userId");
        JSONObject jsonObject = new JSONObject(context.body());
        
        User user = JsonMapper.parseUser(jsonObject);
        
        if(!userId.equals(user.userId())) {
            throw new HttpServer.BadRequestException("UserId can not be changed");
        }
        
        userService.updateUser(userId, user);
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
        
        JSONObject jsonObject = new JSONObject(context.body());
        String password = jsonObject.getString("password");
        String newPassword = jsonObject.getString("newPassword");
        
        try {
            userService.changePassword(userId, password, newPassword);
        } catch(AuthenticationException ex) {
            context.status(401).result("Unauthorized");
        }
    }
    
}
