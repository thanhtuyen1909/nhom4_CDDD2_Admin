package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.AccountAdapter;
import vn.edu.tdc.cddd2.adapters.HistoryAdapter;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.History;

public class ListHistoryActivity extends AppCompatActivity {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private Button btnAdd;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<History> listHistory;
    private NavigationView navigationView;
    private HistoryAdapter historyAdapter;
    private Intent intent;
    private ListView listView;
    private DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
    private String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_historyactivity);
        intent=this.getIntent();
        key=intent.getStringExtra("key");

        // Khởi tạo biến
        btnBack = findViewById(R.id.btnBack);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //ListView
        listView = findViewById(R.id.listHistory);
        listHistory = new ArrayList<>();
        data();
        historyAdapter =new HistoryAdapter(this,R.layout.item_historyactivity,listHistory);

        listView.setAdapter(historyAdapter);

    }

    private void data() {
       myRef.child("AccountHistory").addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot DSAccountH:dataSnapshot.getChildren()){
                   History history = DSAccountH.getValue(History.class);
                  if(history.getAccountID().equals(key)){
                      listHistory.add(history);
                      Log.d("aaaa", "onDataChange: "+listHistory.size());
                  }
               }

               historyAdapter.notifyDataSetChanged();
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }
}
