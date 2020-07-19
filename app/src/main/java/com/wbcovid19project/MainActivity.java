package com.wbcovid19project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wbcovid19project.Adapter.GridAdapter;
import com.wbcovid19project.Adapter.MainAdapter;
import com.wbcovid19project.Login.LoginActivity;
import com.wbcovid19project.Login.ProfileActivity;
import com.wbcovid19project.Models.Jsons;
import com.wbcovid19project.Models.Users;
import com.wbcovid19project.OrphanageSupport.FreeFoodActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mUsersDatabase;
    private RecyclerView Grid_List;
    private FirebaseAuth mAuth;
    private ImageView Profile, Menu;

    String[] web = {
            "Corona Status",
            "Home Treatment",
            "Toll Numbers",
            "My Health Status",
            "Health Cares",
            "Medical stores",
            "Doctor Online Appointment",
            "Hospital Admissions",
            "Volunteers & Donors",
            "Food Supply",
            "Labs for Test",
            "Support Orphans & Vulnerable",
            "E-Pass",
            "Donate Funds",
            "Online Education",
            "Government Orders",
            "Tweets",
            "FAQs"

    } ;
    int[] imageId = {
            R.drawable.corona,
            R.drawable.home,
            R.drawable.toll_numbers,
            R.drawable.health,
            R.drawable.healthcare,
            R.drawable.medical_store,
            R.drawable.doctor,
            R.drawable.hospital,
            R.drawable.volunteer,
            R.drawable.food,
            R.drawable.lab,
            R.drawable.support,
            R.drawable.pass,
            R.drawable.donate,
            R.drawable.education,
            R.drawable.governmentorder,
            R.drawable.tweet,
            R.drawable.faq

    };

    private TextView Confirmed, Deceased, Tested, Recovered;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        Grid_List = findViewById(R.id.grid_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),3, LinearLayoutManager.VERTICAL,false);
        Grid_List.setLayoutManager(gridLayoutManager);

        mRequestQueue = Volley.newRequestQueue(MainActivity.this);

        Confirmed = findViewById(R.id.confirmed_count);
        Recovered = findViewById(R.id.cured_count);
        Tested = findViewById(R.id.tested_count);
        Deceased  = findViewById(R.id.deceased_count);

        MainAdapter mainAdapter = new MainAdapter(MainActivity.this, web, imageId);
        Grid_List.setAdapter(mainAdapter);

        parseJSON();

        Profile = findViewById(R.id.toolbar_profile);
        Menu = findViewById(R.id.toolbar_menu);
        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("access","true");
                startActivity(intent);
            }
        });



        Menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.logout:
                                FirebaseAuth.getInstance().signOut();
                                sendToLogin();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.menu_main);
                popupMenu.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();

        if (mCurrentUser == null) {

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }
        else{
            FirebaseAuth auth = FirebaseAuth.getInstance();
            mUsersDatabase = FirebaseDatabase.getInstance().getReference("Users").child(auth.getCurrentUser().getUid());
            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user.getVerified().equals("false")) {
                        Intent setupIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        setupIntent.putExtra("access","false");
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(setupIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    private void parseJSON() {
        JsonObjectRequest request = new JsonObjectRequest("https://api.covid19india.org/v3/data.json", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("WB").getJSONObject("total");
                            String confirmed = jsonObject.getString("confirmed");
                            String deceased = jsonObject.getString("deceased");
                            String tested = jsonObject.getString("tested");
                            String recovered = jsonObject.getString("recovered");

                            Confirmed.setText(confirmed);
                            Deceased.setText(deceased);
                            Tested.setText(tested);
                            Recovered.setText(recovered);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }
}
