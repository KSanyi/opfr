package hu.kits.tennis.application;

import javax.sql.DataSource;

import hu.kits.tennis.domain.court.TennisCourtRepository;
import hu.kits.tennis.domain.court.TennisCourtService;
import hu.kits.tennis.domain.reservation.ReservationService;
import hu.kits.tennis.domain.user.UserService;
import hu.kits.tennis.domain.user.password.DummyPasswordHasher;
import hu.kits.tennis.infrastructure.database.ReservationFakeRepository;
import hu.kits.tennis.infrastructure.database.TennisCourtFakeRepository;
import hu.kits.tennis.infrastructure.database.UserJdbcRepository;

public class ResourceFactory {

    private final UserService userService;
    private final ReservationService reservationService;
    private final TennisCourtService tennisCourtService;
    
    public ResourceFactory(DataSource dataSource) {
        userService = new UserService(new UserJdbcRepository(dataSource), new DummyPasswordHasher());
        TennisCourtRepository tennisCourtRepository = new TennisCourtFakeRepository();
        reservationService = new ReservationService(new ReservationFakeRepository(), tennisCourtRepository);
        tennisCourtService = new TennisCourtService(tennisCourtRepository);
    }

    public TennisCourtService getTennisCourtService() {
        return tennisCourtService;
    }
    
    public ReservationService getReservationService() {
        return reservationService;
    }

    public UserService getUserService() {
        return userService;
    }
    
}
