package hu.kits.opfr.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.kits.opfr.TestUtil;
import hu.kits.opfr.application.ResourceFactory;
import hu.kits.opfr.infrastructure.http.HttpServer;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class UserHandlerTest {

    private int port;
    private HttpServer httpServer;
    
    @BeforeEach
    private void init() throws Exception {
        DataSource dataSource = InMemoryDataSourceFactory.createDataSource();
        
        ResourceFactory resourceFactory = new ResourceFactory(dataSource);
        
        port = TestUtil.findFreePort();
        httpServer = new HttpServer(port, resourceFactory);
        httpServer.start();
    }
    
    @AfterEach
    private void stop() {
        httpServer.stop();
    }
    
    @Test
    public void createUser() {
        
        String url = "http://localhost:" + port + "/api/users";
        
        HttpResponse<String> httpResponse = Unirest.get(url).asString();
        
        assertEquals("[]", httpResponse.getBody());
        
        String json = TestUtil.readTestJson("create-user.json");
        httpResponse = Unirest.post(url).body(json).asString();
        
        assertEquals("", httpResponse.getBody());
        
        httpResponse = Unirest.get(url).asString();
        
        String expectedResponse = """
                [{"role":"ADMIN","phone":"+36703699208","name":"Kócsó Sándor","userId":"ksanyi","email":"kocso.sandor.gabor@gmail.com"}]""";
        
        assertEquals(expectedResponse.trim(), httpResponse.getBody());
    }
    
    @Test
    public void authenticateUser() {
        
        String url = "http://localhost:" + port + "/api/users";
        
        String json = TestUtil.readTestJson("create-user.json");
        Unirest.post(url).body(json).asString();
        
        json = TestUtil.readTestJson("authenticate.json");
        HttpResponse<String> httpResponse = Unirest.post(url + "/authenticate/ksanyi").body(json).asString();
        
        String expectedResponse = """
                {"role":"ADMIN","phone":"+36703699208","name":"Kócsó Sándor","userId":"ksanyi","email":"kocso.sandor.gabor@gmail.com"}""";
        
        assertEquals(expectedResponse.trim(), httpResponse.getBody());
    }
    
    @Test
    public void changePassword() {
        
        String url = "http://localhost:" + port + "/api/users";
        
        String json = TestUtil.readTestJson("create-user.json");
        Unirest.post(url).body(json).asString();
        
        json = TestUtil.readTestJson("change-password.json");
        HttpResponse<String> httpResponse = Unirest.post(url + "/change-password/ksanyi").body(json).asString();
        
        assertEquals(200, httpResponse.getStatus());
        
        json = TestUtil.readTestJson("authenticate.json");
        httpResponse = Unirest.post(url + "/authenticate/ksanyi").body(json).asString();
        
        assertEquals(401, httpResponse.getStatus());
        
        json = """
                {"password": "Korte1234"}
                """;
        httpResponse = Unirest.post(url + "/authenticate/ksanyi").body(json).asString();
        
        assertEquals(200, httpResponse.getStatus());
    }
    
}
