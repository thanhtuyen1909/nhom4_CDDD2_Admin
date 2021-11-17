package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.ManageEmployeesAdapter;
import vn.edu.tdc.cddd2.data_models.Employees;
import vn.edu.tdc.cddd2.data_models.Role;
import vn.edu.tdc.cddd2.interfaceClick.ItemClickRefreshEmployees;

public class ListEmployeeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ItemClickRefreshEmployees, SearchView.OnQueryTextListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Intent intent;
    private Button btnManageEmployeesAdd;

    //newData
    private Spinner spManageEmployees;
    private SearchView searchManageEmployees;
    private TextView tvManageEmployeesRenderSalary, tvManageEmployeesTotal;
    private RecyclerView rcvManageEmployees;

    private ManageEmployeesAdapter employeesAdapter;
    private Query queryByManageEmployees, queryByRole;
    ArrayList<Employees> arrEmployees;
    ArrayList<Employees> arrEmployeesFilter;
    ArrayList<String> arrRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_employee);
        setControl();

        arrEmployees = new ArrayList<>();
        arrEmployeesFilter = new ArrayList<>();
        arrRole = new ArrayList<>();

        queryByManageEmployees = FirebaseDatabase.getInstance().getReference().
                child("Employees");

        queryByRole = FirebaseDatabase.getInstance().getReference().
                child("Role");

        setRcvManageEmployees();
        loadDataManageEmployees();

        loadDataByRole();
        //Filter by name or id
        searchManageEmployees.setOnQueryTextListener(this);

        //Click by spinner
        spManageEmployees.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                arrEmployeesFilter.clear();
                if (spManageEmployees.getSelectedItemPosition() == 0) {
                    arrEmployeesFilter.addAll(arrEmployees);
                } else {
                    for (int x = 0; x < arrEmployees.size(); x++) {
                        if (arrEmployees.get(x).getPosition()
                                .equals(spManageEmployees.getSelectedItem().toString())) {
                            arrEmployeesFilter.add(arrEmployees.get(x));
                        }
                    }

                }

                employeesAdapter.notifyDataSetChanged();
                setDataTotalNumber();
                searchManageEmployees.clearFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện click button "+":
        btnManageEmployeesAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ListEmployeeActivity.this, DetailEmployeeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

    }

    private void setDataTotalNumber() {
        tvManageEmployeesTotal.setText("Tìm được: " + arrEmployeesFilter.size() + "/" + arrEmployees.size() + " nhân viên");
    }

    private void setRcvManageEmployees() {
        rcvManageEmployees.setLayoutManager(new LinearLayoutManager(this));
        employeesAdapter = new ManageEmployeesAdapter(ListEmployeeActivity.this
                , arrEmployeesFilter, ListEmployeeActivity.this);
        rcvManageEmployees.setAdapter(employeesAdapter);
    }

    private void loadDataManageEmployees() {
        queryByManageEmployees.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Employees employees;
                    String keyEmployees, accountID, address, birthday, created_at, gender, image, name, position;
                    int allowance, salary;
                    for (DataSnapshot emp : snapshot.getChildren()) {
                        keyEmployees = emp.getKey();
                        accountID = emp.getValue(Employees.class).getAccountID();
                        address = emp.getValue(Employees.class).getAddress();
                        birthday = emp.getValue(Employees.class).getBirthday();
                        created_at = emp.getValue(Employees.class).getCreated_at();
                        gender = emp.getValue(Employees.class).getGender();
                        image = emp.getValue(Employees.class).getImage();
                        name = emp.getValue(Employees.class).getName();
                        position = emp.getValue(Employees.class).getPosition();
                        allowance = emp.getValue(Employees.class).getAllowance();
                        salary = emp.getValue(Employees.class).getSalary();
                        employees = new Employees(keyEmployees, accountID, address, birthday, created_at
                                , gender, image, name, position, allowance, salary);

                        arrEmployees.add(employees);
                        arrEmployeesFilter.add(employees);
                        employeesAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadDataSpinner() {
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(ListEmployeeActivity.this, android.R.layout.simple_spinner_item, arrRole);
        spManageEmployees.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void loadDataByRole() {
        queryByRole.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean processDone = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && !processDone) {
                    for (DataSnapshot ro : snapshot.getChildren()) {
                        arrRole.add(ro.getValue(Role.class).getName());
                        processDone = true;
                    }
                }

                if (processDone) {
                    //Remove customer
                    arrRole.remove(0);
                    arrRole.add(0, "Tất cả nhân viên");
                    loadDataSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onItemClickEmployees() {
        arrEmployeesFilter.clear();
        arrEmployees.clear();
        loadDataManageEmployees();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        s = s.toLowerCase(Locale.getDefault());
        arrEmployeesFilter.clear();
        if (s.length() == 0) {
            arrEmployeesFilter.addAll(arrEmployees);
        } else {
            for (int i = 0; i < arrEmployees.size(); i++) {
                if (arrEmployees.get(i).getName().toLowerCase(Locale.getDefault()).contains(s)
                        || arrEmployees.get(i).getKeyEmployees().toLowerCase(Locale.getDefault()).contains(s)) {
                    arrEmployeesFilter.add(arrEmployees.get(i));
                }
            }
        }
        employeesAdapter.notifyDataSetChanged();
        setDataTotalNumber();
        return false;
    }

    private void setControl() {
        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách nhân viên");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnManageEmployeesAdd = findViewById(R.id.btnManageEmployeesAdd);
        tvManageEmployeesRenderSalary = findViewById(R.id.tvManageEmployeesRenderSalary);
        searchManageEmployees = findViewById(R.id.searchManageEmployees);
        rcvManageEmployees = findViewById(R.id.rcvManageEmployees);
        spManageEmployees = findViewById(R.id.spManageEmployees);
        tvManageEmployeesTotal = findViewById(R.id.tvManageEmployeesTotal);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

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
