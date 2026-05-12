package com.neonark.cli;

//this class handles http requests, response retrieval, and backend communication

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.neonark.cli.dto.CreateCreatureRequest;
import com.neonark.cli.dto.CreatureResponse;
import com.neonark.cli.dto.RenameCreatureRequest;

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

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint)).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return mapper.readValue(response.body(),
                CreatureResponse[].class);
    }
    //route 2: GET /api/creaturess/{id}
    public CreatureResponse getCreatureById(Long id) throws IOException, InterruptedException {
        HttpRequest request =  HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creatures/" + id)).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() > 300) {
            throw new RuntimeException("API returned HTTP status: " +  response.statusCode());
        }
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

        if (response.statusCode() > 300) {
            throw new RuntimeException("API returned HTTP status: " +  response.statusCode());
        }
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

        if (response.statusCode() > 300) {
            throw new RuntimeException("API returned HTTP status: " +  response.statusCode());
        }
        return mapper.readValue(response.body(), CreatureResponse.class);
    }

    //route 5: DELETE /api/creatures/{id}
    public void deleteCreature(Long id) throws IOException, InterruptedException {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/creatures/" + id))
                .DELETE().build();

        HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() > 300) {
            throw new RuntimeException("API returned HTTP status: " +  response.statusCode());
        }

        //else successful and return to stack trace
    }


    //route 6: GET /api/creatures/{id}/observations
    //route 7: GET /api/feedings?time={HH:MM} -- feeding schedules by id
    //route 8: GET /api/admin/users -- lists all users
    //internal: PUT /api/creatures/{id}/restore

}
