package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import vn.edu.tdc.cddd2.DAO.DAOProduct;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.Product1Adapter;
import vn.edu.tdc.cddd2.data_models.Category;
import vn.edu.tdc.cddd2.data_models.Manufacture;
import vn.edu.tdc.cddd2.data_models.Product;

public class DetailProductActivity extends AppCompatActivity {
    // Khai báo biến
    Handler handler = new Handler();
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnCancel, title, mess;
    private ImageView productImage;
    private EditText productID, productName, productDescription, productQuantity, productImportPrice, productPrice;
    private Intent intent;
    String username = "";
    RecyclerView recyclerView;
    private ArrayList<Product> listProduct;
    private Spinner spinCata, spinManu;
    AutoCompleteTextView spinColor, spinStorage;
    private Product1Adapter proAdapter;
    Button btnAdd;
    static int SELECT_IMAGE_CODE = 1;
    private Uri imageUri;
    private ArrayList<Manufacture> listManu;
    private ArrayList<Category> listCate;
    List<String> colors, sizes;

    static FirebaseDatabase db = FirebaseDatabase.getInstance();
    boolean check = true, check1 = true, checkSave = true;

    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_product);
        intent = getIntent();
        username = intent.getStringExtra("username");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.title9);

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        btnAdd = findViewById(R.id.btnAdd);
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
        listProduct = new ArrayList<>();
        colors = Arrays.asList(getResources().getStringArray(R.array.mau));
        sizes = Arrays.asList(getResources().getStringArray(R.array.dungluong));
        data();

        productImage.setOnClickListener(v -> {
            intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
        });

        // Xử lý sự kiện click button "Lưu":
        btnSave.setOnClickListener(v -> {
            checkSave = true;
            if (listProduct.size() > 0) {
                DAOProduct dao = new DAOProduct();
                for (Product product : listProduct) {
                    dao.add(product).addOnFailureListener(e -> checkSave = false);
                }
            }
            if (checkSave) {
                showSuccesDialog("Thêm sản phẩm thành công!");
            } else {
                showWarningDialog("Thêm sản phẩm thất bại!");
            }
        });

        // Xử lý sự kiện click button "+":
        btnAdd.setOnClickListener(v -> {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String id = String.valueOf(productID.getText());
            checkTrungID(id);
            if (checkError() == 1) {
                handler.postDelayed(() -> {
                    if (check && check1) {
                        Product product = new Product();
                        product.setSold(0);
                        product.setRating(0);
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
                        DatabaseReference ref = db.getReference("Manufactures");
                        Manufacture manu = (Manufacture) spinManu.getSelectedItem();
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot node : snapshot.getChildren()) {
                                    Manufacture temp = node.getValue(Manufacture.class);
                                    if (temp.getName() == manu.getName()) {
                                        product.setManu_id(node.getKey());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Category cate = (Category) spinCata.getSelectedItem();
                        ref = db.getReference("Categories");
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot node : snapshot.getChildren()) {
                                    Category temp = node.getValue(Category.class);
                                    if (temp.getName() == cate.getName()) {
                                        product.setCategory_id(node.getKey());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        product.setStatus(0);
                        product.setRating(0);
                        product.setImage(product.getName() + ".jpg");
                        product.setQuantity(Integer.parseInt(String.valueOf(productQuantity.getText())));
                        product.setImport_price(Integer.parseInt(String.valueOf(productImportPrice.getText())));
                        product.setPrice(Integer.parseInt(String.valueOf(productPrice.getText())));
                        product.setDescription(String.valueOf(productDescription.getText()));

                        //upload ảnh
                        StorageReference storageRef = storage.getReference("images/products");
                        StorageReference productRef = storageRef.child(product.getName()).child(product.getName() + ".jpg");
                        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
                        productRef.putFile(imageUri, metadata).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    listProduct.add(product);
                                    proAdapter.notifyDataSetChanged();
                                    clear();
                                }
                            }
                        });
                    }
                }, 200);
            }
        });

        // Xử lý sự kiện click button "Huỷ":
        btnCancel.setOnClickListener(v -> finish());

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        proAdapter = new Product1Adapter(listProduct, this);
        proAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(proAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        // AutoCompleteTextView
        ArrayAdapter<String> adapterColor = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, colors);
        spinColor.setAdapter(adapterColor);
        ArrayAdapter<String> adapterSize = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, sizes);
        spinStorage.setAdapter(adapterSize);

        spinColor.setOnClickListener(view -> spinColor.showDropDown());

        spinStorage.setOnClickListener(view -> spinStorage.showDropDown());
    }

    public int checkError() {
        //Check chưa chọn image
        if (imageUri == null) {
            showWarningDialog("Vui lòng chọn ảnh cho sản phẩm!");
            return -1;
        }
        //Check mã sản phẩm trống
        if (String.valueOf(productID.getText()).compareTo("") == 0) {
            showWarningDialog("Mã sản phẩm không được để trống!");
            if (productID.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check tên sản phẩm trống
        if (String.valueOf(productName.getText()).compareTo("") == 0) {
            showWarningDialog("Tên sản phẩm không được để trống!");
            if (productName.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check mô tả sản phẩm trống
        if (String.valueOf(productDescription.getText()).compareTo("") == 0) {
            showWarningDialog("Mô tả phẩm không được để trống!");
            if (productDescription.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check Số lượng sản phẩm trống
        if (String.valueOf(productQuantity.getText()).compareTo("") == 0) {
            showWarningDialog("Số lượng phẩm không được để trống!");
            if (productQuantity.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check số lượng sản phẩm < 0
        if (Integer.parseInt(String.valueOf(productQuantity.getText())) <= 0) {
            showWarningDialog("Số lượng sản phẩm phải lớn hơn 0!");
            if (productQuantity.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá nhập sản phẩm trống
        if (String.valueOf(productImportPrice.getText()).compareTo("") == 0) {
            showWarningDialog("Giá nhập sản phẩm không được để trống!");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá nhập sản phẩm < 0
        if (Integer.parseInt(String.valueOf(productImportPrice.getText())) <= 0) {
            showWarningDialog("Giá nhập sản phẩm phải lớn hơn 0!");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá bán sản phẩm trống
        if (String.valueOf(productPrice.getText()).compareTo("") == 0) {
            showWarningDialog("Giá bán sản phẩm không được để trống!");
            if (productPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá bản < giá nhập
        if (Integer.parseInt(String.valueOf(productImportPrice.getText())) >= Integer.parseInt(String.valueOf(productPrice.getText()))) {
            showWarningDialog("Giá bán sản phẩm phải lớn hơn giá nhập!");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        return 1;
    }

    private void checkTrungID(String id) {
        check1 = true;
        //Check trùng mã sản phẩm
        DatabaseReference ref = db.getReference("Products");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                check = true;
                for (DataSnapshot node : snapshot.getChildren()) {
                    Product temp = node.getValue(Product.class);
                    if (temp.getId().equals(id) && temp.getStatus() != -1) {
                        check = false;
                        showWarningDialog("Mã sản phẩm đã tồn tại!");
                        if (productID.requestFocus()) {
                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        }
                        productID.setText("");
                        break;
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        for (Product product : listProduct) {
            if (product.getId().equals(id)) {
                showWarningDialog("Mã sản phẩm đã tồn tại!");
                if (productID.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                productID.setText("");
                check1 = false;
                break;
            }
        }
    }

    @SuppressLint("NewApi")
    private Product1Adapter.ItemClickListener itemClickListener = new Product1Adapter.ItemClickListener() {
        @Override
        public void getInfor(String key) {
            listProduct.removeIf(listProduct -> listProduct.getId().equals(key));
            proAdapter.notifyDataSetChanged();
        }
    };

    private void data() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference refManu = db.getReference("Manufactures");
        DatabaseReference refCate = db.getReference("Categories");
        listManu = new ArrayList<Manufacture>();
        listCate = new ArrayList<Category>();

        ArrayAdapter manuAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, listManu);

        ArrayAdapter cateAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, listCate);

        manuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinManu.setAdapter(manuAdapter);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCata.setAdapter(cateAdapter);
        refManu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Manufacture manu = node.getValue(Manufacture.class);
                    listManu.add(manu);
                    manuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        refCate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Category cate = node.getValue(Category.class);
                    listCate.add(cate);
                    cateAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void clear() {
        productImage.setImageResource(R.mipmap.ic_launcher_round);
        productID.setText("");
        productName.setText("");
        productDescription.setText("");
        productQuantity.setText("");
        productImportPrice.setText("");
        productPrice.setText("");
        imageUri = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
    }

    private void showWarningDialog(String notify) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DetailProductActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailProductActivity.this).inflate(
                R.layout.layout_warning_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.yes));

        final android.app.AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
        });


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailProductActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailProductActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(s);
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
