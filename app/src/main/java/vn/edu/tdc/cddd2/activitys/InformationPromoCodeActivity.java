package vn.edu.tdc.cddd2.activitys;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.Notification;
import vn.edu.tdc.cddd2.data_models.PromoCode;

public class InformationPromoCodeActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến
    Toolbar toolbar;
    ImageView img;
    TextView btnSave, subtitleAppbar, btnCancel, btnBrowser, title, mess;
    Intent intent;
    private EditText edtStartDate, edtEndDate, edtName;
    private String date;
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath = null;
    PromoCode item = null;
    DatabaseReference promoRef;
    StorageReference imageRef = null;
    String key = null, name = null, image = null, startDate = null, endDate = null, username = "";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_info_promocode);
        intent = getIntent();
        username = intent.getStringExtra("username");
        item = (PromoCode) intent.getParcelableExtra("item");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleLayoutTTKM);

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

    public int checkError() {
        name = edtName.getText().toString();
        startDate = edtStartDate.getText().toString();
        endDate = edtEndDate.getText().toString();
        Date sdate = sdf.parse(startDate, new ParsePosition(0));
        Date edate = sdf.parse(endDate, new ParsePosition(0));
        Date now = new Date();

        //Check chưa chọn image
        if (filePath == null && item.getImage().equals("")) {
            showWarningDialog("Vui lòng chọn ảnh cho chương trình khuyến mãi!");
            return -1;
        }

        //Check tên khuyến mãi trống
        if (name.equals("")) {
            showWarningDialog("Tên khuyến mãi không được để trống!");
            if (edtName.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check ngày
        if (sdate.before(now)) {
            showWarningDialog("Ngày bắt đầu phải lớn hơn hoặc bằng ngày hiện tại!");
            return -1;
        }
        if (edate.before(sdate)) {
            showWarningDialog("Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu!");
            return -1;
        }

        return 1;
    }

    private void showDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, (view, year, month, dayOfMonth) -> {
            date = (dayOfMonth + 1) + "/" + (month + 1) + "/" + year;
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
        if (v == btnSave) {
            name = edtName.getText().toString();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/promocodes")
                    .child(name + "." + getFileExtension(filePath));
            startDate = edtStartDate.getText().toString();
            endDate = edtEndDate.getText().toString();
            if(checkError() == 1) {
                //upload ảnh
                if (filePath != null) {
                    storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //lưu thông báo
                            DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference("Notification");
                            DatabaseReference accountRef = FirebaseDatabase.getInstance().getReference("Account");
                            accountRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot node : dataSnapshot.getChildren()){
                                        Account account = node.getValue(Account.class);
                                        if(account.getRole_id() == 1){
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                                            Notification noti = new Notification();
                                            noti.setTitle(name);
                                            noti.setCreated_at(sdf.format(new Date()));
                                            noti.setContent("Tưng bừng khuyến mãi từ ngày "+startDate+" đến "+endDate);
                                            noti.setAccountID(node.getKey());
                                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Log.d("TAG", "onSuccess: +"+uri.toString());
                                                    noti.setImage(uri.toString());
                                                    notiRef.push().setValue(noti);
                                                    //get image uri
                                                }
                                            });

                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                    image = name + "." + getFileExtension(filePath);
                }
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

    private void showWarningDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InformationPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(InformationPromoCodeActivity.this).inflate(
                R.layout.layout_warning_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(s);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(InformationPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(InformationPromoCodeActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
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

