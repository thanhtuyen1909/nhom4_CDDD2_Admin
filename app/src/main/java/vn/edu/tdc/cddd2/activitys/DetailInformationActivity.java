package vn.edu.tdc.cddd2.activitys;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Category;
import vn.edu.tdc.cddd2.data_models.Manufacture;

public class DetailInformationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnCancel, title, mess;
    EditText edtName;
    ImageView img;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    Intent intent;
    Category itemCate = null;
    Manufacture itemManu = null;
    String to, key = null, name = null, image = null, location = "images/categories";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference cateRef = database.getReference("Categories");
    DatabaseReference manuRef = database.getReference("Manufactures");
    StorageReference imageRef = null;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_infomation);
        to = getIntent().getStringExtra("to");
        itemCate = (Category) getIntent().getParcelableExtra("itemCate");
        itemManu = (Manufacture) getIntent().getParcelableExtra("itemManu");

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        edtName = findViewById(R.id.edtTen);
        img = findViewById(R.id.imageView);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        if (to.equals("ListCate")) {
            subtitleAppbar.setText(R.string.titleDetailLSP);
            edtName.setHint("Tên loại sản phẩm");
        } else {
            subtitleAppbar.setText(R.string.titleDetailH);
            edtName.setHint("Tên hãng");
            location = "images/manufactures";
        }
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Kiểm tra dữ liệu:
        if (itemCate != null) {
            edtName.setText(itemCate.getName());
            name = itemCate.getName();
            key = itemCate.getKey();
            image = itemCate.getImage();
            imageRef = FirebaseStorage.getInstance().getReference("images/categories/" + image);
            imageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    img.setImageBitmap(Bitmap.createScaledBitmap(bmp, img.getWidth(), img.getHeight(), false));
                }
            });
        }

        if (itemManu != null) {
            edtName.setText(itemManu.getName());
            name = itemManu.getName();
            key = itemManu.getKey();
            image = itemManu.getImage();
            imageRef = FirebaseStorage.getInstance().getReference("images/manufactures/" + image);
            imageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    img.setImageBitmap(Bitmap.createScaledBitmap(bmp, img.getWidth(), img.getHeight(), false));
                }
            });
        }

        // Sự kiện click:
        btnSave.setOnClickListener(this);
        img.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlsp:
                intent = new Intent(DetailInformationActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                intent = new Intent(DetailInformationActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
                intent = new Intent(DetailInformationActivity.this, OrderCoordinationActivity.class);
                startActivity(intent);
                Toast.makeText(DetailInformationActivity.this, "Điều phối hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(DetailInformationActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(DetailInformationActivity.this, ListCateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(DetailInformationActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(DetailInformationActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(DetailInformationActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(DetailInformationActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
        name = edtName.getText().toString();
        if (v == btnSave) {
            //upload ảnh
            if (filePath != null) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference(location)
                        .child(name + "." + getFileExtension(filePath));
                storageRef.putFile(filePath);
                image = name + "." + getFileExtension(filePath);
            }
            if (name == null) {
                showWarningDialog();
            } else {
                if (to.equals("ListCate")) {
                    Category category = new Category();
                    category.setName(name);
                    category.setImage(image);
                    if (itemCate == null) {
                        cateRef.push().setValue(category).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showSuccesDialog("Thêm loại sản phẩm thành công!");
                            }
                        });
                    } else {
                        cateRef.child(key).setValue(category).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showSuccesDialog("Cập nhật loại sản phẩm thành công!");
                            }
                        });
                    }
                }
                else {
                    Manufacture manufacture = new Manufacture();
                    manufacture.setName(name);
                    manufacture.setImage(image);
                    if (itemManu == null) {
                        manuRef.push().setValue(manufacture).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showSuccesDialog("Thêm hãng thành công!");
                            }
                        });
                    } else {
                        manuRef.child(key).setValue(manufacture).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showSuccesDialog("Cập nhật hãng thành công!");
                            }
                        });
                    }
                }
            }

        } else if (v == img) {
            chooseImage();
        } else {
            showErrorDialog();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cR.getType(uri));
    }

    private void showWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailInformationActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailInformationActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText("THÔNG BÁO");
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText("Tên không được để trống!");
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailInformationActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailInformationActivity.this).inflate(
                R.layout.layout_error_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText("THÔNG BÁO");
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận huỷ?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailInformationActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailInformationActivity.this).inflate(
                R.layout.layout_succes_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText("THÔNG BÁO");
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText(message);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                finish();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }


}