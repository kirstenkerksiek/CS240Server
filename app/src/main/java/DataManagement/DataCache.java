package DataManagement;

import android.graphics.Color;

import com.example.family_map_client.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Model.Event;
import Model.Person;
import Results.AllEventsResult;
import Results.AllPersonResult;
import Results.LoginResult;
import Results.RegisterResult;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

public class DataCache {
    private MainActivity main;
    private final int mapType = MAP_TYPE_NORMAL;

    private static DataCache _instance = new DataCache();
    private String serverHost = "192.168.86.169";
    private String serverPort = "8080";

    private RegisterResult registerResult;
    private AllEventsResult eventsResultRegister;
    private AllPersonResult personsResultRegister;

    private LoginResult loginResult;
    private AllEventsResult eventsResultLogin;
    private AllPersonResult personsResultLogin;

    private boolean success;
    private String username;
    private String personID;
    private String authID;
    private boolean loggedIn = false;

    private Map<String, Person> personMap = new HashMap<>();
    private Set<Person> motherFemales= new HashSet();
    private Set<Person> fatherMales= new HashSet();
    private Set<Person> motherMales= new HashSet();
    private Set<Person> fatherFemales= new HashSet();

    private ArrayList<Person> persons;
    private ArrayList<Event> events;
    private ArrayList<Event> visibleEvents;

    private boolean lifeStoryLinesVisible;
    private boolean familyTreeLinesVisible;
    private boolean spouseLinesVisible;

    private boolean fatherEventsVisible;
    private boolean motherEventsVisible;
    private boolean maleEventsVisible;
    private boolean femaleEventsVisible;

    private final int spouseLineColor = Color.RED;
    private final int familyLineColor = Color.YELLOW;
    private final int lifeLineColor = Color.GREEN;

    private DataCache() {
        persons = new ArrayList<>();
        events = new ArrayList<>();
        visibleEvents = new ArrayList<>();
        lifeStoryLinesVisible = true;
        familyTreeLinesVisible = true;
        spouseLinesVisible = true;
        fatherEventsVisible = true;
        motherEventsVisible = true;
        maleEventsVisible = true;
        femaleEventsVisible = true;
    }

    public void filter() {
    }


    public List<Event> findPossibleEvents(String text) {
        text = text.toLowerCase();
        List<Event> possibleEvents = new ArrayList<>();
        for (Event event : events) {
            if(event.getCity().toLowerCase().contains(text)) {
                possibleEvents.add(event);
            }
            else if(event.getCountry().toLowerCase().contains(text)) {
                possibleEvents.add(event);
            }
            else if(String.valueOf(event.getYear()).toLowerCase().contains(text)) {
                possibleEvents.add(event);
            }
            else if(event.getEventType().toLowerCase().contains(text)) {
                possibleEvents.add(event);
            }
        }
        return possibleEvents;
    }

    public List<Person> findPossiblePersons(String text) {
        text = text.toLowerCase();
        List<Person> possiblePersons = new ArrayList<>();
        for (Person person : persons) {
            Person debug = person;
            if(person.getfName().toLowerCase().contains(text)) {
                possiblePersons.add(person);
            }
            else if(person.getlName().toLowerCase().contains(text)) {
                possiblePersons.add(person);
            }
        }
        return possiblePersons;
    }

    public List<PersonRelations> getRelatives(String personID) {
        Person selectedPerson = getPerson(personID);
        List<PersonRelations> relatives = new ArrayList<>();
        for(Person person : persons) {
            if(person.getPersonID().equals(selectedPerson.getFatherID())) {
                PersonRelations temp = new PersonRelations();
                temp.person = getPerson(person.getPersonID());
                temp.relation = "Father";
                relatives.add(temp);
            }
            if(person.getPersonID().equals(selectedPerson.getMotherID())) {
                PersonRelations temp = new PersonRelations();
                temp.person = getPerson(person.getPersonID());
                temp.relation = "Mother";
                relatives.add(temp);
            }
            if(person.getPersonID().equals(selectedPerson.getSpouseID())) {
                PersonRelations temp = new PersonRelations();
                temp.person = getPerson(person.getPersonID());
                temp.relation = "Spouse";
                relatives.add(temp);
            }
            if(selectedPerson.getGender() == "m" && selectedPerson.getPersonID().equals(person.getFatherID())) {
                PersonRelations temp = new PersonRelations();
                temp.person = getPerson(person.getPersonID());
                temp.relation = "Child";
                relatives.add(temp);
            }
            if(selectedPerson.getGender() == "f" && selectedPerson.getPersonID().equals(person.getMotherID())) {
                PersonRelations temp = new PersonRelations();
                temp.person = getPerson(person.getPersonID());
                temp.relation = "Child";
                relatives.add(temp);
            }
        }
        return relatives;
    }

    public ArrayList<Event> getEventsByPersonID(String personID) {
        ArrayList<Event> personEvents = new ArrayList<>();
        for (int i = 0; i < visibleEvents.size(); i++) {
            if(visibleEvents.get(i).getPersonID().equals(personID)) {
                personEvents.add(visibleEvents.get(i));
            }
        }
        return personEvents;
    }

    public Event getEvent(String eventID) {
        for (int i = 0; i < visibleEvents.size(); i++) {
            Event currEvent = visibleEvents.get(i);
            if(currEvent.getEventID().equals(eventID)) {
                return currEvent;
            }
        }
        return null;
    }

    public Person getPerson(String personID) {
        for (int i = 0; i < persons.size(); i++) {
            Person currPerson = persons.get(i);
            if(currPerson.getPersonID().equals(personID)) {
                return currPerson;
            }
        }
        return null;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public static DataCache getInstance() {
        return _instance;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public void setRegisterResult(RegisterResult result) {
        this.registerResult = result;
        personID = result.getPersonID();
        username = result.getUsername();
        authID = result.getAuthtoken();
    }

    public void setLoginResult(LoginResult result) {
        this.loginResult = result;
        personID = result.getPersonID();
        username = result.getUsername();
        authID = result.getAuthtoken();
    }

    public void setEventsResultLogin(AllEventsResult result) {
        this.eventsResultLogin = result;
        events.addAll(Arrays.asList(result.getData()));
    }

    public void setPersonsResultLogin(AllPersonResult result) {
        this.personsResultLogin = result;
        persons.addAll(Arrays.asList(result.getData()));
        for (Person person : persons) {
            personMap.put(person.getPersonID(), person);
        }
        addParents(getPerson(getPerson(personID).getFatherID()), true);
        addParents(getPerson(getPerson(personID).getMotherID()), false);
        motherFemales.add(getPerson(getPerson(personID).getMotherID()));
        fatherMales.add(getPerson(getPerson(personID).getFatherID()));
    }

    private void addParents(Person person, boolean isFatherSide) {
        if(person.getFatherID()!=null) {
            if(isFatherSide) {
                fatherMales.add(personMap.get(person.getFatherID()));
            }
            else {
                motherMales.add(personMap.get(person.getFatherID()));
            }
            addParents(personMap.get(person.getFatherID()), isFatherSide);
        }
        if(person.getMotherID()!=null) {
            if(isFatherSide) {
                fatherFemales.add(personMap.get(person.getMotherID()));
            }
            else {
                motherFemales.add(personMap.get(person.getMotherID()));
            }
            addParents(personMap.get(person.getMotherID()), isFatherSide);
        }
    }

    public void setEventsResultRegister(AllEventsResult result) {
        this.eventsResultRegister = result;
    }

    public void setPersonsResultRegister(AllPersonResult result) {
        this.personsResultRegister = result;
    }

    public boolean isLifeStoryLinesVisible() {
        return lifeStoryLinesVisible;
    }

    public void setLifeStoryLinesVisible(boolean lifeStoryLinesVisible) {
        this.lifeStoryLinesVisible = lifeStoryLinesVisible;
    }

    public boolean isFamilyTreeLinesVisible() {
        return familyTreeLinesVisible;
    }

    public void setFamilyTreeLinesVisible(boolean familyTreeLinesVisible) {
        this.familyTreeLinesVisible = familyTreeLinesVisible;
    }

    public boolean isSpouseLinesVisible() {
        return spouseLinesVisible;
    }

    public void setSpouseLinesVisible(boolean spouseLinesVisible) {
        this.spouseLinesVisible = spouseLinesVisible;
    }

    public boolean isFatherEventsVisible() {
        return fatherEventsVisible;
    }

    public void setFatherEventsVisible(boolean fatherEventsVisible) {
        this.fatherEventsVisible = fatherEventsVisible;
    }

    public boolean isMotherEventsVisible() {
        return motherEventsVisible;
    }

    public void setMotherEventsVisible(boolean motherEventsVisible) {
        this.motherEventsVisible = motherEventsVisible;
    }

    public boolean isMaleEventsVisible() {
        return maleEventsVisible;
    }

    public void setMaleEventsVisible(boolean maleEventsVisible) {
        this.maleEventsVisible = maleEventsVisible;
    }

    public boolean isFemaleEventsVisible() {
        return femaleEventsVisible;
    }

    public void setFemaleEventsVisible(boolean femaleEventsVisible) {
        this.femaleEventsVisible = femaleEventsVisible;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public int getSpouseLineColor() {
        return spouseLineColor;
    }

    public int getFamilyLineColor() {
        return familyLineColor;
    }

    public int getLifeLineColor() {
        return lifeLineColor;
    }

    public ArrayList<Event> getVisibleEvents() {
        return visibleEvents;
    }

    public Event getEarliestEvent(Event event, String relation) {
        Person person = getPerson(event.getPersonID());
        String relationID;
        switch (relation) {
            case ("Spouse"):
                relationID = person.getSpouseID();
                break;
            case ("Father"):
                relationID = person.getFatherID();
                break;
            case ("Mother"):
                relationID = person.getMotherID();
                break;
            default:
                relationID = person.getPersonID();
        }

        Event relativeEvent = null;
        if(!relationID.equals("null"))
        {
            for(int i = 0; i < visibleEvents.size(); i++) {
                if (visibleEvents.get(i).getPersonID().equals(relationID)) {
                    if(relativeEvent != null && visibleEvents.get(i).getYear() < relativeEvent.getYear()) {
                        relativeEvent = visibleEvents.get(i);
                    }
                    else if (relativeEvent == null) {
                        relativeEvent = visibleEvents.get(i);
                    }
                }
            }
        }
        return relativeEvent;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public MainActivity getMain() {
        return main;
    }

    public void setMain(MainActivity main) {
        this.main = main;
    }

    public void logout() {
        _instance = null;
        _instance = new DataCache();
    }
}
