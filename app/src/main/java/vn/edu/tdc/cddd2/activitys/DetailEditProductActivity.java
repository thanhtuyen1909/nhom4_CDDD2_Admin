
package vn.edu.tdc.cddd2.activitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import vn.edu.tdc.cddd2.DAO.DAOProduct;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.AccountHistory;
import vn.edu.tdc.cddd2.data_models.Category;
import vn.edu.tdc.cddd2.data_models.HistoryActivity;
import vn.edu.tdc.cddd2.data_models.Manufacture;
import vn.edu.tdc.cddd2.data_models.Product;

public class DetailEditProductActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnCancel;
    private ImageView productImage;
    private EditText productID, productName, productDescription, productQuantity, productImportPrice, productPrice;
    TextView title, mess;
    private Intent intent;
    private Spinner spinCata, spinManu;
    AutoCompleteTextView spinColor, spinStorage;
    private final static int SELECT_IMAGE_CODE = 1;
    private Uri imageUri;
    private ArrayList<Manufacture> listManu;
    private ArrayList<Category> listCate;
    private final static FirebaseDatabase db = FirebaseDatabase.getInstance();
    List<String> colors, sizes;
    String username = "", accountID = "";
    Product item = null;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_edit_product);
        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        item = intent.getParcelableExtra("item");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.title9);

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        productImage = findViewById(R.id.imageView);
        productID = findViewById(R.id.edtMSP);
        productName = findViewById(R.id.edtTSP);
        productDescription = findViewById(R.id.edtMT);
        productQuantity = findViewById(R.id.edtSL);
        productImportPrice = findViewById(R.id.edtGN);
        productPrice = findViewById(R.id.edtGB);
        spinCata = findViewById(R.id.spinner_cata);
        spinManu = findViewById(R.id.spinner_manu);
        spinColor = findViewById(R.id.spinner_color);
        spinStorage = findViewById(R.id.spinner_size);
        btnSave = findViewById(R.id.txtSave);
        colors = Arrays.asList(getResources().getStringArray(R.array.mau));
        sizes = Arrays.asList(getResources().getStringArray(R.array.dungluong));

        if (item != null) {
            data();
        }

        productImage.setOnClickListener(v -> {
            intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
        });

        // Xử lý sự kiện click button:
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        // AutoCompleteTextView
        ArrayAdapter<String> adapterColor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, colors);
        spinColor.setAdapter(adapterColor);
        ArrayAdapter<String> adapterSize = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sizes);
        spinStorage.setAdapter(adapterSize);

        spinColor.setOnClickListener(view -> spinColor.showDropDown());

        spinStorage.setOnClickListener(view -> spinStorage.showDropDown());
    }

    public int checkError() {
        //Check mã sản phẩm trống
        if (String.valueOf(productID.getText()).compareTo("") == 0) {
            showWarningDialog("Mã sản phẩm không được để trống");
            if (productID.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check tên sản phẩm trống
        if (String.valueOf(productName.getText()).compareTo("") == 0) {
            showWarningDialog("Tên sản phẩm không được để trống");
            if (productName.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check mô tả sản phẩm trống
        if (String.valueOf(productDescription.getText()).compareTo("") == 0) {
            showWarningDialog("Mô tả sản phẩm không được để trống");
            if (productDescription.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check Số lượng sản phẩm trống
        if (String.valueOf(productQuantity.getText()).compareTo("") == 0) {
            showWarningDialog("Số lượng sản phẩm không được để trống");
            if (productQuantity.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check số lượng sản phẩm < 0
        if (Integer.parseInt(String.valueOf(productQuantity.getText())) <= 0) {
            showWarningDialog("Số lượng sản phẩm phải lớn hơn 0");
            if (productQuantity.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá nhập sản phẩm trống
        if (String.valueOf(productImportPrice.getText()).compareTo("") == 0) {
            showWarningDialog("Giá nhập sản phẩm không được để trống");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá nhập sản phẩm < 0
        if (Integer.parseInt(String.valueOf(productImportPrice.getText())) <= 0) {
            showWarningDialog("Giá nhập sản phẩm phải lớn hơn 0");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá bán sản phẩm trống
        if (String.valueOf(productPrice.getText()).compareTo("") == 0) {
            showWarningDialog("Giá bán sản phẩm không được để trống");
            if (productPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá bản < giá nhập
        if (Integer.parseInt(String.valueOf(productImportPrice.getText())) >= Integer.parseInt(String.valueOf(productPrice.getText()))) {
            showWarningDialog("Giá bán sản phẩm phải lớn hơn giá nhập");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }

        return 1;
    }

    private void data() {
        DatabaseReference refManu = db.getReference("Manufactures");
        DatabaseReference refCate = db.getReference("Categories");
        listManu = new ArrayList<>();
        listCate = new ArrayList<>();
        ArrayAdapter manuAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, listManu);

        ArrayAdapter cateAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, listCate);

        manuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinManu.setAdapter(manuAdapter);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCata.setAdapter(cateAdapter);
        refManu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Manufacture manu = node.getValue(Manufacture.class);
                    manu.setKey(node.getKey());
                    listManu.add(manu);
                    manuAdapter.notifyDataSetChanged();
                    for (int i = 0; i < manuAdapter.getCount(); i++) {
                        if (item.getManu_id().equals(node.getKey()) && manu.equals(manuAdapter.getItem(i))) {
                            spinManu.setSelection(i);
                            break;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refCate.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Category cate = node.getValue(Category.class);
                    cate.setKey(node.getKey());
                    listCate.add(cate);
                    cateAdapter.notifyDataSetChanged();
                    for (int i = 0; i < cateAdapter.getCount(); i++) {
                        if (item.getCategory_id().equals(node.getKey()) && cate.equals(cateAdapter.getItem(i))) {
                            spinCata.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set dữ liệu cho sản phẩm
        productID.setText(String.valueOf(item.getId()));
        productID.setEnabled(false);
        productName.setText(item.getName().split("- ")[0].trim());
        productDescription.setText(item.getDescription());
        productQuantity.setText("" + item.getQuantity());
        productImportPrice.setText("" + item.getImport_price());
        productPrice.setText("" + item.getPrice());
        String[] list = item.getName().split("- ");
        if (list.length == 1) {
            spinColor.setText("");
            spinStorage.setText("");
        } else if (list.length == 2) {
            spinColor.setText(list[1]);
            spinStorage.setText("");
        } else if (list.length == 3) {
            spinColor.setText(list[1]);
            spinStorage.setText(list[2]);
        }
        //set image
        final long ONE_MEGABYTE = 1024 * 1024;
        StorageReference imageRef = storage.getReference("images/products/" + item.getName() + "/" + item.getImage());
        imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                productImage.setImageBitmap(Bitmap.createScaledBitmap(bmp, productImage.getWidth(), productImage.getHeight(), false));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }

    public void pushAccountHistory(String action, String detail) {
        // Thêm vào "Lịch sử hoạt động"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setAccountID(accountID);
        accountHistory.setAction(action);
        accountHistory.setDetail(detail);
        accountHistory.setDate(sdf.format(new Date()));
        db.getReference("AccountHistory").push().setValue(accountHistory);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (checkError() == 1) {
                Product product = new Product();
                product.setSold(0);
                product.setCreated_at(formatter.format(now));
                product.setId(String.valueOf(productID.getText()));
                String color = String.valueOf(spinColor.getText());
                String size = String.valueOf(spinStorage.getText());
                product.setName(String.valueOf(productName.getText()));
                if (!color.equals("")) {
                    product.setName(product.getName() + " - " + color);
                }
                if (!size.equals("")) {
                    product.setName(product.getName() + " - " + size);
                }
                String keymanu = ((Manufacture) spinManu.getSelectedItem()).getKey();
                String keyCate = ((Category) spinCata.getSelectedItem()).getKey();

                product.setQuantity(Integer.parseInt(String.valueOf(productQuantity.getText())));
                product.setImport_price(Integer.parseInt(String.valueOf(productImportPrice.getText())));
                product.setPrice(Integer.parseInt(String.valueOf(productPrice.getText())));
                product.setDescription(String.valueOf(productDescription.getText()));
                product.setImage(product.getName() + ".jpg");
                product.setManu_id(keymanu);
                product.setCategory_id(keyCate);

                if (!product.getName().equals(item.getName())) {
                    StorageReference ref = storage.getReference("images/products");
                    StorageReference oldRef = ref.child(item.getName()).child(item.getImage());
                    Bitmap bitmap = ((BitmapDrawable) productImage.getDrawable()).getBitmap();
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(DetailEditProductActivity.this.getContentResolver(), bitmap, "Title", null);
                    imageUri = Uri.parse(path);
                    oldRef.delete();
                    ref.child(item.getName()).delete();
                }

                //upload ảnh
                if (imageUri != null) {
                    StorageReference storageRef = storage.getReference("images/products");
                    StorageReference productRef = storageRef.child(product.getName()).child(product.getImage());
                    productRef.putFile(imageUri);
                }
                DAOProduct dao = new DAOProduct();
                dao.update(item.getKey(), product).addOnSuccessListener(unused -> showSuccesDialog("Sửa sản phẩm thành công!"));
                pushAccountHistory("Cập nhật thông tin sản phẩm", "Sản phẩm có mã " + product.getId());
            }
        } else {
            finish();
        }
    }

    private void showSuccesDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailEditProductActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailEditProductActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailEditProductActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailEditProductActivity.this).inflate(
                R.layout.layout_warning_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.yes));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}

