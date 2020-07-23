package hu.kits.tennis.application;

import hu.kits.tennis.domain.court.TennisCourtRepository;
import hu.kits.tennis.domain.court.TennisCourtService;
import hu.kits.tennis.domain.reservation.ReservationRepository;
import hu.kits.tennis.domain.reservation.ReservationService;

public class ResourceFactory {

    private final TennisCourtRepository tennisCourtRepository;
    private final ReservationRepository reservationRepository;
    
    public ResourceFactory(TennisCourtRepository tennisCourtRepository, ReservationRepository reservationRepository) {
        this.tennisCourtRepository = tennisCourtRepository;
        this.reservationRepository = reservationRepository;
    }

    public TennisCourtService getTennisCourtService() {
        return new TennisCourtService(tennisCourtRepository);
    }
    
    public ReservationService getReservationService() {
        return new ReservationService(reservationRepository, tennisCourtRepository);
    }
    
}
