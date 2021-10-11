package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import vn.edu.tdc.cddd2.adapters.CataAdapter;
import vn.edu.tdc.cddd2.adapters.ManuAdapter;
import vn.edu.tdc.cddd2.data_models.Catagory;
import vn.edu.tdc.cddd2.data_models.Manufacturer;

public class ListCataActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Catagory> lisCata;
    private CataAdapter cataAdapter;
    private NavigationView navigationView;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_manu);
        // Khởi tạo biến
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách loại sản phẩm");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //RecycleView
        recyclerView = findViewById(R.id.listManu);
        recyclerView.setHasFixedSize(true);
        lisCata = new ArrayList<>();
        data();
        cataAdapter = new CataAdapter(lisCata,this);
        cataAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(cataAdapter);
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
        lisCata.add(new Catagory("Tivi"));
        lisCata.add(new Catagory("Tủ lạnh"));
        lisCata.add(new Catagory("Laptop"));
        lisCata.add(new Catagory("Máy tính bảng"));
        lisCata.add(new Catagory("Điện thoại"));
    }

    private CataAdapter.ItemClickListener itemClickListener = new CataAdapter.ItemClickListener() {
        @Override
        public void getInfor(Catagory item) {
            Toast.makeText(ListCataActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlsp:
                intent = new Intent(ListCataActivity.this, ListProductActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListCataActivity.this, "Quản lý khuyến mãi", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dph:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListCataActivity.this, "Điều phối hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlmgg:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListCataActivity.this, "Quản lý mã giảm giá", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qllsp:
                break;
            case R.id.nav_dmk:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListCataActivity.this, "Đổi mật khẩu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_dx:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListCataActivity.this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlh:
                intent = new Intent(ListCataActivity.this, ListManuActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListCataActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
