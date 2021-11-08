package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.EmployeeAdapter;
import vn.edu.tdc.cddd2.adapters.InvoiceAdapter;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.data_models.Invoice;

public class AttendanceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar,title,mess;
    private EditText edtĐiemDanh;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Employee> listEmployees;
    private NavigationView navigationView;
    private EmployeeAdapter employeeAdapter;
    private Intent intent;
    private DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();

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
        myRef.child("Employees").addListenerForSingleValueEvent(new ValueEventListener() {
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
            showSuccesDialog("Lý do vắng");
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
    private void showSuccesDialog(String message) {
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
        edtĐiemDanh=findViewById(R.id.edtDiemDanh);

        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
