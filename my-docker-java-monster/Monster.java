//Code by Jessa K. West
//COSC 4306: Modern Programming
//Project 1: Monster Class

public class Monster {
    private String name;
    private String type;
    private String description;

    protected Monster(String name, String type, String description) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("Monster name cannot be null/empty.");
        }
        if(type == null || type.isBlank()) {
            throw new IllegalArgumentException("Monster type cannot be null/empty.");
        }
        if(description == null || description.isBlank()) {
            throw new IllegalArgumentException("Monster description cannot be null/empty.");
        }
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public String getName() { return name;}
    public String getType() { return type; }
    public String getDescription() {return description; }

    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return String.format("Monster %s%n" + "Type: %s%n" + "Description: %s%n", getName(), getType(), getDescription());
    }
}
