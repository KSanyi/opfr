package hu.kits.opfr.testdatagen;

import static java.util.stream.Collectors.joining;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONObject;

import hu.kits.opfr.common.DateTimeRange;
import hu.kits.opfr.common.StringUtil;
import hu.kits.opfr.domain.user.Role;
import hu.kits.opfr.domain.user.UserData;
import kong.unirest.Unirest;

public class TestDataGenerator {

    private static final String BASE_URL = "http://opfr.herokuapp.com";
    
    public static void main(String[] args) {
        
        //createRandomUsers();
        createRandomReservations(100);
    }
    
    private static void createRandomReservations(int n) {
        
        List<String> userIds = getUserIds();
        System.out.println("User ids: " + userIds);
        
        var httpResponse = Unirest.get(BASE_URL + "/api/reservations-range").asString();
        if(httpResponse.getStatus() != 200) {
            throw new RuntimeException();
        }
        
        var jsonObject = new JSONObject(httpResponse.getBody());
        DateTimeRange reservationDateRange = new DateTimeRange(LocalDateTime.parse(jsonObject.getString("from")), LocalDateTime.parse(jsonObject.getString("to")));
        
        System.out.println("Reservation range: " + reservationDateRange);
        
        Random random = new Random();
        for(int i=0;i<n;i++) {
            String userId = userIds.get(random.nextInt(userIds.size()));
            String courtId = String.valueOf(random.nextInt(5) + 1);
            int length = random.nextInt(5) < 1 ? 1 : 2; 
            LocalDateTime time = getRandomTime(reservationDateRange);
            JSONObject json = new JSONObject()
                    .put("userId", userId)
                    .put("courtId", courtId)
                    .put("time", new JSONObject()
                            .put("date", time.toLocalDate().toString())
                            .put("timeRange", new JSONObject()
                                .put("startAt", time.getHour())
                                .put("hours", length)))
                    .put("comment", "");
            var response = Unirest.post(BASE_URL + "/api/reservations").body(json.toString()).asString();
            System.out.println("Reervation: " + response.getStatus());
        }
        
    }
    
    private static LocalDateTime getRandomTime(DateTimeRange reservationDateRange) {
        
        int hours = (int)ChronoUnit.HOURS.between(reservationDateRange.from(), reservationDateRange.to());
        while(true) {
            int hour = new Random().nextInt(hours);
            LocalDateTime dateTime = reservationDateRange.from().plusHours(hour);
            if(6 < dateTime.getHour() && dateTime.getHour() < 22) {
                return dateTime;
            }  
        }
    }

    private static List<String> getUserIds() {
        var httpResponse = Unirest.get(BASE_URL + "/api/users").asString();
        if(httpResponse.getStatus() != 200) {
            throw new RuntimeException();
        }
        
        List<String> userIds = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(httpResponse.getBody());
        for(int i=0;i<jsonArray.length();i++) {
            String userId = jsonArray.getJSONObject(i).getString("userId");
            userIds.add(userId);
        }
        return userIds;
    }

    private static void createRandomUsers() {
        List<UserData> users = generateRandomUser(4);
        
        users.stream().parallel().forEach(userData -> {
            JSONObject json = new JSONObject()
                    .put("role", userData.role().name())
                    .put("phone", userData.phone())
                    .put("name", userData.name())
                    .put("userId", userData.userId())
                    .put("isActive", userData.isActive())
                    .put("email", userData.email())
                    .put("password", "Alma1234");
            var response = Unirest.post(BASE_URL + "/api/users").body(json.toString()).asString();
            System.out.println(response.getStatusText());
        });
    }
    
    private static List<UserData> generateRandomUser(int n) {
        
        Random random = new Random();
        
        RandomWordPicker lastNamePicker = new RandomWordPicker(Paths.get("./demodata/LastNames.txt"));
        RandomWordPicker firstNamePicker = new RandomWordPicker(Paths.get("./demodata/FirstNames.txt"));
        
        List<UserData> userDataList = new ArrayList<>();
        
        for(int i=0;i<n;i++) {
            String lastName = lastNamePicker.pickRandomWord();
            String firstName = firstNamePicker.pickRandomWord();
            
            String userId = StringUtil.cleanNameString(lastName.charAt(0) + firstName) + (random.nextInt(10) + 1);
            String name = lastName + " " + firstName;
            String email = userId + "@testmail.hu";
            String phone = generatePhoneNumber();
            
            userDataList.add(new UserData(userId, name, Role.MEMBER, phone, email, true));
        }
        
        return userDataList;
    }
    
    private static String generatePhoneNumber() {
        Random random = new Random();
        String carrier = switch(random.nextInt(3)) {
            case 0 -> "20";
            case 1 -> "30";
            default -> "70";
        };
        
        return "06" + carrier + IntStream.generate(() -> random.nextInt(10)).limit(9).mapToObj(String::valueOf).collect(joining(""));
    }

}
