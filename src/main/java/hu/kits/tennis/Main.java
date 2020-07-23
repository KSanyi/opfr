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
        
        int port = getPort();
        
        ResourceFactory resourceFactory = new ResourceFactory(
                new TennisCourtFakeRepository(),
                new ReservationFakeRepository());
        
        new HttpServer(port, resourceFactory).start();
    }
    
    private static int getPort() {
        String port = loadMandatoryEnvVariable("PORT");

        try {
            int portNumber = Integer.parseInt(port);
            logger.info("PORT: " + port);
            return portNumber;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal system environment variable PORT: " + port);
        }
    }
    
    private static String loadMandatoryEnvVariable(String name) {
        String variable = System.getenv(name);
        if (variable == null) {
            throw new IllegalArgumentException("System environment variable " + name + " is missing");
        } else {
            return variable;
        }
    }

}
