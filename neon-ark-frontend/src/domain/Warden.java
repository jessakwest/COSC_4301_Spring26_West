package domain;

public class Warden {
    private int id; //assigned by server
    private String f_name;
    private String l_name;
    private String role;
    private String status;
    private String clearance;
    private boolean is_deleted;

    //constructor
    public Warden(String f_name, String l_name, String role, String status, String clearance) {
        this.id = -1;
        this.f_name = f_name;
        this.l_name = l_name;
        this.role = role;
        this.status = status;
        this.clearance = clearance;
        this.is_deleted = false;
    }

    //setters & getters
    public int getId() { return id; }
    public String getFirstName() { return f_name; }
    public String getLastName() { return l_name; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getClearance() { return clearance; }
    public boolean getIsDeleted() { return is_deleted; }

    public void setId(int id) { this.id = id; }
    public void setFirstName(String f_name) { this.f_name = f_name; }
    public void setLastName(String l_name) { this.l_name = l_name; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }
    public void setClearance(String clearance) { this.clearance = clearance; }
    public void setIsDeleted(boolean is_deleted) { this.is_deleted = is_deleted; }

    @Override
    public String toString() {
        return f_name + " " + l_name + "\nrole: " + role + "\nstatus: " + status + "\nclearance: " + clearance;
    }
}
