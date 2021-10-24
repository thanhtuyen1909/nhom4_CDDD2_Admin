package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Date;

import vn.edu.tdc.cddd2.DAO.DAOProduct;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.Product1Adapter;
import vn.edu.tdc.cddd2.data_models.Category;
import vn.edu.tdc.cddd2.data_models.Manufacture;
import vn.edu.tdc.cddd2.data_models.Product;

public class DetailProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Handler handler = new Handler();
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnCancel, title, mess;
    private ImageView productImage;
    private EditText productID, productName, productDescription, productQuantity, productImportPrice, productPrice;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    private Intent intent;
    RecyclerView recyclerView;
    private ArrayList<Product> listProduct;
    private Spinner spinCata, spinManu, spinColor, spinStorage;
    private Product1Adapter proAdapter;
    Button btnAdd;
    static int SELECT_IMAGE_CODE = 1;
    private Uri imageUri;
    private ArrayList<Manufacture> listManu;
    private ArrayList<Category> listCate;
    static FirebaseDatabase db = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/");
    boolean check = true, check1 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_product);
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.title9);
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
        data();

        productImage.setOnClickListener(v -> {
            intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Title"), SELECT_IMAGE_CODE);
        });

        // Xử lý sự kiện click button "Lưu":
        btnSave.setOnClickListener(v -> {
            check = true;
            if (listProduct.size() > 0) {
                DAOProduct dao = new DAOProduct();
                for (Product product : listProduct) {
                    dao.add(product).addOnFailureListener(e -> check = false);
                }
            }
            if (check) {
                showSuccesDialog("Thêm sản phẩm thành công!");
                listProduct.clear();
            } else {
                showErrorDialog("Thêm sản phẩm thất bại!");
            }
        });

        // Xử lý sự kiện click button "+":
        btnAdd.setOnClickListener(v -> {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            int id = Integer.parseInt(String.valueOf(productID.getText()));
            checkTrungID(id);

            handler.postDelayed(new Runnable() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void run() {
                    if (checkError() == 1 && check && check1) {
                        Product product = new Product();
                        product.setSold(0);
                        product.setCreated_at(formatter.format(now));
                        product.setId(Integer.parseInt(String.valueOf(productID.getText())));
                        String color = String.valueOf(spinColor.getSelectedItem());
                        String size = String.valueOf(spinStorage.getSelectedItem());
                        product.setName(String.valueOf(productName.getText()));
                        if (color != "") {
                            product.setName(product.getName() + " - " + color);
                        }
                        if (size != null) {
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
                                        Log.d("TAG", "onDataChange: " + node.getKey());
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
                                        Log.d("TAG", "onDataChange: " + node.getKey());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        product.setImage(product.getName() + ".jpg");
                        product.setQuantity(Integer.parseInt(String.valueOf(productQuantity.getText())));
                        product.setImport_price(Integer.parseInt(String.valueOf(productImportPrice.getText())));
                        product.setPrice(Integer.parseInt(String.valueOf(productPrice.getText())));
                        product.setDescription(String.valueOf(productDescription.getText()));

                        //upload ảnh
                        FirebaseStorage storage = FirebaseStorage.getInstance("gs://cddd2-f1bcd.appspot.com/");
                        StorageReference storageRef = storage.getReference("images/products");
                        StorageReference productRef = storageRef.child(product.getName()).child(product.getName() + ".jpg");
                        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
                        productRef.putFile(imageUri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("image", taskSnapshot.getMetadata().getName() + "." + taskSnapshot.getMetadata().getContentType());
                            }
                        });
                        listProduct.add(product);
                        proAdapter.notifyDataSetChanged();
                        clear();
                    }
                }
            }, 200);
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
    }

    public int checkError() {
        //Check chưa chọn image
        if (imageUri == null) {
            showErrorDialog("Vui lòng chọn ảnh cho sản phẩm!");
            return -1;
        }
        //Check mã sản phẩm trống
        if (String.valueOf(productID.getText()).compareTo("") == 0) {
            showErrorDialog("Mã sản phẩm không được để trống!");
            if (productID.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check tên sản phẩm trống
        if (String.valueOf(productName.getText()).compareTo("") == 0) {
            showErrorDialog("Tên sản phẩm không được để trống!");
            if (productName.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check mô tả sản phẩm trống
        if (String.valueOf(productDescription.getText()).compareTo("") == 0) {
            showErrorDialog("Mô tả phẩm không được để trống!");
            if (productDescription.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check Số lượng sản phẩm trống
        if (String.valueOf(productQuantity.getText()).compareTo("") == 0) {
            showErrorDialog("Số lượng phẩm không được để trống!");
            if (productQuantity.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check số lượng sản phẩm < 0
        if (Integer.parseInt(String.valueOf(productQuantity.getText())) < 0) {
            showErrorDialog("Số lượng sản phẩm phải lớn hơn hoặc bằng 0!");
            if (productQuantity.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá nhập sản phẩm trống
        if (String.valueOf(productImportPrice.getText()).compareTo("") == 0) {
            showErrorDialog("Giá nhập sản phẩm không được để trống!");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá nhập sản phẩm < 0
        if (Integer.parseInt(String.valueOf(productImportPrice.getText())) < 0) {
            showErrorDialog("Giá nhập sản phẩm phải lớn hơn 0!");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá bán sản phẩm trống
        if (String.valueOf(productPrice.getText()).compareTo("") == 0) {
            showErrorDialog("Giá bán sản phẩm không được để trống!");
            if (productPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        //Check giá bản < giá nhập
        if (Integer.parseInt(String.valueOf(productImportPrice.getText())) > Integer.parseInt(String.valueOf(productImportPrice.getText()))) {
            showErrorDialog("Giá bán sản phẩm phải lớn hơn giá nhập!");
            if (productImportPrice.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            return -1;
        }
        return 1;
    }

    private void checkTrungID(int id) {
        check1 = true;
        //Check trùng mã sản phẩm
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference ref = db.getReference("Products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                check = true;
                for (DataSnapshot node : snapshot.getChildren()) {
                    Product temp = node.getValue(Product.class);
                    if (temp.getId() == id) {
                        check = false;
                        showErrorDialog("Mã sản phẩm không được trùng!");

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
            if (product.getId() == id) {
                showErrorDialog("Mã sản phẩm không được trùng!");
                if (productID.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                productID.setText("");
                check1 = false;
            }
        }
    }

    private Product1Adapter.ItemClickListener itemClickListener = new Product1Adapter.ItemClickListener() {
        @Override
        public void getInfor(Product item) {
            Toast.makeText(DetailProductActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri);
        }
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlsp:
                intent = new Intent(DetailProductActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                intent = new Intent(DetailProductActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
                intent = new Intent(DetailProductActivity.this, OrderProcessActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(DetailProductActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(DetailProductActivity.this, ListCataActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(DetailProductActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(DetailProductActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(DetailProductActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(DetailProductActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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

    private void showErrorDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailProductActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailProductActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("s");
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

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
