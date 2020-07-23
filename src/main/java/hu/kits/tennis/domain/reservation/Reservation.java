package hu.kits.tennis.domain.reservation;

import java.time.LocalDateTime;

import hu.kits.tennis.domain.common.DailyTimeRange;
import hu.kits.tennis.domain.user.User;

public record Reservation(User user, String courtId, DailyTimeRange dailyTimeRange, LocalDateTime timeStamp, String comment) {

}
