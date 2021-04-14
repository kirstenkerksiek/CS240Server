package com.example.family_map_client;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import DataManagement.DataCache;
import DataManagement.MarkerDetails;
import Model.Event;
import Model.Person;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    public GoogleMap googleMap;
    public TextView fullName;
    public TextView eventDetails;
    public ImageView gender;
    private LinearLayout layout;
    private ArrayList<Polyline> lines = new ArrayList<Polyline>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public void onMapReady(GoogleMap googleMap) {
        DataCache cache = DataCache.getInstance();
        this.googleMap = googleMap;
        googleMap.clear();
        cache.filter();
        placeMarkers();
        setMarkerListener();
        setEventBarListener();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        fullName = (TextView) v.findViewById(R.id.person_full_name);
        eventDetails = (TextView) v.findViewById(R.id.event_details);
        gender = (ImageView) v.findViewById(R.id.gender_icon);
        layout = (LinearLayout) v.findViewById(R.id.event_bar);
        layout.setTag("");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //toolbar

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setEventBarListener()
    {
        layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String temp = (String) layout.getTag();

                if(!temp.equals("")) {
                    Intent intent = new Intent(getActivity(), PersonActivity.class);
                    String personID = (String) layout.getTag();
                    intent.putExtra("PERSON_ID", personID);
                    startActivity(intent);
                }
            }
        });
    }

    private void setMarkerListener() {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(Polyline line : lines) {
                    line.remove();
                }
                lines.clear();

                MarkerDetails details = (MarkerDetails) marker.getTag();
                Event event = details.event;
                Person person = details.person;
                layout.setTag(person.getPersonID());
                LatLng location = new LatLng(details.event.getLatitude(),details.event.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

                fullName.setText(person.getfName() + " " + person.getlName());
                eventDetails.setText(event.getEventType().toUpperCase() + "\n" + event.getCity() + ", " + event.getCountry() +
                        " (" + event.getYear() + ")");
                if(person.getGender().equals("m")) {
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_male);
                    gender.setImageDrawable(drawable);
                }
                else {
                    Drawable drawable = getResources().getDrawable(R.mipmap.ic_female);
                    gender.setImageDrawable(drawable);
                }

                DataCache cache = DataCache.getInstance();
                if(cache.isSpouseLinesVisible()) {
                    drawSpouseLine(event);
                }
                if(cache.isFamilyTreeLinesVisible()) {
                    drawAncestorLines(event, 5);
                }
                if(cache.isLifeStoryLinesVisible()) {
                    drawLifeEvents(event, 5);
                }
                return true;
            }
        });

    }

    private void placeMarkers() {
        boolean birthEventFound = false;
        Event birthEvent = null;
        DataCache cache = DataCache.getInstance();
        Map<String, Integer> markerColors = new HashMap<>();
        ArrayList<String> encounteredTypes = new ArrayList<>();
        int index = 0;
        int currColor;
        int numMarkers = 0;

        for(int i = 0; i < cache.getVisibleEvents().size(); i++) {
            Event event = cache.getVisibleEvents().get(i);
            Person person = cache.getPerson(cache.getVisibleEvents().get(i).getPersonID());
            MarkerDetails details = new MarkerDetails(person,event);
            LatLng location = new LatLng(cache.getVisibleEvents().get(i).getLatitude(),
                    cache.getVisibleEvents().get(i).getLongitude());

            String eventType = event.getEventType().toLowerCase();
            if(!encounteredTypes.contains(eventType)) {
                if (index == 10) {
                    index = 0;
                }
                encounteredTypes.add(eventType);
                markerColors.put(eventType, index);
                currColor = index;
                index++;
            }
            else {
                currColor = markerColors.get(eventType);
            }


            switch(currColor) {
                case 0:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 1:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 2:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 3:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 4:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 5:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 6:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 7:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 8:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
                            .setTag(details);
                    numMarkers++;
                    break;
                case 9:
                    googleMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(eventType)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)))
                            .setTag(details);
                    numMarkers++;
                    break;
            }

            //centers the camera on the birthplace of the user
            if(!birthEventFound) {
                if (cache.getVisibleEvents().get(i).getPersonID().equals(cache.getPersonID())) {
                    birthEvent = cache.getVisibleEvents().get(i);
                    birthEventFound = true;
                }
            }
        }
        System.out.println(numMarkers);
        Intent intent = getActivity().getIntent();
        String eventID = intent.getStringExtra("EVENT_ID");
        Event currEvent = cache.getEvent(eventID);
        if(birthEvent != null) {
            LatLng location = new LatLng(birthEvent.getLatitude(), birthEvent.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
        if(currEvent != null) {
            LatLng latlon = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlon));
            Person currPerson = cache.getPerson(currEvent.getPersonID());
            layout.setTag(currPerson.getPersonID());
            fullName.setText(currPerson.getfName() + " " + currPerson.getlName());
            eventDetails.setText(currEvent.getEventType() + "\n" + currEvent.getCity() + ", "
                    + currEvent.getCountry() + " (" + currEvent.getYear() + ")");

            Drawable drawable;
            if (currPerson.getGender().equals("m")) {
                drawable = getResources().getDrawable(R.mipmap.ic_male);
            }
            else {
                drawable = getResources().getDrawable(R.mipmap.ic_female);
            }
            gender.setImageDrawable(drawable);
        }

    }


    private void drawSpouseLine(Event event)
    {
        DataCache cache = DataCache.getInstance();
        LatLng latlon = new LatLng(event.getLatitude(),event.getLongitude());
        Event spouseEvent = cache.getEarliestEvent(event, "Spouse");
        if (spouseEvent != null) {
            LatLng latlonSpouse = new LatLng(spouseEvent.getLatitude(),spouseEvent.getLongitude());
            drawLine(latlon, latlonSpouse, cache.getSpouseLineColor(), 5);
        }
    }

    private void drawAncestorLines(Event event, int thickness)
    {
        drawParentsLine(event, thickness);
        DataCache cache = DataCache.getInstance();
        Person currPerson = cache.getPerson(event.getPersonID());
        if(currPerson.getFatherID() != null) {
            Event fatherEvent = cache.getEarliestEvent(event,"Father");
            int width = thickness - 1;
            if(width < 1) {
                width = 1;
            }
            drawAncestorLines(fatherEvent, width);
        }
        if(currPerson.getMotherID() != null) {
            Event motherEvent = cache.getEarliestEvent(event,"Mother");
            int width = thickness - 1;
            if(width < 1) {
                width = 1;
            }
            drawAncestorLines(motherEvent, width);
        }
    }

    private void drawParentsLine(Event event, int thickness)
    {
        DataCache cache = DataCache.getInstance();
        LatLng latlon = new LatLng(event.getLatitude(),event.getLongitude());
        Event fatherEvent = cache.getEarliestEvent(event,"Father");
        if (fatherEvent != null)
        {
            LatLng latlonFather = new LatLng(fatherEvent.getLatitude(),fatherEvent.getLongitude());
            drawLine(latlon, latlonFather, cache.getFamilyLineColor(), thickness);
        }
        Event motherEvent = cache.getEarliestEvent(event,"Mother");
        if (motherEvent != null)
        {
            LatLng motherLocation = new LatLng(motherEvent.getLatitude(),motherEvent.getLongitude());
            drawLine(latlon, motherLocation, cache.getFamilyLineColor(), thickness);
        }
    }

    private void drawLifeEvents(Event event, int thickness)
    {
        DataCache cache = DataCache.getInstance();
        ArrayList<Event> events = cache.getEventsByPersonID(event.getPersonID());
        ArrayList<LatLng> locations = new ArrayList<>();
        for(int i = 0; i < events.size(); i++) {
            locations.add(new LatLng(events.get(i).getLatitude(),events.get(i).getLongitude()));
        }
        LatLng prevLocation = locations.get(0);
        for(int i = 1; i < locations.size(); i++) {
            drawLine(prevLocation, locations.get(i), cache.getLifeLineColor(), thickness);
            prevLocation = locations.get(i);
        }

    }

    void drawLine(LatLng point1, LatLng point2, int color, int lineWidth)
    {
        PolylineOptions options = new PolylineOptions().add(point1, point2).color(color).width(lineWidth);
        lines.add(googleMap.addPolyline(options));
    }

}
