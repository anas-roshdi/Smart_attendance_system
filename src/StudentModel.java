public class StudentModel {
    private String studentName;
    private String studentId;
    private String studentStatus;

    public StudentModel(String studentName,String studentId, String studentStatus){
        this.studentName = studentName;
        this.studentId = studentId;
        this.studentStatus = studentStatus;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }
    public String getStudentStatus() {
        return studentStatus;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

}
