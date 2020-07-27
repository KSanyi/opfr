package hu.kits.opfr.domain.user;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Users {

    private final Map<String, UserData> usersMap;

    public Users(List<UserData> users) {
        usersMap = users.stream().collect(toMap(
                UserData::userId, 
                Function.identity()));
    }
    
    public UserData getUser(String userId) {
        return usersMap.getOrDefault(userId, UserData.unknown(userId));
    }
    
    public List<UserData> list() {
        return usersMap.values().stream().sorted(comparing(UserData::name)).collect(toList());
    }
    
}
