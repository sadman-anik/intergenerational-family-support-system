import java.io.Serializable;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;

    private int eventId;
    private int fidn;
    private String eventType;
    private String eventDate;
    private String eventTime;
    private String duration;
    private String venue;
    private double cost;

    public Event() {
    }

    public Event(int fidn, String eventType, String eventDate, String eventTime,
                 String duration, String venue, double cost) {
        this.fidn = fidn;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.duration = duration;
        this.venue = venue;
        this.cost = cost;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getFidn() {
        return fidn;
    }

    public void setFidn(int fidn) {
        this.fidn = fidn;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Event ID: " + eventId + " | FIDN: " + fidn + " | " + eventType
                + " | " + eventDate + " " + eventTime + " | " + venue + " | Cost: $" + cost;
    }
}
