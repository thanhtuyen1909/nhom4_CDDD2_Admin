package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.ProductPromoAdapter;
import vn.edu.tdc.cddd2.data_models.DetailPromoCode;
import vn.edu.tdc.cddd2.data_models.Product;
import vn.edu.tdc.cddd2.data_models.PromoCode;

public class DetailPromoCodeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    // Khai báo biến
    Handler handler = new Handler();
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnCancel, title, mess;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    Intent intent;
    RecyclerView recyclerView;
    ArrayList<DetailPromoCode> listProduct;
    ArrayList<Product> listP;
    private Spinner spinProduct, spinGiamGia;
    ProductPromoAdapter productAdapter;
    Button btnAdd;
    String key = "-MmLuVjl6Gg64YK9FEft", keyPD = null, remove;
    boolean check = true;
    ArrayAdapter<Product> spinAdapter;
    DatabaseReference promoDetailRef = FirebaseDatabase.getInstance().getReference("Offer_Details");
    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_promocode);
        promoDetailRef.keepSynced(true);
        productRef.keepSynced(true);

        //key = getIntent().getStringExtra("key");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleLayoutCTKM);
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
        listProduct = new ArrayList<>();
        listP = new ArrayList<>();
        spinProduct = findViewById(R.id.edtSP);
        spinGiamGia = findViewById(R.id.edtPKM);

        // Xử lý sự kiện click button:
        btnSave.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        productAdapter = new ProductPromoAdapter(listProduct, this);
        productAdapter.setItemClickListener(itemClickListener);
        spinAdapter = new ArrayAdapter<Product>(this, android.R.layout.simple_spinner_item, listP);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinProduct.setAdapter(spinAdapter);
        data();
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private final ProductPromoAdapter.ItemClickListener itemClickListener = new ProductPromoAdapter.ItemClickListener() {
        @SuppressLint("NewApi")
        @Override
        public void deleteProductInPromo(String s) {
            listProduct.removeIf(listProduct -> listProduct.getProductID().equals(s));
            productAdapter.notifyDataSetChanged();
        }

        @Override
        public void editProductInPromo(String key) {
            Toast.makeText(DetailPromoCodeActivity.this, key, Toast.LENGTH_SHORT).show();
        }
    };

    private void data() {
        // List:
        promoDetailRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProduct.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("offerID").getValue(String.class).equals(key)) {
                        DetailPromoCode dp = snapshot.getValue(DetailPromoCode.class);
                        dp.setKey(snapshot.getKey());
                        listProduct.add(dp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Spinner:
        productRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listP.clear();
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    Product product = snapshot1.getValue(Product.class);
                    product.setKey(snapshot1.getKey());
                    listP.add(product);
                }
                spinAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                intent = new Intent(DetailPromoCodeActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                intent = new Intent(DetailPromoCodeActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
                intent = new Intent(DetailPromoCodeActivity.this, OrderCoordinationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(DetailPromoCodeActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(DetailPromoCodeActivity.this, ListCateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(DetailPromoCodeActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(DetailPromoCodeActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(DetailPromoCodeActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(DetailPromoCodeActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
            // Xoá hết detail trước đó trên dtb
            promoDetailRef.addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("offerID").getValue(String.class).equals(key)) {
                            promoDetailRef.child(snapshot.getKey()).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            // Thêm lại list lên dtb
            for(DetailPromoCode dp : listProduct) {
                DetailPromoCode dp1 = new DetailPromoCode();
                dp1.setOfferID(dp.getOfferID());
                dp1.setPercentSale(dp.getPercentSale());
                dp1.setProductID(dp.getProductID());
                promoDetailRef.push().setValue(dp1);
            }
            showSuccesDialog("Cập nhật chi tiết khuyến mãi thành công!");
        }
        else if (v == btnAdd) {
            // Kiểm tra trùng sp
            keyPD = ((Product) spinProduct.getSelectedItem()).getKey();
            Integer percent = Integer.parseInt(spinGiamGia.getSelectedItem().toString().substring(0, spinGiamGia.getSelectedItem().toString().length() - 1));
            checkTrung(keyPD);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Add xuống list
                    if (check) {
                        DetailPromoCode dp = new DetailPromoCode();
                        dp.setOfferID(key);
                        dp.setPercentSale(percent);
                        dp.setProductID(keyPD);
                        listProduct.add(dp);
                        productAdapter.notifyDataSetChanged();
                    } else {
                        showWarningDialog("Khuyến mãi đã được áp dụng trên sản phẩm này!");
                    }
                }
            }, 200);
        }
        else {
            showErrorDialog();
        }
    }

    // Thông báo:
    private void showWarningDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailPromoCodeActivity.this).inflate(
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

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailPromoCodeActivity.this).inflate(
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
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailPromoCodeActivity.this).inflate(
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
            //finish();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void checkTrung(String s) {
        check = true;
        for (DetailPromoCode p : listProduct) {
            if(p.getProductID().equals(s)) {
                check = false;
                break;
            }
        }
    }
}
