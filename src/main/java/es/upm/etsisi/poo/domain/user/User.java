package es.upm.etsisi.poo.domain.user;

// Represents a user in the system, as defined in E2.
public abstract class User implements java.io.Serializable {
    private String id;
    private String name;
    private String email;

    public User(String id, String name, String email) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Error: ID cannot be null or empty.");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Error: Name cannot be null or empty.");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Error: Email cannot be null or empty.");
        }
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public abstract String toString();
}