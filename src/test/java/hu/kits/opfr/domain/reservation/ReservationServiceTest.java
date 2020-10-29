package hu.kits.opfr.domain.reservation;

import static hu.kits.opfr.TestUtil.TEST_MEMBER_1;
import static hu.kits.opfr.TestUtil.TEST_MEMBER_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.kits.opfr.SpyEmailSender;
import hu.kits.opfr.common.Clock;
import hu.kits.opfr.common.DateRange;
import hu.kits.opfr.common.DateTimeRange;
import hu.kits.opfr.domain.common.DailyTimeRange;
import hu.kits.opfr.domain.common.OPFRException;
import hu.kits.opfr.domain.court.TennisCourt;
import hu.kits.opfr.domain.reservation.Requests.ReservationRequest;
import hu.kits.opfr.infrastructure.database.ReservationFakeRepository;
import hu.kits.opfr.infrastructure.database.ReservationSettingsFakeRepository;
import hu.kits.opfr.infrastructure.database.TennisCourtFakeRepository;

public class ReservationServiceTest {

    private ReservationService reservationService;
    
    private final SpyEmailSender spyEmailSender = new SpyEmailSender();
    
    @BeforeEach
    void init() {
        reservationService = new ReservationService(new ReservationSettingsFakeRepository(), new ReservationFakeRepository(), new TennisCourtFakeRepository(), spyEmailSender);
        Clock.setStaticTime(LocalDateTime.of(2020,7,30, 10,0));
    }
    
    @Test
    void getAllowedReservationRange() {
        DateTimeRange allowedReservationRange = reservationService.getAllowedReservationRange();
        assertEquals(DateTimeRange.of(LocalDateTime.of(2020,7,30, 11,0), LocalDateTime.of(2020,8,5, 23,0)), allowedReservationRange);
    }
    
    @Test
    void drawTodaysReservationOpeningTime() {
        Optional<LocalTime> drawTodaysReservationOpeningTime = reservationService.drawTodaysReservationOpeningTime();
        
        assertTrue(drawTodaysReservationOpeningTime.isPresent());
        assertEquals(20, drawTodaysReservationOpeningTime.get().getHour());
        
        drawTodaysReservationOpeningTime = reservationService.drawTodaysReservationOpeningTime();
        assertTrue(drawTodaysReservationOpeningTime.isEmpty());
    }
    
    @Test
    void reserveSuccesfully() {
        
        reservationService.reserveCourt(TEST_MEMBER_1, new ReservationRequest("1", new DailyTimeRange("2020-08-01", 10, 2), ""));
        
        reservationService.reserveCourt(TEST_MEMBER_1, new ReservationRequest("1", new DailyTimeRange("2020-08-01", 19, 1), ""));
        
        reservationService.reserveCourt(TEST_MEMBER_1, new ReservationRequest("1", new DailyTimeRange("2020-08-02", 10, 2), ""));
        
        reservationService.reserveCourt(TEST_MEMBER_1, new ReservationRequest("2", new DailyTimeRange("2020-08-01", 10, 2), ""));
        
        List<Reservation> member1Reservations = reservationService.listMyReservations(TEST_MEMBER_1, DateRange.of(2020));
        
        assertEquals(4, member1Reservations.size());
        assertEquals("1", member1Reservations.get(0).courtId());
        assertEquals(new DailyTimeRange("2020-08-01", 10, 2), member1Reservations.get(0).dailyTimeRange());
        
        assertTrue( reservationService.listMyReservations(TEST_MEMBER_2, DateRange.of(2020)).isEmpty());
    }
    
    @Test
    void reserveFailedAsCourseIsNotAvailable() {
        reservationService.reserveCourt(TEST_MEMBER_1, new ReservationRequest("1", new DailyTimeRange("2020-08-01", 10, 2), ""));
        
        OPFRException exception = Assertions.assertThrows(OPFRException.class, () -> {
            reservationService.reserveCourt(TEST_MEMBER_2, new ReservationRequest("1", new DailyTimeRange("2020-08-01", 11, 1), ""));
        });
        assertEquals("Court is not available", exception.getMessage());
    }
    
    @Test
    void reserveFailedAsDateTooFar() {
        OPFRException exception = Assertions.assertThrows(OPFRException.class, () -> {
            reservationService.reserveCourt(TEST_MEMBER_1, new ReservationRequest("1", new DailyTimeRange("2020-08-10", 10, 2), ""));
        });
        assertEquals("Reservation is not allowed for 2020-08-10", exception.getMessage());
    }
    
    @Test
    void checkAvailability() {
        DailyTimeRange myTennisTime = new DailyTimeRange("2020-08-01", 10, 2);
        
        List<TennisCourt> availableCourts = reservationService.listAvailableCourts(myTennisTime);
        assertEquals(4, availableCourts.size());
        
        reservationService.reserveCourt(TEST_MEMBER_1, new ReservationRequest("1", myTennisTime, ""));
        
        availableCourts = reservationService.listAvailableCourts(myTennisTime);
        assertEquals(3, availableCourts.size());
    }
    
}
