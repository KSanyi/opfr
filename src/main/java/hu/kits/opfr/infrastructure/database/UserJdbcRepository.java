package hu.kits.opfr.infrastructure.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.jdbi.v3.core.Jdbi;

import hu.kits.opfr.common.Pair;
import hu.kits.opfr.domain.common.OPFRException.OPFRConflictException;
import hu.kits.opfr.domain.common.OPFRException.OPFRResourceNotFoundException;
import hu.kits.opfr.domain.user.Role;
import hu.kits.opfr.domain.user.UserData;
import hu.kits.opfr.domain.user.UserRepository;
import hu.kits.opfr.domain.user.Users;

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
    public Users loadAllUsers() {
        String sql = String.format("SELECT * FROM %s", TABLE_USER);
        
        List<UserData> usersList = jdbi.withHandle(handle -> 
            handle.createQuery(sql)
            .map((rs, ctx) -> mapToUser(rs)).list());
        
        return new Users(usersList);
    }
    
    @Override
    public Optional<Pair<UserData, String>> findUserWithPasswordHash(String userId) {
        
        String sql = String.format("SELECT * FROM %s WHERE %s = :userId", TABLE_USER, COLUMN_USERID);
        
        return jdbi.withHandle(handle -> 
            handle.createQuery(sql)
            .bind("userId", userId)
            .map((rs, ctx) -> new Pair<>(mapToUser(rs), rs.getString(COLUMN_PASSWORD_HASH))).findOne());
    }
    
    @Override
    public UserData loadUser(String userId) {
        return findUserWithPasswordHash(userId).map(Pair::getFirst).orElseThrow(() -> new OPFRResourceNotFoundException("Can not find user with id: '" + userId + "'"));
    }
    
    private static UserData mapToUser(ResultSet rs) throws SQLException {
        
        return new UserData(
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
    public void saveNewUser(UserData userData, String passwordHash) {
        
        Map<String, Object> map = createMap(userData);
        map.put(COLUMN_PASSWORD_HASH, passwordHash);
        try {
            jdbi.withHandle(handle -> JdbiUtil.createInsertStatement(handle, TABLE_USER, map).execute());    
        } catch(Exception ex) {
            if(ex.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new OPFRConflictException("UserId '" + userData.userId() + "' already exists");
            } else {
                throw new RuntimeException(ex);
            }
        }
    }
    
    private static Map<String, Object> createMap(UserData user) {
        
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
    public void updateUser(String userId, UserData updatedUserData) {
        
        Optional<Pair<UserData, String>> userWithPasswordHash = findUserWithPasswordHash(userId);
        if(userWithPasswordHash.isPresent()) {
            UserData originalUser = userWithPasswordHash.get().getFirst();
            
            Map<String, Object> originalMap = createMap(originalUser);
            Map<String, Object> updatedMap = createMap(updatedUserData);
            
            JdbiUtil.executeUpdate(jdbi, TABLE_USER, originalMap, updatedMap, COLUMN_USERID, userId);
        } else {
            throw new OPFRResourceNotFoundException("User '" + userId + "' not found");
        }
    }

    @Override
    public void deleteUser(String userId) {
        
        jdbi.withHandle(handle -> handle.execute(String.format("DELETE FROM %s WHERE %s = ?", TABLE_USER, COLUMN_USERID), userId));
    }

}
