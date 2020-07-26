package hu.kits.opfr.infrastructure.http.jsonmapper;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.TimeRange;
import hu.kits.opfr.domain.court.TennisCourt;
import hu.kits.opfr.domain.reservation.Reservation;
import hu.kits.opfr.domain.user.Role;
import hu.kits.opfr.domain.user.User;
import hu.kits.opfr.infrastructure.http.HttpServer;

public class JsonMapper {

    public static String mapToJsonString(Object object) {
        return mapToJson(object).toString();
    }
    
    private static Object mapToJson(Object object) {
        
        if(object instanceof Collection) {
            Collection<?> collection = (Collection<?>)object; 
            return new JSONArray(collection.stream().map(JsonMapper::mapToJson).collect(toList()));
        } else if(object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>)object;
            Map<?, ?> jsonEntriesMap = map.entrySet().stream().collect(toMap(
                    e -> JsonMapper.mapToJson(e.getKey()),
                    e -> JsonMapper.mapToJson(e.getValue())));
            return new JSONObject(jsonEntriesMap);
        } else if(object instanceof TennisCourt) {
            return mapTennisCourtToJson((TennisCourt)object);    
        } else if(object instanceof User) {
            return mapUserToJson((User)object);    
        } else if(object instanceof TimeRange) {
            return mapTimeRangeToJson((TimeRange)object);    
        } else if(object instanceof DailyTimeRange) {
            return mapDailyTimeRangeToJson((DailyTimeRange)object);    
        } else if(object instanceof Reservation) {
            return mapReservationToJson((Reservation)object);    
        } else {
            return object;
        }
    }
    
    private static JSONObject mapTennisCourtToJson(TennisCourt tennisCourt) {
        
        return new JSONObject()
                .put("id", tennisCourt.id())
                .put("name", tennisCourt.name());
    }
    
    private static JSONObject mapUserToJson(User user) {
        
        return new JSONObject()
                .put("userId", user.userId())
                .put("name", user.name())
                .put("email", user.email())
                .put("phone", user.phone())
                .put("role", user.role().name());
    }
    
    public static User parseUser(JSONObject jsonObject) {
        try {
            return new User(
                    jsonObject.getString("userId"),
                    jsonObject.getString("name"),
                    Role.valueOf(jsonObject.getString("role")),
                    jsonObject.getString("phone"),
                    jsonObject.getString("email"),
                    jsonObject.getBoolean("isActive"));
        } catch(JSONException | IllegalArgumentException ex) {
            throw new HttpServer.BadRequestException(ex.getMessage());
        }
    }
    
    private static JSONObject mapTimeRangeToJson(TimeRange timeRange) {
        
        return new JSONObject()
                .put("startAt", timeRange.startAt())
                .put("hours", timeRange.hours());
    }
    
    private static JSONObject mapDailyTimeRangeToJson(DailyTimeRange dailyTimeRange) {
        
        return new JSONObject()
                .put("date", dailyTimeRange.date())
                .put("timeRange", mapTimeRangeToJson(dailyTimeRange.timeRange()));
    }
    
    public static DailyTimeRange parseDailyTimeRange(JSONObject jsonObject) {
        try {
            return new DailyTimeRange(
                    LocalDate.parse(jsonObject.getString("date")),
                    parseTimeRange(jsonObject.getJSONObject("timeRange")));
        } catch(JSONException | IllegalArgumentException ex) {
            throw new HttpServer.BadRequestException(ex.getMessage());
        }
    }
    
    public static TimeRange parseTimeRange(JSONObject jsonObject) {
        try {
            return new TimeRange(
                    jsonObject.getInt("startAt"),
                    jsonObject.getInt("hours"));
        } catch(JSONException | IllegalArgumentException ex) {
            throw new HttpServer.BadRequestException(ex.getMessage());
        }
    }
    
    private static JSONObject mapReservationToJson(Reservation reservation) {
        
        return new JSONObject()
                .put("courtId", reservation.courtId())
                .put("user", mapUserToJson(reservation.user()))
                .put("comment", reservation.comment())
                .put("timestamp", reservation.timeStamp())
                .put("time", mapDailyTimeRangeToJson(reservation.dailyTimeRange()));
    }
    
}
