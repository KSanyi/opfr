package hu.kits.tennis.domain.reservation;

import static hu.kits.tennis.domain.TestUtil.TEST_MEMBER_1;
import static hu.kits.tennis.domain.TestUtil.TEST_MEMBER_2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.kits.tennis.common.DateRange;
import hu.kits.tennis.domain.common.DailyTimeRange;
import hu.kits.tennis.domain.court.TennisCourt;
import hu.kits.tennis.infrastructure.database.ReservationFakeRepository;
import hu.kits.tennis.infrastructure.database.TennisCourtFakeRepository;

public class ReservationServiceTest {

    private ReservationService reservationService;
    
    @BeforeEach
    void init() {
        reservationService = new ReservationService(new ReservationFakeRepository(), new TennisCourtFakeRepository());
    }
    
    @Test
    void reserveSuccesfully() {
        
        boolean sucessfullReservationForSaturdayMorning = reservationService.reserveCourt(TEST_MEMBER_1, "1", new DailyTimeRange("2020-08-01", 10, 2), "");
        assertTrue(sucessfullReservationForSaturdayMorning);
        
        boolean sucessfullReservationForSaturdayEvening = reservationService.reserveCourt(TEST_MEMBER_1, "1", new DailyTimeRange("2020-08-01", 19, 1), "");
        assertTrue(sucessfullReservationForSaturdayEvening);
        
        boolean sucessfullReservationForSunday = reservationService.reserveCourt(TEST_MEMBER_1, "1", new DailyTimeRange("2020-08-02", 10, 2), "");
        assertTrue(sucessfullReservationForSunday);
        
        boolean sucessfullReservationForCourt2 = reservationService.reserveCourt(TEST_MEMBER_1, "2", new DailyTimeRange("2020-08-01", 10, 2), "");
        assertTrue(sucessfullReservationForCourt2);
        
        List<Reservation> member1Reservations = reservationService.listMyReservations(TEST_MEMBER_1, DateRange.of(2020));
        
        assertEquals(4, member1Reservations.size());
        assertEquals("1", member1Reservations.get(0).courtId());
        assertEquals(new DailyTimeRange("2020-08-01", 10, 2), member1Reservations.get(0).dailyTimeRange());
        
        assertTrue( reservationService.listMyReservations(TEST_MEMBER_2, DateRange.of(2020)).isEmpty());
    }
    
    @Test
    void reserveFailed() {
        
        boolean sucessfullReservationForSaturdayMorning = reservationService.reserveCourt(TEST_MEMBER_1, "1", new DailyTimeRange("2020-08-01", 10, 2), "");
        assertTrue(sucessfullReservationForSaturdayMorning);
        
        boolean sucessfullSecondReservationForSaturdayMorning = reservationService.reserveCourt(TEST_MEMBER_2, "1", new DailyTimeRange("2020-08-01", 11, 1), "");
        assertFalse(sucessfullSecondReservationForSaturdayMorning);
    }
    
    @Test
    void checkAvailability() {
        
        DailyTimeRange myTennisTime = new DailyTimeRange("2020-08-01", 10, 2);
        
        List<TennisCourt> availableCourts = reservationService.listAvailableCourts(myTennisTime);
        assertEquals(4, availableCourts.size());
        
        reservationService.reserveCourt(TEST_MEMBER_1, "1", myTennisTime, "");
        
        availableCourts = reservationService.listAvailableCourts(myTennisTime);
        assertEquals(3, availableCourts.size());
    }
    
}
