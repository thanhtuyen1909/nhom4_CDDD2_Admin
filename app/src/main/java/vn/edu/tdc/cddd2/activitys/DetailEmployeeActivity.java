package vn.edu.tdc.cddd2.activitys;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.AreaAdapter;
import vn.edu.tdc.cddd2.data_models.AccountHistory;
import vn.edu.tdc.cddd2.data_models.Area;
import vn.edu.tdc.cddd2.data_models.Employee;

public class DetailEmployeeActivity extends AppCompatActivity {
    // Khai b??o bi???n
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnXemBangLuong, btnCancel;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Intent intent;
    private ImageView empImage;
    private Spinner empPosition, empGender, empShipArea;
    private EditText empName, empID, empAddress, empSalary, empAllowance, empSeniority, empDOB, empCreated_at;
    AreaAdapter areaAdapter;
    ArrayList<Area> listArea;
    Uri imageUri;
    ProgressBar bar;
    String type = "add", date = "", accountID = "";
    Employee item;
    DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("Employees");
    DatabaseReference shipAreaRef = FirebaseDatabase.getInstance().getReference("Ship_area");
    DatabaseReference areaRef = FirebaseDatabase.getInstance().getReference("Area");

    static int SELECT_IMAGE_CODE = 1;
    boolean check = true,check1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_employee);
        accountID = getIntent().getStringExtra("accountID");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            type = bundle.getString("type");
            item = bundle.getParcelable("item");
        }
        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleTTNV);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Kh???i t???o bi???n
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        btnXemBangLuong = findViewById(R.id.btnXBL);
        empID = findViewById(R.id.edtMNV);
        if(type.equals("edit")){
            empID.setFocusable(false);
        }
        empPosition = findViewById(R.id.spinner_chucvu);
        empName = findViewById(R.id.edtTNV);
        empAddress = findViewById(R.id.edtDC);
        empDOB = findViewById(R.id.dtp_ngay_sinh);
        empGender = findViewById(R.id.spinner_gioitinh);
        empCreated_at = findViewById(R.id.dtp_ngayvao);
        empSeniority = findViewById(R.id.edtTGLV);
        empSeniority.setFocusable(false);
        empSalary = findViewById(R.id.edtLCB);
        empAllowance = findViewById(R.id.edtPC);
        empAllowance.setText(""+0);
        empAllowance.setFocusable(false);
        empImage = findViewById(R.id.imageView);
        empShipArea = findViewById(R.id.spinner_khuvucship);
        bar = findViewById(R.id.progess1);
        bar.setIndeterminateDrawable(new FoldingCube());
        listArea = new ArrayList<>();
        areaAdapter = new AreaAdapter(this, listArea);
        empShipArea.setAdapter(areaAdapter);

        data();
        empDOB.setOnClickListener(this::showDatePickerDialog);
        empCreated_at.setOnClickListener(this::showDatePickerDialog);
        empImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
        });

        empSalary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(String.valueOf(s).length() == 0){
                    s = "0";
                }
                int salary = Integer.parseInt(s+"");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date create = sdf.parse(empCreated_at.getText()+"", new ParsePosition(0));
                Date now = new Date();
                long diff = now.getTime() - create.getTime();
                int year = (int) (diff / 1000 / 60 / 60 / 24 / 365);
                int allowance = salary*20/100 + year*100000;
                empAllowance.setText(allowance+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // X??? l?? s??? ki???n click button "Hu???":
        btnCancel.setOnClickListener(v -> finish());

        // X??? l?? s??? ki???n click button "L??u":
        btnSave.setOnClickListener(v -> {
            checkTTV();
            if (checkError() == 1) {
                if (type.equals("add")) {
                    checkTrungID();
                    new Handler().postDelayed(() -> {
                        if (check && check1) {
                            saveEmployees();
                        }
                    }, 200);

                } else if (type.equals("edit")) {
                    new Handler().postDelayed(() -> {
                        if (check1) {
                            saveEmployees();
                        }
                    }, 200);
                }
            }
        });

        // X??? l?? s??? ki???n click button "Xem b???ng l????ng":
        btnXemBangLuong.setOnClickListener(v -> {
            intent = new Intent(DetailEmployeeActivity.this, DetailSalaryActivity.class);
            intent.putExtra("key", empID.getText() + "");
            startActivity(intent);
        });

        empPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(empPosition.getSelectedItem().equals("Nh??n vi??n giao h??ng")) {
                    empShipArea.setVisibility(View.VISIBLE);
                } else empShipArea.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveEmployees() {
        Employee employees = new Employee();

        if(type.equals("add")) employees.setAccountID("");
        employees.setAddress(empAddress.getText() + "");
        if (!String.valueOf(empAllowance.getText()).equals("")) {
            employees.setAllowance(Integer.parseInt(empAllowance.getText() + ""));
        }
        employees.setBirthday(empDOB.getText() + "");
        employees.setCreated_at(empCreated_at.getText() + "");
        employees.setGender((String) empGender.getSelectedItem());
        employees.setName(empName.getText() + "");
        employees.setPosition((String) empPosition.getSelectedItem());
        employees.setSalary(Integer.parseInt(empSalary.getText() + ""));
        //get image
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/Employeess/" + employees.getName() + ".jpg");
        Bitmap bitmap = ((BitmapDrawable) empImage.getDrawable()).getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(DetailEmployeeActivity.this.getContentResolver(), bitmap, "Title", null);
        imageUri = Uri.parse(path);
        bar.setVisibility(View.VISIBLE);
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                employees.setImage(uri.toString());
                empRef.child(empID.getText() + "").setValue(employees).addOnSuccessListener(unused -> {
                    bar.setVisibility(View.INVISIBLE);
                    if (type.equals("add")) {
                        showSuccesDialog("Th??m nh??n vi??n th??nh c??ng!");
                        pushAccountHistory("Th??m nh??n vi??n", "M?? NV: " + employees.getId() + "\nH??? t??n NV: " + employees.getName());
                    } else {
                        showSuccesDialog("C???p nh???t nh??n vi??n th??nh c??ng!");
                        pushAccountHistory("C???p nh???t nh??n vi??n", "M?? NV: " + employees.getId() + "\nH??? t??n NV: " + employees.getName());
                    }
                });
            }
        }));

        if(empPosition.getSelectedItem().toString().equals("Nh??n vi??n giao h??ng")){
            Area area = (Area) empShipArea.getSelectedItem();

            shipAreaRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean checkz = true;
                    String key = "";
                    for(DataSnapshot node : snapshot.getChildren()){
                        String id = node.child("employeeID").getValue(String.class);
                        if(id.equals(empID.getText()+"")){
                            checkz = false;
                            key = node.getKey();
                        }
                    }
                    if(checkz){
                        HashMap map =  new HashMap();
                        map.put("areaID",area.getKey());
                        map.put("employeeID",empID.getText()+"");
                        shipAreaRef.push().setValue(map);
                    }else{
                        shipAreaRef.child(key).child("areaID").setValue(area.getKey());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private int checkError() {

        if (type.equals("add")) {
            if (imageUri == null) {
                showWarningDialog("Vui l??ng ch???n ???nh cho nh??n vi??n");
            }
        }
        if (String.valueOf(empID.getText()).equals("")) {
            showWarningDialog("M?? nh??n vi??n kh??ng ???????c ????? tr???ng");
            return -1;
        }
        if (String.valueOf(empName.getText()).equals("")) {
            showWarningDialog("T??n nh??n vi??n kh??ng ???????c ????? tr???ng");
            return -1;
        }
        if (String.valueOf(empAddress.getText()).equals("")) {
            showWarningDialog("?????a ch??? kh??ng ???????c ????? tr???ng");
            return -1;
        }
        if (String.valueOf(empSalary.getText()).equals("")) {
            showWarningDialog("L????ng c?? b???n kh??ng ???????c ????? tr???ng");
            return -1;
        }
        if (Integer.parseInt(String.valueOf(empSalary.getText())) <= 3000000) {
            showWarningDialog("L????ng c?? b???n ph???i l???n h??n 3.000.000 ??");
            return -1;
        }
        return 1;
    }

    private void checkTrungID() {
        empRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                check = true;
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    if (node.getKey().equals(String.valueOf(empID.getText()))) {
                        check = false;
                        showWarningDialog("M?? nh??n vi??n kh??ng ???????c tr??ng!");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkTTV(){
        if(empPosition.getSelectedItem().toString().equals("T?? v???n vi??n")){
            empRef.orderByChild("position").equalTo("T?? v???n vi??n").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    check1 = true;
                    for(DataSnapshot node : snapshot.getChildren()){
                        if(node.exists()){
                            if(!node.getKey().equals(empID.getText()+"")){
                                Log.d("TAG",node.getKey());
                                check1 = false;
                            }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if(check1 == false){
            showWarningDialog("???? c?? t?? v???n vi??n trong c???a h??ng");
        }
    }

    private void data() {
        if (type.equals("edit")) {
            empID.setText(item.getId());
            for (int i = 0; i < empPosition.getCount(); i++) {
                String position = (String) empPosition.getItemAtPosition(i);
                if (position.equals(item.getPosition())) {
                    empPosition.setSelection(i);
                }
            }
            empName.setText(item.getName());
            empAddress.setText(item.getAddress());
            empDOB.setText(item.getBirthday());
            for (int i = 0; i < empGender.getCount(); i++) {
                String gender = (String) empGender.getItemAtPosition(i);
                if (gender.equals(item.getGender())) {
                    empGender.setSelection(i);
                }
            }
            empCreated_at.setText(item.getCreated_at());
            String seniority = getSeniority(item.getCreated_at());
            empSeniority.setText(seniority);
            empSalary.setText("" + item.getSalary());
            empAllowance.setText("" + item.getAllowance());
            Picasso.get().load(item.getImage()).fit().into(empImage);
        }
        areaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot node : snapshot.getChildren()) {
                    Area area = new Area(node.getKey(), node.child("area").getValue(String.class));
                    listArea.add(area);
                }
                areaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getSeniority(String create_at) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date create = sdf.parse(create_at, new ParsePosition(0));
        Date now = new Date();
        long diff = now.getTime() - create.getTime();
        Log.d("TAG",diff+"");
        int year = (int) (diff / 1000 / 60 / 60 / 24 / 365);
        if (year < 1) {
            return "D?????i 1 n??m";
        } else {
            return year + " n??m";
        }
    }

    private void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
            date = dayOfMonth + "/" + (month + 1) + "/" + year;
            if(dayOfMonth < 10){
                date = "0"+date;
            }
            if (v.getId() == R.id.dtp_ngay_sinh) {
                empDOB.setText(date);
            } else {
                empCreated_at.setText(date);
                empSeniority.setText(getSeniority(date));
            }
        },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showSuccesDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailEmployeeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailEmployeeActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        TextView mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showWarningDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailEmployeeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailEmployeeActivity.this).inflate(
                R.layout.layout_warning_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        TextView mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.yes));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE) {
            imageUri = data.getData();
            empImage.setImageURI(imageUri);
        }
    }

    public void pushAccountHistory(String action, String detail) {
        // Th??m v??o "L???ch s??? ho???t ?????ng"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setAccountID(accountID);
        accountHistory.setAction(action);
        accountHistory.setDetail(detail);
        accountHistory.setDate(sdf.format(new Date()));
        FirebaseDatabase.getInstance().getReference("AccountHistory").push().setValue(accountHistory);
    }
}
