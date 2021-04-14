package com.example.family_map_client;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import DataManagement.DataCache;
import DataManagement.PersonRelations;
import Model.Event;
import Model.Person;

public class PersonActivity extends AppCompatActivity {
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);

        DataCache cache = DataCache.getInstance();

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);

        //generate Data!!
        String personID = getIntent().getStringExtra("PERSON_ID");
        Person person = cache.getPerson(personID);
        TextView first = findViewById(R.id.first_name_person);
        TextView last = findViewById(R.id.last_name_person);
        TextView gender = findViewById(R.id.gender_person);

        first.setText(person.getfName());
        last.setText(person.getlName());
        gender.setText(person.getGender());

        List<PersonRelations> family = cache.getRelatives(personID);
        List<Event> lifeEvents = cache.getEventsByPersonID(personID);
        List<Person> relatives = new ArrayList<>();
        List<String> relationship = new ArrayList<>();

        for (PersonRelations member : family) {
            relatives.add(member.person);
            relationship.add(member.relation);
        }

        expandableListView.setAdapter(new ExpandableListAdapter(lifeEvents, relatives, relationship));
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int LIFE_EVENT_GROUP_POSITION = 0;
        private static final int RELATIVE_GROUP_POSITION = 1;

        private final List<Event> lifeEvents;
        private final List<Person> relatives;
        private final List<String> relationship;

        ExpandableListAdapter(List<Event> lifeEvents, List<Person> relatives, List<String> relationship) {
            this.lifeEvents = lifeEvents;
            this.relatives = relatives;
            this.relationship = relationship;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    return lifeEvents.size();
                case RELATIVE_GROUP_POSITION:
                    return relatives.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            switch (groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    return "Events";
                case RELATIVE_GROUP_POSITION:
                    return "Family";
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    return lifeEvents.get(childPosition);
                case RELATIVE_GROUP_POSITION:
                    return relatives.get(childPosition);
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    titleView.setText("Events");
                    break;
                case RELATIVE_GROUP_POSITION:
                    titleView.setText("Family");
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case LIFE_EVENT_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.event_row, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case RELATIVE_GROUP_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.person_row, parent, false);
                    initializeRelativesView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(final View eventItemView, final int childPosition) {
            //event details
            //full name
            TextView details = eventItemView.findViewById(R.id.result_details);
            TextView fullName = eventItemView.findViewById(R.id.event_full_name);
            Event event = lifeEvents.get(childPosition);
            Person person = DataCache.getInstance().getPerson(event.getPersonID());
            fullName.setText(person.getfName() + " " + person.getlName());
            details.setText(event.getEventType() + " : " + event.getCity() + ", "
                    + event.getCountry() + " (" + event.getYear() + ")");

            eventItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataCache cache = DataCache.getInstance();
                    Intent eventIntent = new Intent(cache.getMain(), EventActivity.class);
                    eventIntent.putExtra("EVENT_ID", lifeEvents.get(childPosition).getEventID());
                    startActivity(eventIntent);
                }
            });
        }

        private void initializeRelativesView(View relativeItemView, final int childPosition) {

            TextView fullName = relativeItemView.findViewById(R.id.person_full_name);
            Person person = relatives.get(childPosition);
            fullName.setText(person.getfName() + " " + person.getlName());
            TextView relation = relativeItemView.findViewById(R.id.relationship);
            relation.setText(relationship.get(childPosition));
            ImageView icon = relativeItemView.findViewById(R.id.icon);

            Drawable drawable;
            if (person.getGender().equals("m")) {
                drawable = getResources().getDrawable(R.mipmap.ic_male);
            }
            else {
                drawable = getResources().getDrawable(R.mipmap.ic_female);
            }
            icon.setImageDrawable(drawable);

            relativeItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataCache cache = DataCache.getInstance();
                    Intent personIntent = new Intent(cache.getMain(), PersonActivity.class);
                    personIntent.putExtra("PERSON_ID", relatives.get(childPosition).getPersonID());
                    startActivity(personIntent);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}




