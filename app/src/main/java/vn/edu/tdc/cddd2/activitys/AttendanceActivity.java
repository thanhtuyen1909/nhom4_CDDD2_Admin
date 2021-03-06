package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.AttendanceAdapter;
import vn.edu.tdc.cddd2.data_models.Attendance;
import vn.edu.tdc.cddd2.data_models.Employee;


public class AttendanceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, title, txtFilter;
    EditText edtĐiemDanh, spinerDate;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    private ArrayList<Attendance> listAttend;
    NavigationView navigationView;
    private AttendanceAdapter adapter;
    Intent intent;
    private DatePickerDialog datePickerDialog;
    SearchView searchView;

    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Attendance");
    DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("Employees");
    String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    ArrayList<Employee> listEmployee = new ArrayList<>();
    String accountID = "", username = "", name = "", role = "", img = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_attendance);
        getListEmployee();

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        txtFilter = findViewById(R.id.tvManageEmployeesTotal);
        subtitleAppbar.setText("Điểm danh");


        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();


        spinerDate = findViewById(R.id.spManageEmployees);
        String[] temp = currentDate.split("/");
        String key = temp[1] + "-" + temp[2];
        Query query = myRef.child(key).orderByChild("date").equalTo(currentDate);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    empRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot node : dataSnapshot.getChildren()) {
                                Attendance attendance = new Attendance(currentDate, node.getKey(), "", 0);
                                myRef.child(key).push().setValue(attendance);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        spinerDate.setText(currentDate);

        spinerDate.setOnClickListener(v -> {
            // calender class's instance and get current date , month and year from calender
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            datePickerDialog = new DatePickerDialog(AttendanceActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // set day of month , month and year value in the edit text
                        String tempDay = "";
                        tempDay = dayOfMonth + "";
                        if (dayOfMonth < 10) {
                            tempDay = "0" + dayOfMonth;
                        }
                        String day = tempDay + "/"
                                + (monthOfYear + 1) + "/" + year;
                        spinerDate.setText(day);
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        });

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);

        // Ẩn button "Trở lại":
        btnBack.setVisibility(View.GONE);

        //RecycleView
        recyclerView = findViewById(R.id.rcvManageEmployees);
        recyclerView.setHasFixedSize(true);
        listAttend = new ArrayList<>();
        if (!spinerDate.getText().equals("")) {
            data(spinerDate.getText() + "");
        } else {
            data(currentDate);
        }
        adapter = new AttendanceAdapter(listAttend, this);
        adapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        TextView txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        ImageView iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);

        //search
        searchView = findViewById(R.id.searchManageEmployees);

        spinerDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                data(String.valueOf(s));
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                filter(String.valueOf(spinerDate.getText()), newText);
                return false;
            }
        });
    }


    private void filter(String date, String query) {
        if (!date.equals("")) {
            if (query.equals("")) {
                data(date);
            } else {
                String month = date.split("/")[1] + "-" + date.split("/")[2];
                myRef.child(month).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listAttend.clear();
                        ArrayList<Attendance> temp = new ArrayList<>();
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Attendance attendance = node.getValue(Attendance.class);
                            attendance.setKey(node.getKey());
                            if (attendance.getDate().equals(date)) {
                                temp.add(attendance);
                            }
                        }

                        for (Attendance attendance : temp) {
                            if (attendance.getEmployeeID().trim().toLowerCase().contains(query.trim().toLowerCase())) {
                                listAttend.add(attendance);
                                continue;
                            }

                            for (Employee emp : listEmployee) {
                                if (attendance.getEmployeeID().equals(emp.getId())) {
                                    if (emp.getName().trim().toLowerCase().contains(query.trim().toLowerCase())) {
                                        listAttend.add(attendance);
                                    }
                                }
                            }
                        }
                        txtFilter.setText(listAttend.size() + " trên " + listEmployee.size() + " nhân viên");
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void getListEmployee() {
        empRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Employee employee = node.getValue(Employee.class);
                    employee.setId(node.getKey());
                    listEmployee.add(employee);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data(String date) {
        String month = date.split("/")[1] + "-" + date.split("/")[2];
        myRef.child(month).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listAttend.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Attendance attendance = node.getValue(Attendance.class);
                    attendance.setKey(node.getKey());
                    if (attendance.getDate().equals(date)) {
                        listAttend.add(attendance);
                    }
                }
                txtFilter.setText(listAttend.size() + " trên " + listEmployee.size() + " nhân viên");
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private AttendanceAdapter.ItemClickListener itemClickListener = item -> showSuccesDialog("Lý do vắng", item);

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlnv:
                intent = new Intent(AttendanceActivity.this, ListEmployeeActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dd:
                break;
            case R.id.nav_dmk:
                intent = new Intent(AttendanceActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(AttendanceActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(AttendanceActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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

    private void showSuccesDialog(String message, Attendance item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(AttendanceActivity.this).inflate(
                R.layout.layout_diemdanh_dialog,
                findViewById(R.id.layoutDialogDiemDanh)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText("Lý do vắng");
        edtĐiemDanh = view.findViewById(R.id.edtDiemDanh);

        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            Map<String, Object> map = new HashMap<>();
            String currentMoth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            map.put("employeeID", item.getEmployeeID());
            map.put("note", edtĐiemDanh.getText().toString());
            map.put("status", -1);
            map.put("date", currentDate);
            myRef.child(currentMoth).child(item.getKey()).setValue(map);


        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
