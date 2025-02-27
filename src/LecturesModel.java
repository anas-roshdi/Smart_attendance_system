
public class LecturesModel {
    private String date;
    private String time;
    private String topic;
    private String ID;
    private String week;


    public LecturesModel(String date,String time, String topic,String ID,String week){
        this.time = time;
        this.date = date;
        this.topic = topic;
        this.ID = ID;
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
     public void setTopic(String topic) {
        this.topic = topic;
    }
    public String getTopic() {
        return topic;
    }
    public String getID() {
        return ID;
    }
     public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }
}
