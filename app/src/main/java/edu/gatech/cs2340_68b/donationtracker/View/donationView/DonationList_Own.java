package edu.gatech.cs2340_68b.donationtracker.View.donationView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import edu.gatech.cs2340_68b.donationtracker.Models.DonationDetail;
import edu.gatech.cs2340_68b.donationtracker.R;

public class DonationList_Own extends AppCompatActivity {

    private ListView donationListView;
    private Button addButton;
    private String newLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donation_list_own);
        final String locationName = (String) getIntent().getSerializableExtra("PLACENAME");
        final String defaultLocation = (String) getIntent().getSerializableExtra("DEFAULT");
        donationListView = findViewById(R.id.donationList);
        addButton = findViewById(R.id.add_button);

        if (locationName == null) {
            newLocation = defaultLocation;
        } else {
            newLocation = locationName;
        }
        DatabaseReference donationDB = FirebaseDatabase.getInstance().getReference("donations/");
        Query query = donationDB.orderByChild("location").equalTo(newLocation);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Map.Entry<String, String>> donationInfo = new ArrayList<>();
                final ArrayList<DonationDetail> donationList = new ArrayList<>();
                final ArrayList<String> keyHashFromFB = new ArrayList<>();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    DonationDetail detail = snapshot.getValue(DonationDetail.class);
                    keyHashFromFB.add(snapshot.getKey());
                    donationList.add(detail);
                    Map.Entry<String,String> entry =
                        new AbstractMap.SimpleEntry<>(detail.getName(), detail.getFullDescription());
                    donationInfo.add(entry);
                }
//                Collections.sort(donationInfo, new Comparator<Map.Entry<String, String>>() {
//                    @Override
//                    public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
//                        return o1.getKey().compareTo(o2.getKey());
//                    }
//                });
//                Collections.sort(donationList, new Comparator<DonationDetail>() {
//                    @Override
//                    public int compare(DonationDetail o1, DonationDetail o2) {
//                        return o1.getName().compareTo(o2.getName());
//                    }
//                });
                donationListView.setAdapter(new dataListAdapter(donationInfo));
                donationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // Sending information through intent
                        DonationDetail l = donationList.get(position);
                        String keyUsed = keyHashFromFB.get(position);
                        String array[] = new String[8];
                        array[0] = l.getName();
                        array[1] = l.getCategory();
                        array[2] = l.getComment();
                        array[3] = l.getFullDescription();
                        array[4] = l.getLocation();
                        array[5] = l.getShortDescription();
                        array[6] = l.getTime();
                        array[7] = l.getValue();
                        Intent detail = new Intent(DonationList_Own.this, DonationDetailControl.class);
                        detail.putExtra("DATA", array);
                        detail.putExtra("KEY", keyUsed);
                        detail.putExtra("LOCATION", locationName);
                        startActivity(detail);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}

            });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DonationList_Own.this, DonationDetailControl_New.class);
                intent.putExtra("LOCATION", newLocation);
                startActivity(intent);
            }
        });

    }

    class dataListAdapter extends BaseAdapter {
        //        String[] Title, Detail;
        ArrayList<Map.Entry<String, String>> data;
//        int[] imge;

        dataListAdapter() {
//            Title = null;
//            Detail = null;
//            imge=null;
            data = null;
        }

//        public dataListAdapter(String[] text, String[] text1) {
//            Title = text;
//            Detail = text1;
//            imge = text3;
//        }

        public dataListAdapter(ArrayList<Map.Entry<String, String>> data) {
            this.data = data;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.list_view_layout, null, true);
            TextView title, detail;
//            ImageView i1;
            title = (TextView) row.findViewById(R.id.title);
            detail = (TextView) row.findViewById(R.id.detail);
//            i1=(ImageView)row.findViewById(R.id.img);
//            title.setText(Title[position]);
//            detail.setText(Detail[position]);
//            i1.setImageResource(imge[position]);
            title.setText(data.get(position).getKey());
            detail.setText(data.get(position).getValue());
            return (row);
        }
    }
}