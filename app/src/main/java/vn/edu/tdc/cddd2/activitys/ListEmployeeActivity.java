package vn.edu.tdc.cddd2.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.ManageEmployeesAdapter;
import vn.edu.tdc.cddd2.adapters.ManuAdapter;
import vn.edu.tdc.cddd2.data_models.Attendance;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.data_models.Payroll;
import vn.edu.tdc.cddd2.data_models.Role;
import vn.edu.tdc.cddd2.interfaceClick.ItemClickRefreshEmployees;

public class ListEmployeeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ItemClickRefreshEmployees, SearchView.OnQueryTextListener {
    private static final int REQUEST_CODE = 1412;
    // Khai b??o bi???n
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
    ArrayList<Employee> arrEmployees;
    ArrayList<Employee> arrEmployeesFilter;
    ArrayList<String> arrRole;
    int count = 0, count1 = 0, firstIndex = 4;
    DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("Employees");
    DatabaseReference payrollRef = FirebaseDatabase.getInstance().getReference("Payroll");
    String accountID = "", username = "", name = "", role = "", img = "";
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_employee);

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);

        setControl();
        createPayroll();
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

        tvManageEmployeesRenderSalary.setOnClickListener(v -> showChooseMonthDialog());
        // X??? l?? s??? ki???n click button "+":
        btnManageEmployeesAdd.setOnClickListener(v -> {
            intent = new Intent(ListEmployeeActivity.this, DetailEmployeeActivity.class);
            intent.putExtra("type", "add");
            intent.putExtra("accountID", accountID);
            startActivity(intent);
        });

    }

    private final ManageEmployeesAdapter.ItemClickListener itemClickListener = (keyEmployees, accountID) -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEmployeeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListEmployeeActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        TextView mess = view.findViewById(R.id.textMessage);
        mess.setText("X??c nh???n xo?? nh??n vi??n?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            FirebaseDatabase.getInstance().getReference().child("Employees")
                    .child(keyEmployees).removeValue();
            //Remove data in account
            if (!accountID.equals("")) {
                FirebaseDatabase.getInstance().getReference().child("Account")
                        .child(accountID).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Ship_area").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            String empID = node.child("employeeID").getValue(String.class);
                            if (empID.equals(keyEmployees)) {
                                FirebaseDatabase.getInstance().getReference().child("Ship_area").child(node.getKey()).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            employeesAdapter.notifyDataSetChanged();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

    };

    private void showChooseMonthDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEmployeeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListEmployeeActivity.this).inflate(
                R.layout.dialog_export,
                findViewById(R.id.layoutDialog)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText("Ch???n th???i gian");
        Spinner spinMonth = view.findViewById(R.id.spinMonth);
        Spinner spinYear = view.findViewById(R.id.spinYear);
        ArrayList<String> listMonth = new ArrayList<>();
        ArrayList<String> listYear = new ArrayList<>();
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(ListEmployeeActivity.this, android.R.layout.simple_spinner_dropdown_item, listMonth);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(ListEmployeeActivity.this, android.R.layout.simple_spinner_dropdown_item, listYear);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMonth.setAdapter(monthAdapter);
        spinYear.setAdapter(yearAdapter);
        for (int i = 1; i <= 12; i++) {
            monthAdapter.add("Th??ng " + i);
        }
        monthAdapter.notifyDataSetChanged();
        for (int i = 2020; i <= 2021; i++) {
            yearAdapter.add("" + i);
        }
        yearAdapter.notifyDataSetChanged();
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            int index = spinMonth.getSelectedItemPosition() + 1;
            String month = "";
            if (index < 10) {
                month = "0" + index;
            } else {
                month = index + "";
            }
            month += "-" + spinYear.getSelectedItem();
            exportExcel(month);
            showSuccesDialog();
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void exportExcel(String month) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFSheet sheet = workbook.createSheet("B???ng l????ng th??ng 10");
        sheet.setColumnWidth(6, 4000);
        sheet.setColumnWidth(10, 4000);
        HSSFRow row = sheet.createRow(firstIndex);
        HSSFCell cell = row.createCell(firstIndex);
        HSSFRow newRow = sheet.createRow(firstIndex + 1);
        firstIndex++;
        HSSFCell cell0 = newRow.createCell(0);
        cell0.setCellValue("M?? nh??n vi??n");

        HSSFCell cell1 = newRow.createCell(1);
        cell1.setCellValue("T??n nh??n vi??n");

        HSSFCell cell2 = newRow.createCell(2);
        cell2.setCellValue("S??? ng??y l??m");

        HSSFCell cell3 = newRow.createCell(3);
        cell3.setCellValue("S??? ng??y ngh???");

        HSSFCell cell4 = newRow.createCell(4);
        cell4.setCellValue("C?? ph??p");

        HSSFCell cell5 = newRow.createCell(5);
        cell5.setCellValue("Kh??ng ph??p");

        HSSFCell cell6 = newRow.createCell(6);
        cell6.setCellValue("L????ng c?? b???n");

        HSSFCell cell7 = newRow.createCell(7);
        cell7.setCellValue("Ph??? c???p");

        HSSFCell cell8 = newRow.createCell(8);
        cell8.setCellValue("Ti???n ph???t");

        HSSFCell cell9 = newRow.createCell(9);
        cell9.setCellValue("Ti???n th?????ng");

        HSSFCell cell10 = newRow.createCell(10);
        cell10.setCellValue("T???ng l????ng");

        cell.setCellValue("Chi ti???t b???ng l????ng th??ng " + month);
        payrollRef.child(month).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    Payroll payroll = node.getValue(Payroll.class);
                    HSSFRow row1 = sheet.createRow(firstIndex + 1);
                    HSSFRow row3 = sheet.createRow(firstIndex + 2);
                    HSSFRow rowName = sheet.createRow(firstIndex + 3);
                    HSSFRow row2 = sheet.createRow(firstIndex + 4);
                    firstIndex++;
                    empRef.child(node.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Employee employee = snapshot.getValue(Employee.class);
                                HSSFCell cell0 = row1.createCell(0);
                                cell0.setCellValue(node.getKey());

                                HSSFCell cell1 = row1.createCell(1);
                                cell1.setCellValue(employee.getName());

                                HSSFCell cell2 = row1.createCell(2);
                                cell2.setCellValue(payroll.getWorkday());

                                HSSFCell cell3 = row1.createCell(3);
                                cell3.setCellValue(payroll.getAbsent());

                                HSSFCell cell4 = row1.createCell(4);
                                cell4.setCellValue(payroll.getAbsent() - payroll.getFine() / 50000);

                                HSSFCell cell5 = row1.createCell(5);
                                cell5.setCellValue(payroll.getFine() / 50000);

                                HSSFCell cell6 = row1.createCell(6);
                                cell6.setCellValue(payroll.getSalary());

                                HSSFCell cell7 = row1.createCell(7);
                                cell7.setCellValue(payroll.getAllowance());

                                HSSFCell cell8 = row1.createCell(8);
                                cell8.setCellValue(payroll.getFine());

                                HSSFCell cell9 = row1.createCell(9);
                                cell9.setCellValue(payroll.getBonus());

                                HSSFCell cell10 = row1.createCell(10);
                                cell10.setCellValue(payroll.getTotal());

                                HSSFCell temp1 = row3.createCell(4);
                                temp1.setCellValue("");
                                HSSFCell temp2 = row3.createCell(6);
                                temp2.setCellValue("");
                                HSSFCell temp3 = row3.createCell(8);
                                temp3.setCellValue("");


                                HSSFCell cellGD = rowName.createCell(4);
                                cellGD.setCellValue("Gi??m ?????c");
                                HSSFCell cellHR = rowName.createCell(6);
                                cellHR.setCellValue("HR");
                                HSSFCell cellKT = rowName.createCell(8);
                                cellKT.setCellValue("K??? to??n");

                                HSSFCell cellGDs = row2.createCell(4);
                                cellGDs.setCellValue("K?? v?? ghi r?? h??? t??n");
                                HSSFCell cellHRs = row2.createCell(6);
                                cellHRs.setCellValue("K?? v?? ghi r?? h??? t??n");
                                HSSFCell cellKTs = row2.createCell(8);
                                cellKTs.setCellValue("K?? v?? ghi r?? h??? t??n");


                                File filePath = new File(Environment.getExternalStorageDirectory() + "/B???ng l????ng th??ng " + month + ".xls");

                                try {
                                    if (!filePath.exists()) {
                                        filePath.createNewFile();
                                    }
                                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                                    workbook.write(fileOutputStream);
                                    if (fileOutputStream != null) {
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
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

    private void showSuccesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListEmployeeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListEmployeeActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialog)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText("Th??ng b??o");
        TextView message = view.findViewById(R.id.textMessage);
        message.setText("Xu???t b???ng l????ng th??nh c??ng!");

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

    private void setDataTotalNumber() {
        tvManageEmployeesTotal.setText("T??m ???????c: " + arrEmployeesFilter.size() + "/" + arrEmployees.size() + " nh??n vi??n");
    }

    private void setRcvManageEmployees() {
        rcvManageEmployees.setLayoutManager(new LinearLayoutManager(this));
        employeesAdapter = new ManageEmployeesAdapter(ListEmployeeActivity.this
                , arrEmployeesFilter);
        rcvManageEmployees.setAdapter(employeesAdapter);
        employeesAdapter.setItemClickListener(itemClickListener);
    }

    private void loadDataManageEmployees() {
        queryByManageEmployees.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot emp : snapshot.getChildren()) {
                        Employee employee = emp.getValue(Employee.class);
                        employee.setId(emp.getKey());
                        arrEmployees.add(employee);
                        arrEmployeesFilter.add(employee);
                    }
                    employeesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
//        int month = Integer.parseInt(currentMonth.split("-")[0])-1;
//        String lastMonth = month+"-"+currentMonth.split("-")[1];
//        DatabaseReference
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
                    arrRole.add(0, "T???t c??? nh??n vi??n");
                    loadDataSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createPayroll() {
        String currentMonth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());

        int month = Integer.parseInt(currentMonth.split("-")[0]);
        month--;
        String lastMonth = month + "-" + currentMonth.split("-")[1];
        payrollRef.child(currentMonth).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    DatabaseReference attendRef = FirebaseDatabase.getInstance().getReference("Attendance");
                    empRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot node : dataSnapshot.getChildren()) {

                                Employee employee = node.getValue(Employee.class);
                                attendRef.child(lastMonth).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        if (snapshot1.exists()) {
                                            HashMap<String, Object> map = new HashMap<>();
                                            count = 0;
                                            count1 = 0;
                                            for (DataSnapshot node1 : snapshot1.getChildren()) {
                                                Attendance attendance = node1.getValue(Attendance.class);
                                                if (attendance.getEmployeeID().equals(node.getKey())) {
                                                    if (attendance.getStatus() == -1) {
                                                        count++;
                                                        if (attendance.getNote().equals("")) {
                                                            count1++;
                                                        }
                                                    }
                                                }
                                            }
                                            map.put("absent", count);
                                            map.put("allowance", employee.getAllowance());
                                            map.put("salary", employee.getSalary());
                                            map.put("workday", 30 - count);
                                            int bonus = 0;
                                            if (30 - count >= 26 && count1 == 0) {
                                                map.put("bonus", 200000);
                                                bonus = 200000;
                                            } else {
                                                map.put("bonus", 0);
                                            }
                                            map.put("fine", 50000 * count1);
                                            map.put("total", employee.getSalary() + employee.getAllowance() + bonus - 50000 * count1);
                                            Log.d("TAG", map.toString());
                                            payrollRef.child(lastMonth).child(node.getKey()).setValue(map);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void requestPermission(String[] permission, int requestCode) {
        ActivityCompat.requestPermissions(this, permission, requestCode);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setControl();
                createPayroll();
            }
        }
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
                        || arrEmployees.get(i).getId().toLowerCase(Locale.getDefault()).contains(s)) {
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
        subtitleAppbar.setText("Danh s??ch nh??n vi??n");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        TextView txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        ImageView iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);

        // Kh???i t???o bi???n
        btnBack = findViewById(R.id.txtBack);

        // ???n button "Tr??? l???i":
        btnBack.setVisibility(View.GONE);
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
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListEmployeeActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListEmployeeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(ListEmployeeActivity.this, "Vui l??ng ch???n ch???c n??ng kh??c", Toast.LENGTH_SHORT).show();
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
