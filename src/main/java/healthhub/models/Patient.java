package healthhub.models;

public class Patient {

    // --- Fields matching the database columns ---
    private int id;       // Auto-incremented primary key
    private String name;  // Patient full name
    private String phone; // Contact phone number
    private int age;      // Patient age
    private String gender; // "Male" or "Female"

    // -----------------------------------------------
    // Constructor — used when creating a new Patient
    // object from a database row or from a form
    // -----------------------------------------------
    public Patient(int id, String name, String phone, int age, String gender) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.age = age;
        this.gender = gender;
    }

    // -----------------------------------------------
    // Getters — used to read the patient's data
    // -----------------------------------------------
    public int getId()       { return id; }
    public String getName()  { return name; }
    public String getPhone() { return phone; }
    public int getAge()      { return age; }
    public String getGender(){ return gender; }

    // -----------------------------------------------
    // Setters — used when editing a patient's data
    // -----------------------------------------------
    public void setId(int id)          { this.id = id; }
    public void setName(String name)   { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAge(int age)        { this.age = age; }
    public void setGender(String gender){ this.gender = gender; }

    // -----------------------------------------------
    // toString — useful for debugging
    // -----------------------------------------------
    @Override
    public String toString() {
        return "Patient{id=" + id + ", name='" + name + "', phone='" + phone +
                "', age=" + age + ", gender='" + gender + "'}";
    }
}