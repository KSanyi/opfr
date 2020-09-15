package hu.kits.opfr.common;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.http.HttpMethod;

public class UseCaseFileParser {

    public static List<TestCall> parseUseCaseFile(File useCaseFile, boolean withComments) throws IOException {
        
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
            .filter(l -> !l.isEmpty())
            .map(l -> parseCall(l, withComments))
            .collect(toList());
    }
    
    private static TestCall parseCall(List<String> callLines, boolean withComments) {
        
        String requestJson = ""; 
        String responseJson = "";
        
        List<String> lines = callLines.stream()
                .filter(line -> !line.startsWith("#"))
                .filter(line -> !line.isBlank())
                .collect(toList());
        
        String name = lines.get(0);
        String urlTemplate = readValue(lines.get(1));
        HttpMethod httpMethod = HttpMethod.valueOf(readValue(lines.get(2)));
        
        int index = 3;
        if(lines.get(index).startsWith("request")) {
            List<String> jsonLines = new ArrayList<>();
            for(index=index+1;index<lines.size();index++) {
                String line = lines.get(index);
                if(line.startsWith("response")) {
                    break;
                } else {
                    jsonLines.add(line);    
                }
            }
            requestJson = String.join("\n", jsonLines);
        }
        
        if(lines.size() > index && lines.get(index).startsWith("response")) {
            List<String> jsonLines = new ArrayList<>();
            for(index=index+1;index<lines.size();index++) {
                String line = lines.get(index);
                jsonLines.add(line);    
            }
            responseJson = String.join("\n", jsonLines);
        }
        
        return new TestCall(name, urlTemplate, httpMethod, requestJson, responseJson);
    }
    
    private static String readValue(String line) {
        return line.substring(line.indexOf(":") + 1).trim();
    }
    
    public static record TestCall(String name, String urlTemplate, HttpMethod httpMethod, String requestJson, String responseJson) {
        
        public String path() {
            return urlTemplate.replace("<url-base>", "");
        }
        
    }
    
}
