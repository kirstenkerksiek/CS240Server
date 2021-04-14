package DataManagement;

import Model.Event;
import Model.Person;

public class MarkerDetails {
    public Event event;
    public Person person;

    public MarkerDetails(Person person,Event event) {
        this.person = person;
        this.event = event;
    }
}