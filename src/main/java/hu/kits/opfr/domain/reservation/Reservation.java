package hu.kits.opfr.domain.reservation;

import java.time.LocalDateTime;

import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.user.User;

public record Reservation(User user, String courtId, DailyTimeRange dailyTimeRange, LocalDateTime timeStamp, String comment) {

}
