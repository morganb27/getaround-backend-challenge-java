package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        try (InputStream inputStream = Main.class.getResourceAsStream("/input.json")) {
            JsonNode rootNode = objectMapper.readTree(inputStream);

            JsonNode carsNode = rootNode.path("cars");
            JsonNode rentalsNode = rootNode.path("rentals");


            List<Map<String, Object>> rentals = new ArrayList<>();

            for (JsonNode rentalNode: rentalsNode) {
                Map<String, Object> rental = new HashMap<>();
                int rentalId = rentalNode.get("id").asInt();
                int distance = rentalNode.get("distance").asInt();
                int carId = rentalNode.get("car_id").asInt();
                LocalDate startDate = LocalDate.parse(rentalNode.get("start_date").asText());
                LocalDate endDate = LocalDate.parse(rentalNode.get("end_date").asText());
                int days = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

                JsonNode carNode = getNodeById(carsNode, carId);
                assert carNode != null;
                int rentalPrice = days * carNode.get("price_per_day").asInt();
                int distancePrice = distance * carNode.get("price_per_km").asInt();


                rental.put("id", rentalId);
                rental.put("price", rentalPrice + distancePrice);

                rentals.add(rental);
            }

            Map<String, Object> output = new HashMap<>();
            output.put("rentals", rentals);

            String JsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
            System.out.println(JsonOutput);

        } catch (Exception e) {
            System.err.println("Error while reading JSON: " + e.getMessage());
        }
    }

    private static JsonNode getNodeById(JsonNode arrayNode, int id) {
        for (JsonNode node: arrayNode) {
            if (node.has("id") && node.get("id").asInt() == id) {
                return node;
            }
        }
        return null;
    }
}