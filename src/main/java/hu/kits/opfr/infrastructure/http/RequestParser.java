package hu.kits.opfr.infrastructure.http;

import java.time.LocalDate;

import org.json.JSONException;
import org.json.JSONObject;

import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.TimeRange;
import hu.kits.opfr.domain.reservation.Requests.ReservationRequest;
import hu.kits.opfr.domain.user.Requests.PasswordChangeRequest;

public class RequestParser {

    public static PasswordChangeRequest parsePasswordChangeRequest(JSONObject jsonObject) {
        try {
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
    
    public static ReservationRequest parseReservationRequest(JSONObject jsonObject) {
        try {
            return new ReservationRequest(
                    jsonObject.getString("courtId"),
                    parseDailyTimeRange(jsonObject.getJSONObject("dailyTimeRange")),
                    jsonObject.getString("comment"));
        } catch(JSONException | IllegalArgumentException ex) {
            throw new HttpServer.BadRequestException(ex.getMessage());
        }
    }
    
}
