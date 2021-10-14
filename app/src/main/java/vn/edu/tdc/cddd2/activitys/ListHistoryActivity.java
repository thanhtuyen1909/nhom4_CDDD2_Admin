package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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

import com.google.android.material.navigation.NavigationView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_historyactivity);

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
        historyAdapter = new HistoryAdapter(this, R.layout.item_historyactivity, listHistory);
        listView.setAdapter(historyAdapter);
    }

    private void data() {
        listHistory.add(new History("13/10/2021", "Tivi", 1));
        listHistory.add(new History("13/10/2021", "Laptop", 5));
        listHistory.add(new History("13/10/2021", "Máy tính bảng", 10));
    }
}
