package hu.kits.tennis.infrastructure.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.tennis.common.Pair;
import hu.kits.tennis.domain.common.OPFRException;
import hu.kits.tennis.domain.user.Role;
import hu.kits.tennis.domain.user.User;
import hu.kits.tennis.domain.user.UserRepository;

public class UserJdbcRepository implements UserRepository {

    private static final String TABLE_USER = "USER";
    private static final String COLUMN_NAME = "NAME";
    private static final String COLUMN_USERID = "USERID";
    private static final String COLUMN_PASSWORD_HASH = "PASSWORD_HASH";
    private static final String COLUMN_ROLE = "ROLE";
    private static final String COLUMN_PHONE = "PHONE";
    private static final String COLUMN_EMAIL = "EMAIL";
    private static final String COLUMN_ACTIVE = "ISACTIVE";
    
    private final Jdbi jdbi;
    
    public UserJdbcRepository(DataSource dataSource) {
        jdbi = Jdbi.create(dataSource);
    }
    
    @Override
    public List<User> loadAllUsers() {
        String sql = String.format("SELECT * FROM %s", TABLE_USER);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql)
            .map((rs, ctx) -> mapToUser(rs)).list());
    }
    
    @Override
    public Optional<Pair<User, String>> findUserWithPasswordHash(String userId) {
        
        String sql = String.format("SELECT * FROM %s WHERE %s = :userId", TABLE_USER, COLUMN_USERID);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql)
            .bind("userId", userId)
            .map((rs, ctx) -> new Pair<>(mapToUser(rs), rs.getString(COLUMN_PASSWORD_HASH))).findOne());
    }
    
    @Override
    public User loadUser(String userId) {
        return findUserWithPasswordHash(userId).map(Pair::getFirst).orElseThrow(() -> new OPFRException("Can not find user with id: '" + userId + "'"));
    }
    
    private static User mapToUser(ResultSet rs) throws SQLException {
        
        return new User(
                rs.getString(COLUMN_USERID), 
                rs.getString(COLUMN_NAME),
                Role.valueOf(rs.getString(COLUMN_ROLE)), 
                rs.getString(COLUMN_PHONE),
                rs.getString(COLUMN_EMAIL),
                rs.getBoolean(COLUMN_ACTIVE));
    }

    @Override
    public void changePassword(String userId, String newPasswordHash) {
        
        JdbiUtil.executeUpdate(jdbi, TABLE_USER, Map.of(COLUMN_PASSWORD_HASH, "*****"), Map.of(COLUMN_PASSWORD_HASH, newPasswordHash), COLUMN_USERID, userId);
    }

    @Override
    public void saveNewUser(User user, String passwordHash) {
        
        Map<String, Object> map = createMap(user);
        map.put(COLUMN_PASSWORD_HASH, passwordHash);
        try {
            jdbi.withHandle(handle -> JdbiUtil.createInsertStatement(handle, TABLE_USER, map).execute());    
        } catch(Exception ex) {
            if(ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new OPFRException("UserId '" + user.userId() + "' already exists");
            } else {
                throw new RuntimeException(ex);
            }
        }
    }
    
    private static Map<String, Object> createMap(User user) {
        
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put(COLUMN_USERID, user.userId());
        valuesMap.put(COLUMN_NAME, user.name());
        valuesMap.put(COLUMN_ROLE, user.role().name());
        valuesMap.put(COLUMN_PHONE, user.phone());
        valuesMap.put(COLUMN_EMAIL, user.email());
        valuesMap.put(COLUMN_ACTIVE, user.isActive());
        
        return valuesMap;
    }

    @Override
    public void updateUser(String userId, User updatedUserData) {
        
        Optional<Pair<User, String>> userWithPasswordHash = findUserWithPasswordHash(userId);
        if(userWithPasswordHash.isPresent()) {
            User originalUser = userWithPasswordHash.get().getFirst();
            
            Map<String, Object> originalMap = createMap(originalUser);
            Map<String, Object> updatedMap = createMap(updatedUserData);
            
            JdbiUtil.executeUpdate(jdbi, TABLE_USER, originalMap, updatedMap, COLUMN_USERID, userId);
        } else {
            throw new OPFRException("User '" + userId + "' not found");
        }
    }

    @Override
    public void deleteUser(String userId) {
        
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ?", TABLE_USER, COLUMN_USERID), userId));
    }

}
