package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.CartDetailAdapter;
import vn.edu.tdc.cddd2.data_models.CartDetail;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListCartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Khai báo biến:
    Toolbar toolbar;
    TextView btnBack;
    Button btnIncrease, btnDecrease, btnThanhToan;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    NavigationView navigationView;
    CartDetailAdapter proAdapter;
    private Intent intent;
    String accountID = "";
    ArrayList<CartDetail> listCart;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference ref = db.getReference("Cart");
    DatabaseReference detailRef = db.getReference("Cart_Detail");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_cart_sm);

        intent = getIntent();
        accountID = intent.getStringExtra("accountID");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnThanhToan = findViewById(R.id.buttonThanhToan);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện click button "Thanh toán":
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ListCartActivity.this, CreateOrderActivity.class);
                //intent.putExtra();
                startActivity(intent);
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listProduct = new ArrayList<>();
        data();
        proAdapter = new CartDetailAdapter(listProduct,this);
        proAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(proAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data(){
    }

    private CartDetailAdapter.ItemClickListener itemClickListener = new CartDetailAdapter.ItemClickListener() {
        @Override
        public void changeValue(Product item) {
            Toast.makeText(ListCartActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_dmk:
                intent = new Intent(ListCartActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListCartActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListCartActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
