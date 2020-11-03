package msr.attend.admin.Model;

public class StudentModel {
    private String id;
    private String name;
    private String department;
    private String studentId;

    public StudentModel() {
    }

    public StudentModel(String name, String department, String studentId) {
        this.name = name;
        this.department = department;
        this.studentId = studentId;
    }

    public StudentModel(String id, String name, String department, String studentId) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.studentId = studentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}
