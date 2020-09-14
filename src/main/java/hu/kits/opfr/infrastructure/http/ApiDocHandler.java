package hu.kits.opfr.infrastructure.http;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import hu.kits.opfr.common.StringUtil;
import hu.kits.opfr.common.UseCaseFileParser;
import hu.kits.opfr.common.UseCaseFileParser.TestCall;
import io.javalin.http.Context;

class ApiDocHandler {

    void createTestCasesList(Context context) {
        
        File testCasesDir = Paths.get("test/test-cases").toFile();
        context.render("test-case-list.mustache", Map.of("testCases", testCasesDir.list()));
    }
    
    void createTestCaseDoc(Context context) {
        
        File testCasesDir = Paths.get("test/test-cases").toFile();
        String testCaseFileName = context.pathParam("testCase");
        
        Optional<File> testCaseFile = Stream.of(testCasesDir.listFiles())
            .filter(file -> file.getName().equals(testCaseFileName))
            .findAny();
        
        if(testCaseFile.isPresent()) {
            File file = testCaseFile.get();
            try {
                List<TestCall> useCaseCalls = UseCaseFileParser.parseUseCaseFile(file, true);
                String testCase = formatTestCaseName(file.getName());
                context.render("test-case.mustache", Map.of("testCase", testCase, "useCaseCalls", useCaseCalls));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            context.redirect("/api/docs");
        }
        
    }
    
    private static String formatTestCaseName(String testCaseFileName) {
        String name = testCaseFileName.split("\\.")[0];
        name = name.substring(name.indexOf("_") + 1);
        return StringUtil.capitalize(name);
    }
    
}
