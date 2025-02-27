public class StudentAttendanceModel {
    private String date;
    private String time;
    private String week;
    private String studentStatus;

    // Constructor with corrected parameter and assignment order
    public StudentAttendanceModel(String week, String studentStatus, String time, String date) {
        this.date = date;
        this.time = time;
        this.week = week;
        this.studentStatus = studentStatus;
    }

    // Getter methods
    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getWeek() {
        return week;
    }

    public String getStudentStatus() {
        return studentStatus;
    }

    // Setter methods
    public void setWeek(String week) {
        this.week = week;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }
}
