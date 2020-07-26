package hu.kits.opfr.infrastructure.http;

import java.time.LocalDate;

import org.json.JSONException;
import org.json.JSONObject;

import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.TimeRange;
import hu.kits.opfr.domain.reservation.Requests.ReservationRequest;
import hu.kits.opfr.domain.user.Role;
import hu.kits.opfr.domain.user.UserData;
import hu.kits.opfr.domain.user.Requests.PasswordChangeRequest;
import hu.kits.opfr.domain.user.Requests.UserCreationRequest;
import hu.kits.opfr.domain.user.Requests.UserDataUpdateRequest;

public class RequestParser {

    public static PasswordChangeRequest parsePasswordChangeRequest(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return new PasswordChangeRequest(
                    jsonObject.getString("password"),
                    jsonObject.getString("newPassword"));
        } catch(JSONException | IllegalArgumentException ex) {
            throw new HttpServer.BadRequestException(ex.getMessage());
        }
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
    
    public static ReservationRequest parseReservationRequest(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return new ReservationRequest(
                    jsonObject.getString("courtId"),
                    parseDailyTimeRange(jsonObject.getJSONObject("dailyTimeRange")),
                    jsonObject.getString("comment"));
        } catch(JSONException | IllegalArgumentException ex) {
            throw new HttpServer.BadRequestException(ex.getMessage());
        }
    }
    
    public static UserCreationRequest parseUserCreationRequest(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return new UserCreationRequest(
                    parseUserData(jsonObject),
                    jsonObject.getString("password"));
        } catch(JSONException | IllegalArgumentException ex) {
            throw new HttpServer.BadRequestException(ex.getMessage());
        }
    }
    
    public static UserDataUpdateRequest parseUserDataUpdateRequest(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return new UserDataUpdateRequest(
                    parseUserData(jsonObject));
        } catch(JSONException | IllegalArgumentException ex) {
            throw new HttpServer.BadRequestException(ex.getMessage());
        }
    }
    
    private static UserData parseUserData(JSONObject jsonObject) {
        return new UserData(
                jsonObject.getString("userId"),
                jsonObject.getString("name"),
                Role.valueOf(jsonObject.getString("role")),
                jsonObject.getString("phone"),
                jsonObject.getString("email"),
                jsonObject.getBoolean("isActive"));
    }
    
}
