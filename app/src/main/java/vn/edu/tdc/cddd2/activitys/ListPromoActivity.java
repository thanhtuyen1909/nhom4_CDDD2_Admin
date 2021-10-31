package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import vn.edu.tdc.cddd2.adapters.PromoCodeAdapter;
import vn.edu.tdc.cddd2.data_models.PromoCode;

public class ListPromoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Khai báo biến
    Handler handler = new Handler();
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, txtName, txtRole;
    String username = "", name = "", role = "";
    SearchView searchView;
    Button btnAdd;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    ArrayList<PromoCode> listPromoCode;
    NavigationView navigationView;
    PromoCodeAdapter promoCodeAdapter;
    private Intent intent;
    DatabaseReference promoRef, promoDetailRef;
    TextView title, mess, totalPromo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_promocode);
        intent = getIntent();
        username = intent.getStringExtra("username");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleLayoutKM);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        promoRef = FirebaseDatabase.getInstance().getReference("Offers");
        promoDetailRef = FirebaseDatabase.getInstance().getReference("Offer_Details");
        totalPromo = findViewById(R.id.totalPromoCode);
        searchView = findViewById(R.id.editSearch);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện click button "+":
        btnAdd.setOnClickListener(v -> {
            intent = new Intent(ListPromoActivity.this, InformationPromoCodeActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
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
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        txtName.setText(name);
        txtRole.setText(role);

        // Xử lý sự kiện thay đổi dữ liệu searchview:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextChange(String newText) {
                promoCodeAdapter.getFilter().filter(newText);
                handler.postDelayed(() -> {
                    totalPromo.setText(recyclerView.getAdapter().getItemCount() + " khuyến mãi từ " + listPromoCode.size());
                }, 200);
                return false;
            }
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data(){
        promoRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPromoCode.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PromoCode promoCode = new PromoCode(
                            snapshot.getKey(),
                            snapshot.child("name").getValue(String.class),
                            snapshot.child("startDate").getValue(String.class),
                            snapshot.child("endDate").getValue(String.class),
                            snapshot.child("image").getValue(String.class));
                    listPromoCode.add(promoCode);
                }
                promoCodeAdapter.notifyDataSetChanged();
                totalPromo.setText(recyclerView.getAdapter().getItemCount() + " khuyến mãi từ " + listPromoCode.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private final PromoCodeAdapter.ItemClickListener itemClickListener = new PromoCodeAdapter.ItemClickListener() {
        @Override
        public void editPromoCode(PromoCode item) {
            intent = new Intent(ListPromoActivity.this, InformationPromoCodeActivity.class);
            intent.putExtra("item", (Parcelable) item);
            intent.putExtra("username", username);
            startActivity(intent);
        }

        @Override
        public void addDetailPromoCode(String key) {
            intent = new Intent(ListPromoActivity.this, DetailPromoCodeActivity.class);
            intent.putExtra("key", key);
            intent.putExtra("username", username);
            startActivity(intent);
        }

        @Override
        public void deletePromoCode(String key) {
            showWarningDialog(key);
        }

    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlsp:
                intent = new Intent(ListPromoActivity.this, ListProductActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlkm:
                break;
            case R.id.nav_dph:
                intent = new Intent(ListPromoActivity.this, OrderCoordinationActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(ListPromoActivity.this, ListDiscountCodeActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListPromoActivity.this, ListCateActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListPromoActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                intent.putExtra("username", username);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListPromoActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlh:
                intent = new Intent(ListPromoActivity.this, ListManuActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
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

    // Xử lý sự kiện xoá:
    private void showWarningDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListPromoActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListPromoActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận xoá khuyến mãi?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            promoRef.child(key).removeValue();
            // Xoá cả những chi tiết khuyến mãi
            promoDetailRef.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("offerID").getValue(String.class).equals(key)) {
                            promoDetailRef.child(snapshot.getKey()).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            showSuccesDialog();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    // Xử lý sự kiện thông báo xoá thành công:
    private void showSuccesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListPromoActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListPromoActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xoá khuyến mãi thành công!");
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
