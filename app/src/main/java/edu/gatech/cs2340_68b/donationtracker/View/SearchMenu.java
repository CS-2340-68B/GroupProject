package edu.gatech.cs2340_68b.donationtracker.View;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import edu.gatech.cs2340_68b.donationtracker.Models.Location;
import edu.gatech.cs2340_68b.donationtracker.R;
import edu.gatech.cs2340_68b.donationtracker.View.locationView.LocationForSearch;

public class SearchMenu extends AppCompatActivity {
    private TextInputEditText searchbar;
    private RadioGroup searchRadioGroup;
    private RadioButton searchItemAllButton;
    private RadioButton searchCatAllButton;
    private RadioButton searchItemLocButton;
    private RadioButton searchCatLocButton;
    private int searchTypeFlag;
    private Location locationSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_menu);

        searchbar = (TextInputEditText) findViewById(R.id.searchbar);
        searchRadioGroup = (RadioGroup) findViewById(R.id.searchRadioGroup);
        searchTypeFlag = -1;
//
        searchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = searchRadioGroup.findViewById(checkedId);
                int index = searchRadioGroup.indexOfChild(radioButton);
                locationSearch = null;
                String searchString = searchbar.getText().toString();
                switch (index) {
                    case 0: // searchItemAll
                        searchTypeFlag = 0;
                        Intent intent1 = new Intent(SearchMenu.this, SearchResult.class);
                        intent1.putExtra("SearchFlag", searchTypeFlag);
                        intent1.putExtra("LOCATIONSEARCH", locationSearch); // should pass null
                        intent1.putExtra("SearchString", searchString);
                        startActivity(intent1);
                        break;
                    case 1: // searchCatAll
                        searchTypeFlag = 1;
                        Intent intent2 = new Intent(SearchMenu.this, SearchResult.class);
                        intent2.putExtra("SearchFlag", searchTypeFlag);
                        intent2.putExtra("LOCATIONSEARCH", locationSearch); // should pass null
                        intent2.putExtra("SearchString", searchString);
                        startActivity(intent2);
                        break;
                    case 2: // searchItemLoc
                        searchTypeFlag = 2;
                        // Location List --> Come back with info?
                        Intent intent3 = new Intent(SearchMenu.this, LocationForSearch.class);
                        intent3.putExtra("SearchFlag", searchTypeFlag);
                        locationSearch = (Location) getIntent().getSerializableExtra("LOCATION");
                        intent3.putExtra("LOCATIONSEARCH", locationSearch);
                        intent3.putExtra("SearchString", searchString);
                        startActivity(intent3);
                        break;
                    case 3: // searchCatLoc
                        searchTypeFlag = 3;
                        // Location List --> Come back with info?
                        Intent intent4 = new Intent(SearchMenu.this, LocationForSearch.class);
                        intent4.putExtra("SearchFlag", searchTypeFlag);
                        locationSearch = (Location) getIntent().getSerializableExtra("LOCATION");
                        intent4.putExtra("LOCATIONSEARCH", locationSearch);
                        intent4.putExtra("SearchString", searchString);
                        startActivity(intent4);
                        break;
                }
            }
        });
    }
}
