package hu.kits.tennis.infrastructure.http.jsonmapper;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import hu.kits.tennis.domain.common.DailyTimeRange;
import hu.kits.tennis.domain.common.TimeRange;
import hu.kits.tennis.domain.court.TennisCourt;
import hu.kits.tennis.domain.reservation.Reservation;
import hu.kits.tennis.domain.user.User;

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
                .put("id", user.id())
                .put("name", user.name())
                .put("role", user.role());
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
    
    private static JSONObject mapReservationToJson(Reservation reservation) {
        
        return new JSONObject()
                .put("courtId", reservation.courtId())
                .put("user", mapUserToJson(reservation.user()))
                .put("comment", reservation.comment())
                .put("timestamp", reservation.timeStamp())
                .put("time", mapDailyTimeRangeToJson(reservation.dailyTimeRange()));
    }
    
}
