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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.ybq.android.spinkit.style.FoldingCube;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Employees;

public class DetailEmployeeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnSave, subtitleAppbar, btnXemBangLuong, btnCancel;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Intent intent;
    private ImageView empImage;
    private Spinner empPosition, empGender;
    private EditText empName, empID, empAddress, empSalary, empAllowance, empSeniority, empDOB, empCreated_at;
    Uri imageUri;
    ProgressBar bar;
    String type = "add", date = "";
    Employees item ;
    DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("Employeess");
    static int SELECT_IMAGE_CODE = 1;
    boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_employee);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            type = bundle.getString("type");
            item = bundle.getParcelable("item");
        }
        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Thông tin nhân viên");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        btnXemBangLuong = findViewById(R.id.btnXBL);
        empID = findViewById(R.id.edtMNV);
        empPosition = findViewById(R.id.spinner_chucvu);
        empName = findViewById(R.id.edtTNV);
        empAddress = findViewById(R.id.edtDC);
        empDOB = findViewById(R.id.dtp_ngay_sinh);
        empGender = findViewById(R.id.spinner_gioitinh);
        empCreated_at = findViewById(R.id.dtp_ngayvao);
        empSeniority = findViewById(R.id.edtTGLV);
        empSalary = findViewById(R.id.edtLCB);
        empAllowance = findViewById(R.id.edtPC);
        empImage = findViewById(R.id.imageView);
        bar =findViewById(R.id.progess1);
        bar.setIndeterminateDrawable(new FoldingCube());

        data();
        empDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        empCreated_at.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        empImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
            }
        });
        // Xử lý sự kiện click button "Huỷ":
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện click button "Lưu":
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTrungID();
                if (checkError() == 1) {
                    if(type.equals("add")){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (check == true) {
                                    Log.d("TAG", "onClick: a");
                                    saveEmployees();
                                }
                            }
                        },200);

                    }
                    else if(type.equals("edit")){
                        saveEmployees();
                    }
                }
            }
        });

        // Xử lý sự kiện click button "Xem bảng lương":
        btnXemBangLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(DetailEmployeeActivity.this,DetailSalaryActivity.class);
                intent.putExtra("key",empID.getText()+"");
                startActivity(intent);
            }
        });

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void saveEmployees(){
        Employees Employees = new Employees();

        Employees.setAccountID("");
        Employees.setAddress(empAddress.getText() + "");
        if(!String.valueOf(empAllowance.getText()).equals("")){
            Employees.setAllowance(Integer.parseInt(empAllowance.getText() + ""));
        }
        Employees.setBirthday(empDOB.getText() + "");
        Employees.setCreated_at(empCreated_at.getText() + "");
        Employees.setGender((String) empGender.getSelectedItem());
        Employees.setName(empName.getText() + "");
        Employees.setPosition((String) empPosition.getSelectedItem());
        Employees.setSalary(Integer.parseInt(empSalary.getText() + ""));
        //get image
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("images/Employeess/" + Employees.getName() + ".jpg");
        Bitmap bitmap = ((BitmapDrawable) empImage.getDrawable()).getBitmap();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(DetailEmployeeActivity.this.getContentResolver(), bitmap, "Title", null);
        imageUri = Uri.parse(path);
        bar.setVisibility(View.VISIBLE);
        imageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Employees.setImage(uri.toString());
                        empRef.child(empID.getText() + "").setValue(Employees).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                bar.setVisibility(View.INVISIBLE);
                                if(type.equals("add")){
                                    showSuccesDialog("Thêm nhân viên thành công !");
                                }else{
                                    showSuccesDialog("Cập nhật nhân viên thành công !");
                                }

                            }
                        });
                    }
                });
            }
        });
    }
    private int checkError() {
        if (type.equals("add")) {
            if (imageUri == null) {
                showWarningDialog("Vui lòng chọn ảnh cho nhân viên");
            }
        }
        if (String.valueOf(empID.getText()).equals("")) {
            showWarningDialog("Mã nhân viên không được để trống");
            return -1;
        }
        if (String.valueOf(empName.getText()).equals("")) {
            showWarningDialog("Tên nhân viên không được để trống");
            return -1;
        }
        if (String.valueOf(empAddress.getText()).equals("")) {
            showWarningDialog("Địa chỉ không được để trống");
            return -1;
        }
        if (String.valueOf(empSalary.getText()).equals("")) {
            showWarningDialog("Lương cơ bản không được để trống");
            return -1;
        }
        if (Integer.parseInt(String.valueOf(empSalary.getText())) <= 3000000) {
            showWarningDialog("Lương cơ bản phải lớn hơn 3.000.000 đ");
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
                        showWarningDialog("Mã nhân viên không được trùng!");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void data() {
        if (type.equals("edit")) {
            empID.setText(item.getKeyEmployees());
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
    }

    private String getSeniority(String create_at) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date create = sdf.parse(create_at, new ParsePosition(0));
        Date now = new Date();
        long diff = now.getTime() - create.getTime();
        int year = (int) (diff / (1000 * 60 * 60 * 24) % 365);
        if (year < 1) {
            return "Dưới 1 năm";
        } else {
            return year + " năm";
        }
    }

    private void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
            date = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (v.getId() == R.id.dtp_ngay_sinh) {
                empDOB.setText(date);
            } else empCreated_at.setText(date);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlnv:
                break;
            case R.id.nav_dd:
                intent = new Intent(DetailEmployeeActivity.this, AttendanceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(DetailEmployeeActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
            Intent itent = new Intent(DetailEmployeeActivity.this,ListEmployeeActivity.class);
            startActivity(itent);
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
}
