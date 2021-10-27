package vn.edu.tdc.cddd2.activitys;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParsePosition;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Category;
import vn.edu.tdc.cddd2.data_models.Manufacture;
import vn.edu.tdc.cddd2.data_models.Product;

public class DetailInformationActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnCancel, title, mess;
    EditText edtName;
    ImageView img;
    Intent intent;
    Category itemCate = null;
    Manufacture itemManu = null;
    String to, key = null, name = null, image = null, location = "images/categories", username = "";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference cateRef = database.getReference("Categories");
    DatabaseReference manuRef = database.getReference("Manufactures");
    StorageReference imageRef = null;
    boolean check = true;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_infomation);
        intent = getIntent();
        username = intent.getStringExtra("username");
        to = intent.getStringExtra("to");
        itemCate = intent.getParcelableExtra("itemCate");
        itemManu = intent.getParcelableExtra("itemManu");

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

        // Kiểm tra dữ liệu:
        if (itemCate != null) {
            edtName.setText(itemCate.getName());
            name = itemCate.getName();
            key = itemCate.getKey();
            image = itemCate.getImage();
            imageRef = FirebaseStorage.getInstance().getReference("images/categories/" + image);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).resize(img.getWidth(), img.getHeight()).into(img);
            });
        }

        if (itemManu != null) {
            edtName.setText(itemManu.getName());
            name = itemManu.getName();
            key = itemManu.getKey();
            image = itemManu.getImage();
            imageRef = FirebaseStorage.getInstance().getReference("images/manufactures/" + image);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Picasso.get().load(uri).resize(img.getWidth(), img.getHeight()).into(img);
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

    public int checkError() {
        name = edtName.getText().toString();
        //Check chưa chọn image
        if (filePath == null) {
            showWarningDialog("Vui lòng chọn ảnh!");
            return -1;
        }
        //Check tên khuyến mãi trống
        if (name.equals("")) {
            showWarningDialog("Tên không được để trống!");
            if (edtName.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        return 1;
    }

    private void checkTrungID(String id) {
        name = edtName.getText().toString();
        check = true;
        //Check trùng loại
        if(to.equals("ListCate")) {
            cateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    check = true;
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Category temp = node.getValue(Category.class);
                        if (temp.getName().equals(name)) {
                            check = false;
                            showWarningDialog("Loại sản phẩm đã tồn tại!");
                            if (edtName.requestFocus()) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                            edtName.setText("");
                            break;
                        }
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            manuRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    check = true;
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Manufacture temp = node.getValue(Manufacture.class);
                        if (temp.getName().equals(name)) {
                            check = false;
                            showWarningDialog("Hãng đã tồn tại!");
                            if (edtName.requestFocus()) {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                            }
                            edtName.setText("");
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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
    public void onClick(View v) {
        name = edtName.getText().toString();
        if (v == btnSave) {
            if(checkError() == 1) {
                //upload ảnh
                if (filePath != null) {
                    StorageReference storageRef = FirebaseStorage.getInstance().getReference(location)
                            .child(name + "." + getFileExtension(filePath));
                    storageRef.putFile(filePath);
                    image = name + "." + getFileExtension(filePath);
                }
                if (to.equals("ListCate")) {
                    Category category = new Category();
                    category.setName(name);
                    category.setImage(image);
                    if (itemCate == null) {
                        cateRef.push().setValue(category).addOnSuccessListener(unused -> showSuccesDialog("Thêm loại sản phẩm thành công!"));
                    } else {
                        cateRef.child(key).setValue(category).addOnSuccessListener(unused -> showSuccesDialog("Cập nhật loại sản phẩm thành công!"));
                    }
                }
                else {
                    Manufacture manufacture = new Manufacture();
                    manufacture.setName(name);
                    manufacture.setImage(image);
                    if (itemManu == null) {
                        manuRef.push().setValue(manufacture).addOnSuccessListener(unused -> showSuccesDialog("Thêm hãng thành công!"));
                    } else {
                        manuRef.child(key).setValue(manufacture).addOnSuccessListener(unused -> showSuccesDialog("Cập nhật hãng thành công!"));
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

    private void showWarningDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailInformationActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailInformationActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText(s);
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
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailInformationActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailInformationActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText("THÔNG BÁO");
        mess = view.findViewById(R.id.textMessage);
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