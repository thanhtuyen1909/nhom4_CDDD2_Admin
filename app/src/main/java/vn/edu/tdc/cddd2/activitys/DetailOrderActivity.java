package vn.edu.tdc.cddd2.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.Product5Adapter;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.OrderDetail;
import vn.edu.tdc.cddd2.data_models.Product;

public class DetailOrderActivity extends AppCompatActivity {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, txtMaDH, txtCreatedAt, txtStatus, txtNote, txtName, txtPhone, txtTotal, txtAddress;
    RecyclerView recyclerView;
    ArrayList<Product> listProducts;
    Product5Adapter product5Adapter;
    Intent intent;
    Order item = null;
    String from = "", accountID = "", username = "";

    DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Status");
    DatabaseReference order_detailRef = FirebaseDatabase.getInstance().getReference("Order_Details");
    DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("Products");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_order);
        item = (Order) getIntent().getParcelableExtra("item");
        from = getIntent().getStringExtra("from");
        accountID = getIntent().getStringExtra("accountID");
        username = getIntent().getStringExtra("username");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleDetaiCTDH);

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        txtMaDH = findViewById(R.id.txt_madonhang);
        txtCreatedAt = findViewById(R.id.txt_ngaytao);
        txtStatus = findViewById(R.id.txt_trangthai);
        txtNote = findViewById(R.id.txt_ghichu);
        txtName = findViewById(R.id.txt_htkh);
        txtPhone = findViewById(R.id.txt_sdt);
        txtTotal = findViewById(R.id.txt_tongtien);
        txtAddress = findViewById(R.id.txt_diachi);

        // Đổ dữ liệu
        if(item != null) {
            txtPhone.setText(item.getPhone());
            txtMaDH.setText(item.getOrderID());
            txtCreatedAt.setText("Ngày tạo: " + item.getCreated_at());
            statusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(Integer.parseInt(snapshot.getKey()) == item.getStatus()) {
                            txtStatus.setText("Trạng thái: " + snapshot.getValue(String.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            txtNote.setText("Ghi chú: " + item.getNote());
            txtName.setText("Họ tên khách hàng: " + item.getName());
            txtTotal.setText("Tổng tiền: " + formatPrice(item.getTotal()));
            txtAddress.setText("Địa chỉ: " + item.getAddress());
        }

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> {
            if(!from.equals("") && from.equals("PaymentSM")) {
                intent = new Intent(DetailOrderActivity.this, ListProductSMActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                startActivity(intent);
            }
            finish();
        });

        // Xử lý sự kiện click phone:
        txtPhone.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + txtPhone.getText()));
            if (ActivityCompat.checkSelfPermission(DetailOrderActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        });

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listProducts = new ArrayList<>();
        data();
        product5Adapter = new Product5Adapter(listProducts, this);
        recyclerView.setAdapter(product5Adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void data() {
        order_detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProducts.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    OrderDetail orderDetail = snapshot.getValue(OrderDetail.class);
                    if(orderDetail.getOrderID().equals(item.getOrderID())) {
                        proRef.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    Product product = snapshot1.getValue(Product.class);
                                    product.setPrice(orderDetail.getPrice());
                                    product.setKey(snapshot1.getKey());
                                    product.setQuantity(orderDetail.getAmount());
                                    if(product.getKey().equals(orderDetail.getProductID())) {
                                        listProducts.add(product);
                                    }
                                }
                                product5Adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

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

    private String formatPrice(int price) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
    }
}
