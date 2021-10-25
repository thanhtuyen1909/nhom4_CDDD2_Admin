package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.PromoCode;

public class InformationPromoCodeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    // Khai báo biến
    Toolbar toolbar;
    ImageView img;
    TextView btnSave, subtitleAppbar, btnCancel, btnBrowser, title, mess;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    Intent intent;
    private EditText edtStartDate, edtEndDate, edtName;
    private String date;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath = null;
    PromoCode item = null;
    DatabaseReference promoRef;
    StorageReference imageRef = null;
    String key = null, name = null, image = null, startDate = null, endDate = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_info_promocode);
        item = (PromoCode) getIntent().getParcelableExtra("item");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleLayoutTTKM);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        btnBrowser = findViewById(R.id.txtChonAnh);
        edtName = findViewById(R.id.edtTen);
        edtStartDate = findViewById(R.id.edtNgayBD);
        edtEndDate = findViewById(R.id.edtNgayKT);
        promoRef = FirebaseDatabase.getInstance().getReference("Offers");
        img = findViewById(R.id.imageView);

        if (item != null) {
            key = item.getKey();
            name = item.getName();
            startDate = item.getStartDate();
            endDate = item.getEndDate();
            image = item.getImage();
            edtName.setText(name);
            edtStartDate.setText(startDate);
            edtEndDate.setText(endDate);
            imageRef = FirebaseStorage.getInstance().getReference("images/promocodes/" + image);
            Log.d("TAG", "onCreate: " + imageRef);
            imageRef.getBytes(1024 * 1024).addOnSuccessListener(bytes -> {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                img.setImageBitmap(Bitmap.createScaledBitmap(bmp, img.getWidth(), img.getHeight(), false));
            });
        }

        // Xử lý sự kiện click button :
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnBrowser.setOnClickListener(this);

        // Xử lý dự kiện hiện DatepickerDialog cho edittext:
        edtStartDate.setOnClickListener(this);
        edtEndDate.setOnClickListener(this);
    }

    private void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
            date = dayOfMonth + "/" + (month + 1) + "/" + year;
            if (v.getId() == R.id.edtNgayBD) {
                edtStartDate.setText(date);
            } else edtEndDate.setText(date);
        },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            Picasso.get().load(filePath).into(img);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlsp:
                intent = new Intent(InformationPromoCodeActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                intent = new Intent(InformationPromoCodeActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
                intent = new Intent(InformationPromoCodeActivity.this, OrderCoordinationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(InformationPromoCodeActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(InformationPromoCodeActivity.this, ListCateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(InformationPromoCodeActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(InformationPromoCodeActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(InformationPromoCodeActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(InformationPromoCodeActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            name = edtName.getText().toString();
            startDate = edtStartDate.getText().toString();
            endDate = edtEndDate.getText().toString();
            //upload ảnh
            if (filePath != null) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/promocodes")
                        .child(name + "." + getFileExtension(filePath));
                storageRef.putFile(filePath);
                image = name + "." + getFileExtension(filePath);
            }
            if (name == null) {
                showWarningDialog();
            } else {
                PromoCode promoCode = new PromoCode();
                promoCode.setName(name);
                promoCode.setStartDate(startDate);
                promoCode.setEndDate(endDate);
                promoCode.setImage(image);
                if (item == null) {
                    promoRef.push().setValue(promoCode).addOnSuccessListener(unused -> showSuccesDialog("Thêm khuyến mãi thành công!"));
                } else {
                    promoRef.child(key).setValue(promoCode).addOnSuccessListener(unused -> showSuccesDialog("Cập nhật khuyến mãi thành công!"));
                }
            }
        } else if (v == btnCancel) {
            showErrorDialog();
        } else if (v == btnBrowser) {
            chooseImage();
        } else showDatePickerDialog(v);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InformationPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(InformationPromoCodeActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText("Tên không được để trống!");
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InformationPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(InformationPromoCodeActivity.this).inflate(
                R.layout.layout_error_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận huỷ?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            finish();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InformationPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(InformationPromoCodeActivity.this).inflate(
                R.layout.layout_succes_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText(message);
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
}

