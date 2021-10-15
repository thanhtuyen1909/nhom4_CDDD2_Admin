package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.Employee1Adapter;
import vn.edu.tdc.cddd2.adapters.EmployeeAdapter;
import vn.edu.tdc.cddd2.data_models.Employee;

public class ListEmployeeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Employee> listEmployees;
    private NavigationView navigationView;
    private Employee1Adapter employeeAdapter;
    private Intent intent;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_employee);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách nhân viên");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện click button "+":
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ListEmployeeActivity.this, DetailEmployeeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listEmployee);
        recyclerView.setHasFixedSize(true);
        listEmployees = new ArrayList<>();
        data();
        employeeAdapter = new Employee1Adapter(listEmployees, this);
        employeeAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data() {
        listEmployees.add(new Employee("NV001", "Nguyễn Văn A", "Nhân viên bán hàng"));
        listEmployees.add(new Employee("QLK001", "Trương Thị Bình", "Quản ly kho"));
        listEmployees.add(new Employee("XL001", "La Văn Tiến", "Xử lý đơn hàng"));
        listEmployees.add(new Employee("GH001", "Nguyễn Bình", "Nhân viên giao hàng"));
        listEmployees.add(new Employee("QTV001", "Lê Danh", "Quản trị viên"));
    }

    private Employee1Adapter.ItemClickListener itemClickListener = new Employee1Adapter.ItemClickListener() {
        @Override
        public void getInfor(Employee item) {
            Toast.makeText(ListEmployeeActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlnv:
                break;
            case R.id.nav_dd:
                intent = new Intent(ListEmployeeActivity.this, AttendanceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListEmployeeActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
