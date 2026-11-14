/**
 * Simple data holder for one certificate recipient.
 */
public class Participant {
    private final String name;
    private final String course;
    private final String date;

    public Participant(String name, String course, String date) {
        this.name = name;
        this.course = course;
        this.date = date;
    }

    public String getName()   { return name; }
    public String getCourse() { return course; }
    public String getDate()   { return date; }
}
