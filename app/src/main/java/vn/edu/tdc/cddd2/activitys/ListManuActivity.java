package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
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
import vn.edu.tdc.cddd2.adapters.ManuAdapter;
import vn.edu.tdc.cddd2.adapters.ProductAdapter;
import vn.edu.tdc.cddd2.data_models.Manufacturer;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListManuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Manufacturer> listManu;
    private ManuAdapter manuAdapter;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_manu);
        // Khởi tạo biến
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách hãng");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listManu = new ArrayList<>();
        data();
        manuAdapter = new ManuAdapter(listManu,this);
        manuAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(manuAdapter);
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
        listManu.add(new Manufacturer("Asus"));
        listManu.add(new Manufacturer("Acer"));
        listManu.add(new Manufacturer("Apple"));
        listManu.add(new Manufacturer("Acer"));
        listManu.add(new Manufacturer("Asus"));
    }

    private ManuAdapter.ItemClickListener itemClickListener = new ManuAdapter.ItemClickListener() {
        @Override
        public void getInfor(Manufacturer item) {
            Toast.makeText(ListManuActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlsp:
                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListManuActivity.this, "Quản lý khuyến mãi", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dph:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListManuActivity.this, "Điều phối hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlmgg:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListManuActivity.this, "Quản lý mã giảm giá", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qllsp:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListManuActivity.this, "Quản lý loại sản phẩm", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dmk:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListManuActivity.this, "Đổi mật khẩu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dx:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListManuActivity.this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlh:
                break;
            default:
                Toast.makeText(ListManuActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
