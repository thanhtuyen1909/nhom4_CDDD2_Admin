package vn.edu.tdc.cddd2.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.ProductAdapter;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack;
    private Spinner spinCata, spinManu;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Product> listitem;
    private ProductAdapter proAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_product);
        // Khởi tạo biến
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listitem = new ArrayList<>();
        data();
        proAdapter = new ProductAdapter(listitem,this);
        proAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(proAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data(){
        listitem.add(new Product("Laptop 1", 15000000, "Asus", 10));
        listitem.add(new Product("Laptop 2", 14000000, "Acer", 11));
        listitem.add(new Product("Laptop 3", 12000000, "Apple", 12));
        listitem.add(new Product("Laptop 4", 16000000, "Acer", 11));
        listitem.add(new Product("Laptop 5", 12000000, "Asus", 10));
    }

    private ProductAdapter.ItemClickListener itemClickListener = new ProductAdapter.ItemClickListener() {
        @Override
        public void getInfor(Product item) {
            Toast.makeText(ListProductActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Toast.makeText(ListProductActivity.this, id, Toast.LENGTH_SHORT).show();
        switch (id) {
            case R.id.nav_qlsp:
                break;
            case R.id.nav_qlkm:
//                Intent intent = new Intent(ListProductActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListProductActivity.this, "Quản lý khuyến mãi", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dph:
//                Intent intent = new Intent(ListProductActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListProductActivity.this, "Điều phối hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlmgg:
//                Intent intent = new Intent(ListProductActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListProductActivity.this, "Quản lý mã giảm giá", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qllsp:
//                Intent intent = new Intent(ListProductActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListProductActivity.this, "Quản lý loại sản phẩm", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dmk:
//                Intent intent = new Intent(ListProductActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListProductActivity.this, "Đổi mật khẩu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dx:
//                Intent intent = new Intent(ListProductActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListProductActivity.this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlh:
                Intent intent = new Intent(ListProductActivity.this, ListManuActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListProductActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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