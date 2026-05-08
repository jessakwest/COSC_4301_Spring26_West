package service;

import domain.Warden;
import repository.WardenRepository;

import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.*;

public class WardenService {
    private final WardenRepository repository;

    //private helpers
    private boolean isBlank(String input) {
        return input == null || input.trim().isEmpty();
    }

    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidExpirationDate(String earnedDate, String expirationDate) {
        try {
            LocalDate earned =  LocalDate.parse(earnedDate);
            LocalDate expiration = LocalDate.parse(expirationDate);

            return !expiration.isBefore(earned);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private final List<String> validRoles = Arrays.asList("admin", "field", "rift", "trainer", "astral");
    private final List<String> validStatuses = Arrays.asList("active", "onleave", "terminated", "retired");
    private final List<String> validClearances = Arrays.asList("alpha", "omega", "eclipse");

    //constructor
    public WardenService(WardenRepository repository) {
        this.repository = repository;
    }

    //(load pre-created wardens, treat file as read-only, display as a table with correct headers matching fields from database!)
    public List<Warden> getAllWardens() {
        return repository.findAll();
    }

    //Add New Warden (collect inputs, validate, show simulated outbound request, show simulated success/failure)
    public String addWarden(String f_name, String l_name, String role, String status, String clearance) {
        //validation
        if (isBlank(f_name) || isBlank(role) || isBlank(status) || isBlank(clearance)) {
            return """
                RESULT: FAILED
                ERROR: Required fields cannot be blank.
                """;
        }
        if(!validRoles.contains(role)) {
            return """
                RESULT: FAILED
                ERROR: Invalid role entered. Allowed values: %s
                """.formatted(validRoles);
        }
        if(!validStatuses.contains(status)) {
            return """
                RESULT: FAILED
                ERROR: Invalid status entered. Allowed values: %s
                """.formatted(validStatuses);
        }
        if(!validClearances.contains(clearance)) {
            return """
                RESULT: FAILED
                ERROR: Invalid clearance entered. Allowed values: %s
                """.formatted(validClearances);
        }
        if(repository.nameExists(f_name, l_name)) {
            return """
                RESULT: FAILED
                REASON: Duplicate Warden identity detected.
                Existing identity: %s %s
                """.formatted(f_name, l_name);
        }

        System.out.println("Adding new Warden...\n");

        return """
                ACTION: add new warden
                INPUTS: String first_name, String last_name (optional), String role, String status
                WOULD SEND: POST /api/wardens
                BRIEF DESCRIPTION: creates a new warden onboarding record
                PAYLOAD (JSON)
                {
                     "firstName": "%s",
                     "lastName": "%s",
                     "role": "%s",
                     "status": "%s",
                     "clearance": "%s"
                }
                RESULT: SUCCESS (simulated)
                """.formatted(f_name, l_name, role, status, clearance);
    }

    public String updateWardenRole(int id, String newRole) {
        System.out.println("Updating Warden " + id + "'s Role...\n");
        //check if id and role are valid
        Warden w = repository.findById(id);
        if(w == null) {
            return """
                RESULT: FAILED
                ERROR: Warden ID does not exist; no record to update.
                """;
        }
        if(!validRoles.contains(newRole)) {
            return """
                RESULT: FAILED
                ERROR: Invalid role entered; record not updated.
                Allowed values: %s
                """.formatted(validRoles);
        }
        //else successful
        return """
                ACTION: update warden role
                WOULD SEND: PUT /api/wardens/%d/role
                BRIEF DESCRIPTION: update the role assignment for an existing warden's record
                PAYLOAD (JSON) { "role": "%s" }
                RESULT: SUCCESS (simulated)
                """.formatted(id, newRole);
    }

    public String updateWardenStatus(int id, String newStatus) {
        System.out.println("Updating Warden " + id + "'s Status...\n");
        //check if id and status are valid
        Warden w = repository.findById(id);
        if(w == null) {
            return """
                RESULT: FAILED
                ERROR: Warden ID does not exist; no record to update.
                """;
        }
        if(!validStatuses.contains(newStatus)) {
            return """
                RESULT: FAILED
                ERROR: Invalid status entered; record not updated.
                Allowed values: %s
                """.formatted(validStatuses);
        }
        return """
                ACTION: update warden status
                WOULD SEND: PUT /api/wardens/%d/status
                BRIEF DESCRIPTION: update a warden's current status
                PAYLOAD (JSON) { "status": "%s" }
                RESULT: SUCCESS (simulated)
                """.formatted(id, newStatus);
    }

    public String terminateWarden(int id) {
        System.out.println("Terminating Warden " + id + "...\n");
        //check if id and warden record exists
        Warden w = repository.findById(id);
        if(w == null) {
            return """
                RESULT: FAILED
                ERROR: Warden ID does not exist; no record to update status to "terminated".
                """;
        }
        return """
                ACTION: terminate a warden
                WOULD SEND: PUT /api/wardens/%d/terminate
                BRIEF DESCRIPTION: update warden's status to "terminated"
                PAYLOAD (JSON) { "status": "terminated" }
                RESULT: SUCCESS (simulated)
                """.formatted(id);
    }

    public String addCertification(int id, String certificationName, String earnedDate, String expirationDate) {
        System.out.println("Adding certification to warden " + id + "...\n");

        Warden w = repository.findById(id);

        if(w == null) {
            return """
                    RESULT: FAILED
                    ERROR: Warden ID does not exist; no record to add certification.
                    """;
        }
        if (isBlank(certificationName) || isBlank(earnedDate)) {
            return """
                RESULT: FAILED
                ERROR: Certification name and earned date are required.
                """;
        }
        if (!isValidDate(earnedDate)) {
            return """
                RESULT: FAILED
                ERROR: Invalid date format. Use YYYY-MM-DD.
                """;
        }
        if (!expirationDate.isBlank()) {
            if (!isValidDate(expirationDate)) {
                return """
                RESULT: FAILED
                ERROR: Invalid date format. Use YYYY-MM-DD.
                """;
            }
            if (!isValidExpirationDate(earnedDate, expirationDate)) {
                return """
                    RESULT: FAILED
                    ERROR: Expiration date can't be before earned date.
                    """;
            }
        }

        return """
                ACTION: add a certification to warden record
                INPUTS: int warden id, String certification name, String earned date (YYYY-MM-DD), String expiration date (YYYY-MM-DD)(optional)
                WOULD SEND: POST /api/wardens/%d/certifications
                BRIEF DESCRIPTION: creates new certification record, associated with an existing warden record
                PAYLOAD (JSON) {
                    "certificationName": "%s",
                     "earnedDate": "%s",
                     "expirationDate": "%s" }
                RESULT: SUCCESS (simulated)
                """.formatted(id, certificationName, earnedDate, expirationDate);
    }

    public String viewCertifications(int id) {
        System.out.println("Viewing all certifications for warden " + id + "...\n");

        Warden w = repository.findById(id);
        if(w == null) {
            return """
                    RESULT: FAILED
                    ERROR: Warden ID does not exist; no record to add certification.
                    """;
        }

        return """
                ACTION: view list of all certifications for specific warden
                INPUTS: int id
                WOULD SEND: GET /api/wardens/%d/certifications
                BRIEF DESCRIPTION: retrieve all certification records associated with an existing warden
                RESULT: SUCCESS (simulated)
                RETURNED DATA:
                [
                    {
                        "name": "First Aid Level 1",
                        "earnedDate": "2020-01-10",
                        "expirationDate": "2023-01-10"
                    },
                    {
                        "name": "Containment Operations Level 2",
                        "earnedDate": "2020-01-10",
                        "expirationDate": "2023-01-10"
                    },
                ]
                """.formatted(id);
    }
}
