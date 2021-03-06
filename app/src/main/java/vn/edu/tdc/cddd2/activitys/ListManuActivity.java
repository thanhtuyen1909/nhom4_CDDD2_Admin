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
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.ManuAdapter;
import vn.edu.tdc.cddd2.data_models.AccountHistory;
import vn.edu.tdc.cddd2.data_models.DetailPromoCode;
import vn.edu.tdc.cddd2.data_models.Manufacture;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListManuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Handler handler = new Handler();
    Toolbar toolbar;
    SearchView searchView;
    TextView btnBack, subtitleAppbar, totalManu, title, mess, txtName, txtRole;
    String username = "", name = "", role = "", img = "", accountID = "";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Manufacture> listManu;
    private ManuAdapter manuAdapter;
    NavigationView navigationView;
    private Intent intent;
    Button btnAdd;
    DatabaseReference manuRef = FirebaseDatabase.getInstance().getReference("Manufactures");
    DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("Products");
    boolean check = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_manu);

        intent = getIntent();
        username = intent.getStringExtra("username");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        accountID = intent.getStringExtra("accountID");
        img = intent.getStringExtra("image");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleLayoutH);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        totalManu = findViewById(R.id.totalManu);
        searchView = findViewById(R.id.editSearch);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> {
            intent = new Intent(ListManuActivity.this, MainQLKActivity.class);
            intent.putExtra("accountID", accountID);
            intent.putExtra("username", username);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("image", img);
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện click button "+":
        btnAdd.setOnClickListener(v -> {
            intent = new Intent(ListManuActivity.this, DetailInformationActivity.class);
            intent.putExtra("to", "ListManu");
            intent.putExtra("accountID", accountID);
            startActivity(intent);
        });

        //RecycleView
        recyclerView = findViewById(R.id.listManu);
        recyclerView.setHasFixedSize(true);
        listManu = new ArrayList<>();
        data();
        manuAdapter = new ManuAdapter(listManu, this);
        manuAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(manuAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        ImageView iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);

        // Xử lý sự kiện thay đổi dữ liệu searchview:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                manuAdapter.getFilter().filter(newText);
                handler.postDelayed(() -> {
                    totalManu.setText(manuAdapter.getItemCount() + " hãng từ " + listManu.size());
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

    private void data() {
        manuRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listManu.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Manufacture manufacture = new Manufacture(snapshot.getKey(), snapshot.child("name").getValue(String.class), snapshot.child("image").getValue(String.class));
                    listManu.add(manufacture);
                }
                manuAdapter.notifyDataSetChanged();
                totalManu.setText(recyclerView.getAdapter().getItemCount() + " hãng từ " + listManu.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private final ManuAdapter.ItemClickListener itemClickListener = new ManuAdapter.ItemClickListener() {
        @Override
        public void deleteManufacture(String key) {
            showWarningDialog(key);
        }

        @Override
        public void editManufacture(Manufacture item) {
            intent = new Intent(ListManuActivity.this, DetailInformationActivity.class);
            intent.putExtra("to", "ListManu");
            intent.putExtra("itemManu", item);
            intent.putExtra("accountID", accountID);
            startActivity(intent);
        }
    };

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
                intent = new Intent(ListManuActivity.this, ListProductActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlkm:
                intent = new Intent(ListManuActivity.this, ListPromoActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dph:
                intent = new Intent(ListManuActivity.this, OrderCoordinationActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(ListManuActivity.this, ListDiscountCodeActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListManuActivity.this, ListCateActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListManuActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListManuActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Xử lý sự kiện xoá:
    private void showWarningDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListManuActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListManuActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận xoá hãng?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            check = true;
            proRef.orderByChild("manu_id").equalTo(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        showErrorDialog("Không thể xoá hãng còn sản phẩm!");
                    } else {
                        manuRef.child(key).removeValue();
                        showSuccesDialog();
                        pushAccountHistory("Xóa hãng", "Hãng " + name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            alertDialog.dismiss();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    // Xử lý sự kiện thông báo xoá thành công:
    private void showSuccesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListManuActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListManuActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xoá hãng thành công!");
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showErrorDialog(String notify) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ListManuActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListManuActivity.this).inflate(
                R.layout.layout_warning_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.yes));

        final android.app.AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void pushAccountHistory(String action, String detail) {
        // Thêm vào "Lịch sử hoạt động"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setAccountID(accountID);
        accountHistory.setAction(action);
        accountHistory.setDetail(detail);
        accountHistory.setDate(sdf.format(new Date()));
        FirebaseDatabase.getInstance().getReference("AccountHistory").push().setValue(accountHistory);
    }

}
