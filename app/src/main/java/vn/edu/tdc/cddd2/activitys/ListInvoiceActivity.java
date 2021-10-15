package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import vn.edu.tdc.cddd2.adapters.InvoiceAdapter;
import vn.edu.tdc.cddd2.adapters.Order5Adapter;
import vn.edu.tdc.cddd2.data_models.Invoice;
import vn.edu.tdc.cddd2.data_models.Order;

public class ListInvoiceActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Invoice> listInvoices;
    private NavigationView navigationView;
    private InvoiceAdapter invoiceAdapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_invoice);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách hoá đơn");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listInvoice);
        recyclerView.setHasFixedSize(true);
        listInvoices = new ArrayList<>();
        data();
        invoiceAdapter = new InvoiceAdapter(listInvoices, this);
        invoiceAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(invoiceAdapter);
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
        listInvoices.add(new Invoice("HD001", 15000000, "12/10/2021"));
        listInvoices.add(new Invoice("HD002", 14000000, "12/10/2021"));
        listInvoices.add(new Invoice("HD003", 12000000, "12/10/2021"));
        listInvoices.add(new Invoice("HD004", 16000000, "12/10/2021"));
        listInvoices.add(new Invoice("HD005", 12000000, "12/10/2021"));
    }

    private InvoiceAdapter.ItemClickListener itemClickListener = new InvoiceAdapter.ItemClickListener() {
        @Override
        public void getInfor(Invoice item) {
            intent = new Intent(ListInvoiceActivity.this, DetailInvoiceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                intent = new Intent(ListInvoiceActivity.this, ListAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_lsdh:
                intent = new Intent(ListInvoiceActivity.this, OrderHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlhd:
                break;
            case R.id.nav_tk:
                intent = new Intent(ListInvoiceActivity.this, RevenueStatisticActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlbl:
                intent = new Intent(ListInvoiceActivity.this, ListRatingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListInvoiceActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListInvoiceActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListInvoiceActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
