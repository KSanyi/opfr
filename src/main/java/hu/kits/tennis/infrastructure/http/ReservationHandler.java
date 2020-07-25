package hu.kits.tennis.infrastructure.http;

import org.json.JSONObject;

import hu.kits.tennis.common.DateRange;
import hu.kits.tennis.domain.common.DailyTimeRange;
import hu.kits.tennis.domain.reservation.ReservationService;
import hu.kits.tennis.domain.user.User;
import hu.kits.tennis.domain.user.UserService;
import hu.kits.tennis.infrastructure.http.jsonmapper.JsonMapper;
import io.javalin.http.Context;

class ReservationHandler {

    private final ReservationService reservationService;
    private final UserService userService;

    ReservationHandler(UserService userService, ReservationService reservationService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }
    
    void handleListMyReservationsRequest(Context context) {
        
        String userId = context.pathParam("userId");
        User user = userService.findUser(userId);
        context.json(reservationService.listMyReservations(user, DateRange.of(2020)));
    }
    
    void reserveCourt(Context context) {
        
        String userId = context.pathParam("userId");
        User user = userService.findUser(userId);
        
        JSONObject jsonObject = new JSONObject(context.body());
        
        String courtId = jsonObject.getString("courtId");
        DailyTimeRange dailyTimeRange = JsonMapper.parseDailyTimeRange(jsonObject.getJSONObject("dailyTimeRange"));
        String comment = jsonObject.getString("comment");
        
        reservationService.reserveCourt(user, courtId, dailyTimeRange, comment);
    }
    
}
