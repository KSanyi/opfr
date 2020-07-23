package hu.kits.tennis;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.tennis.application.ResourceFactory;
import hu.kits.tennis.infrastructure.database.ReservationFakeRepository;
import hu.kits.tennis.infrastructure.database.TennisCourtFakeRepository;
import hu.kits.tennis.infrastructure.http.HttpServer;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public static void main(String[] args) {
        
        logger.info("Starting application");
        
        ResourceFactory resourceFactory = new ResourceFactory(
                new TennisCourtFakeRepository(),
                new ReservationFakeRepository());
        
        new HttpServer(7777, resourceFactory).start();
    }

}
