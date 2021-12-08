package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.HistoryAdapter;
import vn.edu.tdc.cddd2.data_models.AccountHistory;

public class ListHistoryActivity extends AppCompatActivity {
    // Khai báo biến
    TextView btnBack;
    private ArrayList<AccountHistory> listHistory;
    private HistoryAdapter historyAdapter;
    Intent intent;
    ListView listView;
    String key = "";

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("AccountHistory");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_historyactivity);

        intent = getIntent();
        key = intent.getStringExtra("key");

        // Khởi tạo biến
        btnBack = findViewById(R.id.btnBack);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> { finish(); });

        //ListView
        listView = findViewById(R.id.listHistory);
        listHistory = new ArrayList<>();
        data();
        historyAdapter = new HistoryAdapter(this, R.layout.item_historyactivity, listHistory);
        listView.setAdapter(historyAdapter);
    }

    private void data() {
        ref.orderByChild("date").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listHistory.clear();
                for(DataSnapshot node : snapshot.getChildren()) {
                    AccountHistory history = node.getValue(AccountHistory.class);
                    if(history.getAccountID().equals(key)) {
                        listHistory.add(history);
                    }
                }
                Collections.reverse(listHistory);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
