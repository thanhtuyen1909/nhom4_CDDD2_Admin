package vn.edu.tdc.cddd2.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.data_models.Payroll;

public class DetailSalaryActivity extends AppCompatActivity {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private TextView empID, empName, empPosition, empCreated_at, txtAbsent, empSalary, txtWorkDay, empAllowance, txtBonus, txtTotalSalary, txtFine;
    private Spinner spinMonth, spinYear;
    private String month, year;
    DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("Employees");
    DatabaseReference payrollRef = FirebaseDatabase.getInstance().getReference("Payroll");
    String id = "E1", key = "";
    int REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_salary);
        Bundle bundle = getIntent().getExtras();
        if(bundle!= null){
            id = bundle.getString("key");
        }
        requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        UIinit();
        setEvent();

    }

    private void setEvent() {
        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = (String) spinMonth.getItemAtPosition(position);
                month = month.split(" ")[1];
                if (month.length() == 1) {
                    month = "0" + month;
                }
                data();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = (String) spinYear.getItemAtPosition(position);
                data();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void UIinit() {
        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Chi tiết lương");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        empID = findViewById(R.id.txt_manv);
        empName = findViewById(R.id.txt_name);
        empPosition = findViewById(R.id.txt_position);
        empCreated_at = findViewById(R.id.txt_created);
        spinMonth = findViewById(R.id.spinner_month);
        spinYear = findViewById(R.id.spinner_year);
        txtWorkDay = findViewById(R.id.txt_songaylam);
        txtAbsent = findViewById(R.id.txt_songaynghi);
        empSalary = findViewById(R.id.txt_luongcb);
        txtFine = findViewById(R.id.txt_fine);
        txtBonus = findViewById(R.id.txt_chuyencan);
        empAllowance = findViewById(R.id.txt_phucap);
        txtTotalSalary = findViewById(R.id.txt_totalsalary);

        ArrayList<String> years = new ArrayList<>();
        Date now = new Date();
        String[] list = now.toString().split(" ");
        int year = Integer.parseInt(list[list.length - 1]);
        Log.d("TAG", "UIinit: " + now.getMonth());
        for (int i = 2020; i <= year; i++) {
            years.add(i + "");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinYear.setAdapter(adapter);
    }

    private void makeControlInvisible() {
        txtWorkDay.setText("Không có dữ liệu");
        txtAbsent.setVisibility(View.INVISIBLE);
        empSalary.setVisibility(View.INVISIBLE);
        txtFine.setVisibility(View.INVISIBLE);
        txtBonus.setVisibility(View.INVISIBLE);
        empAllowance.setVisibility(View.INVISIBLE);
        txtTotalSalary.setVisibility(View.INVISIBLE);
    }

    private void makeControlVisible() {
        txtWorkDay.setVisibility(View.VISIBLE);
        txtAbsent.setVisibility(View.VISIBLE);
        empSalary.setVisibility(View.VISIBLE);
        txtFine.setVisibility(View.VISIBLE);
        txtBonus.setVisibility(View.VISIBLE);
        empAllowance.setVisibility(View.VISIBLE);
        txtTotalSalary.setVisibility(View.VISIBLE);
    }

    private void data() {
        empRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Employee employee = dataSnapshot.getValue(Employee.class);
                    empID.setText(id);
                    empName.setText("Tên nhân viên : " + employee.getName());
                    empPosition.setText("Chức vụ : " + employee.getPosition());
                    empCreated_at.setText("Ngày vào làm : " + employee.getCreated_at());

                    payrollRef.child(month + "-" + year).child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                makeControlVisible();
                                Payroll payroll = snapshot.getValue(Payroll.class);
                                txtAbsent.setText("Số ngày vắng : " + payroll.getAbsent() + " ngày");
                                txtWorkDay.setText("Số ngày làm : " + payroll.getWorkday() + " ngày");
                                empSalary.setText("Lương cơ bản : " + formatPrice(payroll.getSalary()));
                                txtFine.setText("Đóng phạt : " + formatPrice(payroll.getFine()));
                                txtBonus.setText("Thưởng chuyên cần : " + formatPrice(payroll.getBonus()));
                                empAllowance.setText("Phụ cấp : " + formatPrice(payroll.getAllowance()));
                                txtTotalSalary.setText("Tổng lương : " + formatPrice(payroll.getTotal()));
                            } else {
                                makeControlInvisible();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String formatPrice(int price) {
        String stmp = String.valueOf(price);
        int amount;
        amount = (int) (stmp.length() / 3);
        if (stmp.length() % 3 == 0)
            amount--;
        for (int i = 1; i <= amount; i++) {
            stmp = new StringBuilder(stmp).insert(stmp.length() - (i * 3) - (i - 1), ",").toString();
        }
        return stmp + " ₫";
    }



    private void requestPermission(String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(this, permission, requestCode);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                UIinit();
                setEvent();
            }
        }
    }
}
