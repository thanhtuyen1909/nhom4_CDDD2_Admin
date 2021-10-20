package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import vn.edu.tdc.cddd2.adapters.Product2Adapter;
import vn.edu.tdc.cddd2.adapters.ProductAdapter;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListProductSMActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Khai báo biến
    private Toolbar toolbar;
    private ImageView btnCart;
    private Spinner spinCata, spinManu;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Product> listProduct;
    private NavigationView navigationView;
    private Product2Adapter proAdapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_product_sm);
        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnCart = findViewById(R.id.btnCart);

        // Xử lý sự kiện click button "Giỏ hàng":
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ListProductSMActivity.this, ListCartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listProduct = new ArrayList<>();
        data();
        proAdapter = new Product2Adapter(listProduct,this);
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
//        listProduct.add(new Product("Laptop 1", 15000000, "Asus", 10));
//        listProduct.add(new Product("Laptop 2", 14000000, "Acer", 11));
//        listProduct.add(new Product("Laptop 3", 12000000, "Apple", 12));
//        listProduct.add(new Product("Laptop 4", 16000000, "Acer", 11));
//        listProduct.add(new Product("Laptop 5", 12000000, "Asus", 10));
    }

    private Product2Adapter.ItemClickListener itemClickListener = new Product2Adapter.ItemClickListener() {
        @Override
        public void getInfor(Product item) {
            Toast.makeText(ListProductSMActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_dmk:
                intent = new Intent(ListProductSMActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListProductSMActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListProductSMActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
