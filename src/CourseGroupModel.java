public class CourseGroupModel {
    private String courseName;
    private String groupName;
    private String courseId;
    private String groupId;
    public CourseGroupModel(String courseName, String groupName, String courseId, String groupId) {
        this.courseName = courseName;
        this.groupName = groupName;
        this.groupId = groupId;
        this.courseId = courseId ;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getGroupName() {
        return groupName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
