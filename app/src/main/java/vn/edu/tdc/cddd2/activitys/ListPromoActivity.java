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

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.PromoCodeAdapter;
import vn.edu.tdc.cddd2.data_models.PromoCode;

public class ListPromoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private Button btnAdd;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<PromoCode> listPromoCode;
    private NavigationView navigationView;
    private PromoCodeAdapter promoCodeAdapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_promocode);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách khuyến mãi");
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
                intent = new Intent(ListPromoActivity.this, InformationPromoCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listPromoCode);
        recyclerView.setHasFixedSize(true);
        listPromoCode = new ArrayList<>();
        data();
        promoCodeAdapter = new PromoCodeAdapter(listPromoCode,this);
        promoCodeAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(promoCodeAdapter);
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
        listPromoCode.add(new PromoCode("Back to school", "13/08/2021", "13/09/2021"));
        listPromoCode.add(new PromoCode("Back to school", "13/08/2021", "13/09/2021"));
        listPromoCode.add(new PromoCode("Back to school", "13/08/2021", "13/09/2021"));
        listPromoCode.add(new PromoCode("Back to school", "13/08/2021", "13/09/2021"));
        listPromoCode.add(new PromoCode("Back to school", "13/08/2021", "13/09/2021"));
    }

    private PromoCodeAdapter.ItemClickListener itemClickListener = new PromoCodeAdapter.ItemClickListener() {
        @Override
        public void getInfor(PromoCode item) {
            Toast.makeText(ListPromoActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void getLayoutAddDetailPromoCode() {
            intent = new Intent(ListPromoActivity.this, DetailPromoCodeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlsp:
                intent = new Intent(ListPromoActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                break;
            case R.id.nav_dph:
//                Intent intent = new Intent(ListProductActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListPromoActivity.this, "Điều phối hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(ListPromoActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListPromoActivity.this, ListCataActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListPromoActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListPromoActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(ListPromoActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListPromoActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
