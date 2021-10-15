package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
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

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.AccountAdapter;
import vn.edu.tdc.cddd2.adapters.Product6Adapter;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListRatingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private Button btnAdd;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Product> listProduct;
    private NavigationView navigationView;
    private Product6Adapter product6Adapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_rating);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Đánh giá & bình luận");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện click button "+":
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListRatingActivity.this, "Thêm tài khoản", Toast.LENGTH_SHORT).show();
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listRating);
        recyclerView.setHasFixedSize(true);
        listProduct = new ArrayList<>();
        data();
        product6Adapter = new Product6Adapter(listProduct, this);
        product6Adapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(product6Adapter);
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

    private void data() {
        listProduct.add(new Product("Laptop 1", 15000000, "Asus", 10));
        listProduct.add(new Product("Laptop 2", 14000000, "Acer", 11));
        listProduct.add(new Product("Laptop 3", 12000000, "Apple", 12));
    }

    private Product6Adapter.ItemClickListener itemClickListener = new Product6Adapter.ItemClickListener() {
        @Override
        public void getInfor(Product item) {
            Toast.makeText(ListRatingActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                intent = new Intent(ListRatingActivity.this, ListAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_lsdh:
                intent = new Intent(ListRatingActivity.this, OrderHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlhd:
                intent = new Intent(ListRatingActivity.this, ListInvoiceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_tk:
                intent = new Intent(ListRatingActivity.this, RevenueStatisticActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlbl:
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListRatingActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListRatingActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListRatingActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
}
