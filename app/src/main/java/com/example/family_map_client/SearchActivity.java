package com.example.family_map_client;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import DataManagement.DataCache;
import Model.Event;
import Model.Person;

public class SearchActivity extends AppCompatActivity {

    private static final int PERSON_ITEM_VIEW_TYPE = 0;
    private static final int EVENT_ITEM_VIEW_TYPE = 1;
    private String query = "";
    private ArrayList<Person> personResults = new ArrayList<>();
    private ArrayList<Event> eventResults = new ArrayList<>();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        ImageView button = findViewById(R.id.searchButton);
        button.setClickable(true);
        final EditText searchText = findViewById(R.id.search_box);
        final RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = searchText.getText().toString();
                DataCache cache = DataCache.getInstance();
                personResults = (ArrayList<Person>) cache.findPossiblePersons(query);
                eventResults = (ArrayList<Event>) cache.findPossibleEvents(query);
                RecyclerAdapter adapter = new RecyclerAdapter(personResults, eventResults);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    private class RecyclerAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final ArrayList<Person> personResults;
        private final ArrayList<Event> eventResults;

        RecyclerAdapter(ArrayList<Person> personResults, ArrayList<Event> eventResults) {
            this.personResults = personResults;
            this.eventResults = eventResults;
        }

        @Override
        public int getItemViewType(int position) {
            return position < personResults.size() ? PERSON_ITEM_VIEW_TYPE : EVENT_ITEM_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                view = getLayoutInflater().inflate(R.layout.person_row, parent, false);
            } else {
                view = getLayoutInflater().inflate(R.layout.event_row, parent, false);
            }

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < personResults.size()) {
                holder.bind(personResults.get(position));
            } else {
                holder.bind(eventResults.get(position - personResults.size()));
            }
        }

        @Override
        public int getItemCount() {
            return personResults.size() + eventResults.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView fullName;
        TextView details;

        private final int viewType;
        private Person person;
        private Event event;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                icon = itemView.findViewById(R.id.icon);
                fullName = itemView.findViewById(R.id.person_full_name);
                details = itemView.findViewById(R.id.relationship);
            } else {
                fullName = itemView.findViewById(R.id.event_full_name);
                details = itemView.findViewById(R.id.result_details);
            }
        }

        private void bind(Person person) {
            this.person = person;
            fullName.setText((person.getfName() + " " + person.getlName()));
            if (person.getGender().equals("m")) {
                icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_male));
            } else {
                icon.setImageDrawable(getResources().getDrawable(R.mipmap.ic_female));
            }
        }

        private void bind(Event event) {
            this.event = event;
            DataCache cache = DataCache.getInstance();
            Person person = cache.getPerson(event.getPersonID());
            fullName.setText(person.getfName() + " " + person.getlName());
            details.setText(event.getEventType() + " : " + event.getCity() + ", "
                    + event.getCountry() + " (" + event.getYear() + ")");
        }

        @Override
        public void onClick(View view) {
            if (viewType == PERSON_ITEM_VIEW_TYPE) {
                DataCache cache = DataCache.getInstance();
                Intent personIntent = new Intent(cache.getMain(), PersonActivity.class);
                personIntent.putExtra("PERSON_ID", person.getPersonID());
                startActivity(personIntent);
            } else {
                DataCache cache = DataCache.getInstance();
                Intent eventIntent = new Intent(cache.getMain(), EventActivity.class);
                eventIntent.putExtra("EVENT_ID", event.getEventID());
                startActivity(eventIntent);
            }
        }
    }
}

