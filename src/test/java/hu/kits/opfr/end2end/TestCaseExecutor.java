package hu.kits.opfr.end2end;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.eclipse.jetty.http.HttpMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.opfr.TestUtil;
import hu.kits.opfr.application.ResourceFactory;
import hu.kits.opfr.common.Clock;
import hu.kits.opfr.common.IdGenerator;
import hu.kits.opfr.common.UseCaseFileParser;
import hu.kits.opfr.common.UseCaseFileParser.TestCall;
import hu.kits.opfr.end2end.testframework.TestCaseDirSource;
import hu.kits.opfr.infrastructure.http.HttpServer;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class TestCaseExecutor {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private int port;
    private HttpServer httpServer;
    
    @BeforeEach
    private void init() throws Exception {
        DataSource dataSource = InMemoryDataSourceFactory.createDataSource();
        
        ResourceFactory resourceFactory = new ResourceFactory(dataSource);
        
        port = TestUtil.findFreePort();
        httpServer = new HttpServer(port, resourceFactory);
        httpServer.start();
        
        Clock.setStaticTime(LocalDateTime.of(2020,9,1, 8,0));
        IdGenerator.useFakeGenerator();
    }
    
    @AfterEach
    private void stop() {
        httpServer.stop();
    }
    
    @ParameterizedTest
    @TestCaseDirSource("test/test-cases")
    void executeTestCase(File testCaseFile) throws IOException {
        
        logger.info("Executing test case: {}", testCaseFile.getName());
        
        List<TestCall> testCalls = UseCaseFileParser.parseUseCaseFile(testCaseFile, false);
        
        for(TestCall testCall : testCalls) {
            
            logger.info("-------------------------- {} --------------------------", testCall.name());
            
            String url = testCall.urlTemplate().replaceAll("<url-base>", "http://localhost:" + port);
            HttpResponse<String> httpResponse;
            if(testCall.httpMethod() == HttpMethod.GET) {
                httpResponse = Unirest.get(url).asString();
                logger.info("Calling GET {}", url);
            } else {
                httpResponse = Unirest.post(url).body(testCall.requestJson()).asString();
                logger.info("Calling POST {}", url);
            }
            
            logger.info("Response status: {} {}", httpResponse.getStatus(), httpResponse.getStatusText());
            
            assertEquals(testCall.responseStatus(), httpResponse.getStatus());
            
            if(!testCall.responseJson().isBlank()) {
                assertEquals(normalize(testCall.responseJson()), normalize(httpResponse.getBody()));
            }
            
            logger.info("Response: {}", httpResponse.getBody());
        }
    }
    
    private static String normalize(String responseJson) {
        try {
            return new JSONObject(responseJson).toString(2);
        } catch(Exception ex) {
            return new JSONArray(responseJson).toString(2);
        }
    }

}
