package hu.kits.opfr;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestUtil {

    public static int findFreePort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static String readTestJson(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get("test/" + fileName)));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
