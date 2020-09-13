package hu.kits.opfr.end2end.testframework;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import kong.unirest.HttpMethod;

public class UseCaseParser {

    public static List<TestCall> parseUseCaseFile(File useCaseFile) throws IOException {
        
        List<String> lines = Files.readAllLines(useCaseFile.toPath());
        List<List<String>> linesForCallList = new ArrayList<>();
        List<String> linesForCall = new ArrayList<>();
        for(String line : lines) {
            if(line.startsWith("Call")) {
                linesForCallList.add(linesForCall);
                linesForCall = new ArrayList<>();
            }
            linesForCall.add(line);
        }
        linesForCallList.add(linesForCall);
        
        return linesForCallList.stream()
            .filter(ucLines -> !ucLines.isEmpty())
            .map(UseCaseParser::parseCall)
            .collect(toList());
        
    }
    
    private static TestCall parseCall(List<String> lines) {
        
        String requestJson = ""; 
        String responseJson = "";
        
        List<String> nonCommentLines = lines.stream()
                .filter(line -> !line.startsWith("#"))
                .filter(line -> !line.isBlank())
                .collect(toList());
        
        String name = nonCommentLines.get(0);
        String urlTemplate = readValue(nonCommentLines.get(1));
        HttpMethod httpMethod = HttpMethod.valueOf(readValue(nonCommentLines.get(2)));
        
        int index = 3;
        if(nonCommentLines.get(index).startsWith("request")) {
            List<String> jsonLines = new ArrayList<>();
            for(index=index+1;index<nonCommentLines.size();index++) {
                String line = nonCommentLines.get(index);
                if(line.startsWith("response")) {
                    break;
                } else {
                    jsonLines.add(line);    
                }
            }
            requestJson = String.join("\n", jsonLines);
        }
        
        if(nonCommentLines.size() > index && nonCommentLines.get(index).startsWith("response")) {
            List<String> jsonLines = new ArrayList<>();
            for(index=index+1;index<nonCommentLines.size();index++) {
                String line = nonCommentLines.get(index);
                jsonLines.add(line);    
            }
            responseJson = String.join("\n", jsonLines);
        }
        
        return new TestCall(name, urlTemplate, httpMethod, requestJson, responseJson);
    }
    
    private static String readValue(String line) {
        return line.substring(line.indexOf(":") + 1).trim();
    }
    
    public static record TestCall(String name, String urlTemplate, HttpMethod httpMethod, String requestJson, String responseJson) {}
    
}
