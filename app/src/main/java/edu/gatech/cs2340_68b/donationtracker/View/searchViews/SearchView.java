package edu.gatech.cs2340_68b.donationtracker.View.searchViews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.gatech.cs2340_68b.donationtracker.Controllers.Common.DataListAdapter;
import edu.gatech.cs2340_68b.donationtracker.Models.DonationDetail;
import edu.gatech.cs2340_68b.donationtracker.Models.Enum.Category;
import edu.gatech.cs2340_68b.donationtracker.Models.Enum.SearchOptions;
import edu.gatech.cs2340_68b.donationtracker.Models.Location;
import edu.gatech.cs2340_68b.donationtracker.Models.User;
import edu.gatech.cs2340_68b.donationtracker.Models.UserSearch;
import edu.gatech.cs2340_68b.donationtracker.R;
import edu.gatech.cs2340_68b.donationtracker.View.Welcome;
import edu.gatech.cs2340_68b.donationtracker.View.donationViews.DonationDetailControl;

public class SearchView extends AppCompatActivity {

    // Define Variables
    private User currentUser;
    private RadioGroup searchRadioGroup;
    private TextInputEditText searchBar;
    private Spinner searchCatSpinner;
    private Spinner searchLocSpinner;
    private ListView searchResultList;
    private int searchTypeFlag;
    private UserSearch searchCriteria;
    private ArrayList<String> locationListString;

    Location allLocations = new Location("All");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);

        // Initialize components
        searchRadioGroup = (RadioGroup) findViewById(R.id.searchTypeRadioGroup);
        RadioButton itemRButton = (RadioButton) findViewById(R.id.searchTypeItem);
        RadioButton catRButton = (RadioButton) findViewById(R.id.searchTypeCat);
        Button searchHistoryButton = (Button) findViewById(R.id.searchHistoryButton);
        searchBar = (TextInputEditText) findViewById(R.id.searchBar);
        searchCatSpinner = (Spinner) findViewById(R.id.searchCatSpinner);
        Button searchButton = (Button) findViewById(R.id.searchButton);
        searchLocSpinner = (Spinner) findViewById(R.id.searchLocSpinner);
        searchResultList = (ListView) findViewById(R.id.searchResultList);
        searchCriteria = new UserSearch();
        currentUser = Welcome.currentUser;
        locationListString = new ArrayList<>();

        // Initialize other variables
        searchTypeFlag = -1;
        boolean isSearchAll = true;
        itemRButton.setChecked(true);
        searchCriteria.setSearchOption(SearchOptions.NAME);

        // Radio Group Listener
        searchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View radioButton = searchRadioGroup.findViewById(checkedId);
                searchTypeFlag = searchRadioGroup.indexOfChild(radioButton);
                if (searchTypeFlag == 0) { // Item search
                    searchCriteria.setSearchOption(SearchOptions.NAME);
                    searchBar.setVisibility(View.VISIBLE);
                    searchCatSpinner.setVisibility(View.GONE);

                } else {                    // Category search
                    searchCriteria.setSearchOption(SearchOptions.CATEGORY);
                    searchBar.setVisibility(View.GONE);
                    searchCatSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        /*
         * CATEGORY SET UP
         */
        // Set up category spinner adapter
        ArrayAdapter<String> catAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Category.values());
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchCatSpinner.setAdapter(catAdapter);

        /*
         * LOCATION SET UP
         */

        // Read in location
        DatabaseReference locationDB = FirebaseDatabase.getInstance().getReference("locations");
        final ArrayList<Location> locationList = new ArrayList<>();
        final Context self = this;
        // Get values from locations
        locationDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Map.Entry<String, String>> locationInfo = new ArrayList<>();

                // Loads in all locations into the array list
                locationList.add(allLocations);
                locationListString.add("All");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // Create local copy of one location
                    Location place = snapshot.getValue(Location.class);
                    locationList.add(place);
                    locationListString.add(place.getLocationName());
                    Map.Entry<String, String> entry =
                            new AbstractMap.SimpleEntry<>(place.getLocationName(), place.getAddress());
                    locationInfo.add(entry);

                    // Set up adapter for location spinner
                    ArrayAdapter locationAdapter = new ArrayAdapter(self, android.R.layout.simple_spinner_item, locationListString);
                    locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    searchLocSpinner.setAdapter(locationAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Search History Button Listener
        searchHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchHistory = new Intent(SearchView.this, SearchHistory.class);
                startActivityForResult(searchHistory, 1);
            }
        });

        // Search Button Listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(0);
            }
        });
    }

    protected void search(final int removedIndex) {
        if (searchCriteria.getSearchOption().equals(SearchOptions.NAME)) {
            searchCriteria.setKeyword(searchBar.getText().toString());
        } else {
            searchCriteria.setKeyword(searchCatSpinner.getSelectedItem().toString());
        }
        searchCriteria.setLocationName(searchLocSpinner.getSelectedItem().toString());

        // Update search history to firebase
        final DatabaseReference userDB = FirebaseDatabase.getInstance()
                .getReference("accounts").child(Welcome.userKey);
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
                ArrayList<UserSearch> temp = currentUser.getUserSearchList();
                if (temp == null) {
                    temp = new ArrayList<>();

                    // Here to set the limit of the size of search history
                }
                temp.add(searchCriteria);
                if (temp.size() > 6) {
                    temp.remove(removedIndex);
                }
                userDB.child("userSearchList").setValue(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Query search result
        DatabaseReference donationDB = FirebaseDatabase.getInstance().getReference("donations");
        Query donationQuery = null;
        if (searchCriteria.getSearchOption().equals(SearchOptions.NAME)) {
            donationQuery = donationDB.orderByChild("name").equalTo(searchCriteria.getKeyword());
        } else {
            donationQuery = donationDB.orderByChild("category").equalTo(searchCriteria.getKeyword());
        }

        // Get data from firebase according to our query
        donationQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<Map.Entry<String, String>> donationInfo = new ArrayList<>();
                final ArrayList<DonationDetail> donationList = new ArrayList<>();
                final ArrayList<String> keyHashFromFB = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DonationDetail detail = snapshot.getValue(DonationDetail.class);
                    keyHashFromFB.add(snapshot.getKey());

                    // Check for location requirement.
                    if (searchCriteria.getLocationName().equals("All")
                            || searchCriteria.getLocationName().equals(detail.getLocation())) {
                        donationList.add(detail);
                        Map.Entry<String, String> entry =
                                new AbstractMap.SimpleEntry<>(detail.getName(), detail.getLocation());
                        donationInfo.add(entry);
                    }
                }
                searchResultList.setAdapter(new DataListAdapter(donationInfo, getLayoutInflater()));
                if (donationInfo.size() == 0) {
                    (findViewById(R.id.noItemTextView)).setVisibility(View.VISIBLE);
                    return;
                }
                else {
                    (findViewById(R.id.noItemTextView)).setVisibility(View.GONE);
                }
                searchResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Sending information through intent
                    String keyUsed = keyHashFromFB.get(position);
                    DonationDetail donation = donationList.get(position);
                    Intent detail = new Intent(SearchView.this, DonationDetailControl.class);
                    detail.putExtra("DATA", donation);
                    detail.putExtra("KEY", keyUsed);
                    detail.putExtra("LOCATION", searchCriteria.getLocationName());
                    startActivity(detail);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                UserSearch search = (UserSearch) data.getSerializableExtra("SEARCH");
                int removedIndex = data.getIntExtra("REMOVEDINDEX", 0);
                if (search.getSearchOption().equals(SearchOptions.NAME)) {
                    searchBar.setText(search.getKeyword());
                    searchRadioGroup.check(R.id.searchTypeItem);
                } else {
                    List<Category> catList = Arrays.asList(Category.values());
                    searchCatSpinner.setSelection(catList.indexOf(Category.valueOf(search.getKeyword())));
                    searchRadioGroup.check(R.id.searchTypeCat);
                }
                searchLocSpinner.setSelection(locationListString.indexOf(search.getLocationName()));
                search(removedIndex);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}