package hu.kits.opfr.domain.reservation;

import hu.kits.opfr.domain.common.DailyTimeRange;

public class Requests {

    public static record ReservationRequest(String courtId, DailyTimeRange dailyTimeRange, String comment) {}
    
}
