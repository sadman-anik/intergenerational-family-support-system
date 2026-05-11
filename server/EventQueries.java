import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

// Lab-style query class for event-related database operations.
public class EventQueries {
    private PreparedStatement insertEvent;
    private PreparedStatement getAllEvents;

    public EventQueries(Connection connect) throws SQLException {
        insertEvent = connect.prepareStatement(
                "INSERT INTO EVENTS "
                        + "(fidn, eventType, eventDate, eventTime, duration, venue, cost) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);

        getAllEvents = connect.prepareStatement(
                "SELECT * FROM EVENTS ORDER BY eventId");
    }

    public synchronized int addNewEvent(Event event) throws SQLException {
        insertEvent.setInt(1, event.getFidn());
        insertEvent.setString(2, event.getEventType());
        insertEvent.setString(3, event.getEventDate());
        insertEvent.setString(4, event.getEventTime());
        insertEvent.setString(5, event.getDuration());
        insertEvent.setString(6, event.getVenue());
        insertEvent.setDouble(7, event.getCost());

        insertEvent.executeUpdate();

        ResultSet keys = insertEvent.getGeneratedKeys();
        if (keys.next()) {
            return keys.getInt(1);
        }
        return -1;
    }

    public synchronized ArrayList<Event> getEventsList() throws SQLException {
        ResultSet results = getAllEvents.executeQuery();
        ArrayList<Event> eventList = new ArrayList<>();

        while (results.next()) {
            Event event = new Event();
            event.setEventId(results.getInt("eventId"));
            event.setFidn(results.getInt("fidn"));
            event.setEventType(results.getString("eventType"));
            event.setEventDate(results.getString("eventDate"));
            event.setEventTime(results.getString("eventTime"));
            event.setDuration(results.getString("duration"));
            event.setVenue(results.getString("venue"));
            event.setCost(results.getDouble("cost"));
            eventList.add(event);
        }

        return eventList;
    }
}
