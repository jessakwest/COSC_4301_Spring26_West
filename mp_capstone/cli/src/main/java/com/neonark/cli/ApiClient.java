package com.neonark.cli;

//this class handles http requests, response retrieval, and backend communication

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neonark.cli.dto.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient client;
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public ApiClient() {
        this.client = HttpClient.newHttpClient();
    }

    //route 1.a and 1.b: GET /api/creatures -- all creatures, including removed or only active
    public CreatureResponse[] getAllCreatures(boolean includeRemoved) throws IOException, InterruptedException {

        String endpoint = BASE_URL + "/creatures";

        if(includeRemoved) {
            endpoint += "?includeRemoved=true";
        }

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endpoint)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        handleErrorResponse(response);
        return mapper.readValue(response.body(),
                CreatureResponse[].class);
    }

    //route 2: GET /api/creaturess/{id}
    public CreatureResponse getCreatureById(Long id) throws IOException, InterruptedException {
        HttpRequest request =  HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creatures/" + id)).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        handleErrorResponse(response);
        return mapper.readValue(response.body(), CreatureResponse.class);
    }

    //route 3: POST /api/creatures
    public CreatureResponse createCreature(CreateCreatureRequest request) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creatures"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        handleErrorResponse(response);
        return mapper.readValue(response.body(), CreatureResponse.class);
    }

    //route 4: PUT /api/creatures/{id}/name
    public CreatureResponse renameCreature(Long id, RenameCreatureRequest request) throws IOException, InterruptedException {
        String json = mapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creatures/" + id + "/name"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        handleErrorResponse(response);
        return mapper.readValue(response.body(), CreatureResponse.class);
    }

    //route 5: DELETE /api/creatures/{id}
    public void deleteCreature(Long id) throws IOException, InterruptedException {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creatures/" + id))
                .header("Content-Type", "application/json")
                .DELETE().build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        handleErrorResponse(response);
        //else successful and return to stack trace
    }

    //route 6: GET /api/creatures/{id}/observations
    public CreatureObservationsResponse getCreatureObservations(Long id) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creatures/" + id + "/observations"))
                .GET().build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        handleErrorResponse(response);
        return mapper.readValue(response.body(), CreatureObservationsResponse.class);
    }

    //route 7: GET /api/feedings?time={HH:MM} -- feeding schedules by id
    public FeedingResponse[] getFeedingsByTime(String time) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/feedings?time=" + time))
                .GET().build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        handleErrorResponse(response);
        return mapper.readValue(response.body(), FeedingResponse[].class);
    }

    //route 8: GET /api/admin/users -- lists all users
    public UserResponse[] getAllUsers() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/admin/users?role=ADMIN"))
                .GET().build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        handleErrorResponse(response);
        return mapper.readValue(response.body(), UserResponse[].class);
    }

    //helper method to handle error responses / get http status codes
    private void handleErrorResponse(HttpResponse<String> response) {
        int status = response.statusCode();

        switch (status) {
            case 400:
                throw new RuntimeException(status + " Bad Request: Invalid input." + response.body());
            case 401:
                throw new RuntimeException(status + " Unauthorized: Authentication Required." + response.body());
            case 403:
                throw new RuntimeException(status + " Forbidden Access: Access Denied." + response.body());
            case 404:
                throw new RuntimeException(status + " Not Found: Resource doesn't exist." + response.body());
            case 409:
                throw new RuntimeException(status + " Conflict: Business rule violation and/or duplicated data." + response.body());
            default:
                if (status >= 400) {
                    throw new RuntimeException("HTTP error " +  response.statusCode() + ": " + response.body());
                }
        }
    }
}
