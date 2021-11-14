package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.EmployeeAdapter;

import vn.edu.tdc.cddd2.data_models.Attendance;
import vn.edu.tdc.cddd2.data_models.Employee;


public class AttendanceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack,subtitleAppbar,title,mess;
    private EditText edtĐiemDanh,spinerDate;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Employee> listEmployees;
    private NavigationView navigationView;
    private EmployeeAdapter employeeAdapter;
    private Intent intent;
    private DatePickerDialog datePickerDialog;
    private SearchView searchView;
    private DatabaseReference myDB= FirebaseDatabase.getInstance().getReference();

    private DatabaseReference myRef= FirebaseDatabase.getInstance().getReference("Attendance");
    DatabaseReference empRef= FirebaseDatabase.getInstance().getReference("Employees");
    boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_employee);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Điểm danh");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        spinerDate=findViewById(R.id.spinner_date);
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String[] temp = currentDate.split("/");
        String key = temp[1]+"-"+temp[2];

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                check = true;
                myRef.child(key).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Attendance attendance = dataSnapshot.getValue(Attendance.class);
                        if(attendance.getDate().equals(currentDate)){
                            check = false;
                            Log.d("TAG", "onDataChange: "+check);
                        }

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        }},200);

        if(check){
            empRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot node : dataSnapshot.getChildren()){
                        Attendance attendance = new Attendance(currentDate,node.getKey(),"",0);
                        myRef.child(key).push().setValue(attendance);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        spinerDate.setText(currentDate);
        spinerDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        spinerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(AttendanceActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                String day=dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year;
                                spinerDate.setText(day);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listEmployee);
        recyclerView.setHasFixedSize(true);
        listEmployees = new ArrayList<>();
        data();
        employeeAdapter = new EmployeeAdapter(listEmployees, this);
        employeeAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(employeeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //search
        searchView=findViewById(R.id.editSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                employeeAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                employeeAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data() {
        //listEmployees.add(new Employee("NV001", "Nguyễn Văn A", "Nhân viên bán hàng"));
        //listEmployees.add(new Employee("QLK001", "Trương Thị Bình", "Quản ly kho"));
        //listEmployees.add(new Employee("XL001", "La Văn Tiến", "Xử lý đơn hàng"));
        //listEmployees.add(new Employee("GH001", "Nguyễn Bình", "Nhân viên giao hàng"));
        //listEmployees.add(new Employee("QTV001", "Lê Danh", "Quản trị viên"));
        myDB.child("Employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot DSEmployee:dataSnapshot.getChildren()){
                    Employee employee=DSEmployee.getValue(Employee.class);
                    employee.setMaNV(DSEmployee.getKey());
                    listEmployees.add(employee);
                }
                employeeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private EmployeeAdapter.ItemClickListener itemClickListener = new EmployeeAdapter.ItemClickListener() {
        @Override
        public void getInfor(Employee item) {
            //Toast.makeText(AttendanceActivity.this, "Điểm danh", Toast.LENGTH_SHORT).show();
            showSuccesDialog("Lý do vắng",item.getMaNV());

        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlnv:
                intent = new Intent(AttendanceActivity.this, ListEmployeeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dd:
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
    private void showSuccesDialog(String message,String maNV) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AttendanceActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(AttendanceActivity.this).inflate(
                R.layout.layout_diemdanh_dialog,
                findViewById(R.id.layoutDialogDiemDanh)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(message);
        edtĐiemDanh=view.findViewById(R.id.edtDiemDanh);

        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            Map<String, Object> map = new HashMap<>();
            myDB.child("Employees").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot DSEmployees:dataSnapshot.getChildren()){
                        Employee employee=DSEmployees.getValue(Employee.class);
                        employee.setMaNV(DSEmployees.getKey());
                        //lay ra ngay thang hien tai
                        String currentMoth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
                        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            if(employee.getMaNV().equals(maNV)) {
                                map.put("employeeID", employee.getMaNV());
                                map.put("note", edtĐiemDanh.getText().toString());
                                map.put("status","-1");
                                map.put("date",currentDate);
                                myDB.child("Attendance").child(currentMoth).push().setValue(map);
                            }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
