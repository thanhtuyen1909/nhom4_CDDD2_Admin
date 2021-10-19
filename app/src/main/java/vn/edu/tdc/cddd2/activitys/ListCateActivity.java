package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.CateAdapter;
import vn.edu.tdc.cddd2.data_models.Category;

public class ListCateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, totalCate;
    SearchView searchView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    ArrayList<Category> listCate = new ArrayList<>();
    CateAdapter cateAdapter;
    NavigationView navigationView;
    private Intent intent;
    Button btnAdd;
    DatabaseReference cateRef;
    TextView title, mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_cate);
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleLayoutLSP);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        totalCate = findViewById(R.id.totalCate);
        cateRef = FirebaseDatabase.getInstance().getReference("Categories");
        searchView = findViewById(R.id.editSearch);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện click button "+":
        btnAdd.setOnClickListener(v -> {
            intent = new Intent(ListCateActivity.this, DetailInformationActivity.class);
            intent.putExtra("to", "ListCate");
            startActivity(intent);
        });

        //RecycleView
        recyclerView = findViewById(R.id.listCate);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        data();
        cateAdapter = new CateAdapter(listCate, this);
        cateAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(cateAdapter);

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Xử lý sự kiện thay đổi dữ liệu searchview:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cateAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    public void data() {
        cateRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listCate.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Category category = new Category(snapshot.getKey(), snapshot.child("name").getValue(String.class), snapshot.child("image").getValue(String.class));
                    listCate.add(category);
                }
                cateAdapter.notifyDataSetChanged();
                totalCate.setText(recyclerView.getAdapter().getItemCount() + " loại từ " + listCate.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private CateAdapter.ItemClickListener itemClickListener = new CateAdapter.ItemClickListener() {
        @Override
        public void deleteCategory(String key) {
            showWarningDialog(key);
        }

        @Override
        public void editCategory(Category item) {
            intent = new Intent(ListCateActivity.this, DetailInformationActivity.class);
            intent.putExtra("to", "ListCate");
            intent.putExtra("itemCate", (Parcelable) item);
            startActivity(intent);
        }
    };

    private void showWarningDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListCateActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListCateActivity.this).inflate(
                R.layout.layout_error_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận xoá loại sản phẩm?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            cateRef.child(key).removeValue();
            showSuccesDialog();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListCateActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListCateActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xoá loại sản phẩm thành công!");
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
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
                intent = new Intent(ListCateActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                intent = new Intent(ListCateActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
                intent = new Intent(ListCateActivity.this, OrderCoordinationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(ListCateActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListCateActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListCateActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(ListCateActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListCateActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
