package hu.kits.opfr;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;

import hu.kits.opfr.domain.user.Role;
import hu.kits.opfr.domain.user.User;

public class TestUtil {

    public static final User TEST_MEMBER_1 = new User("T1", "Test user 1", Role.MEMBER, "+362012345678", "test1@opfr.hu", true);
    public static final User TEST_MEMBER_2 = new User("T2", "Test user 2", Role.MEMBER, "+367012345678", "test2@opfr.hu", true);
    
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
