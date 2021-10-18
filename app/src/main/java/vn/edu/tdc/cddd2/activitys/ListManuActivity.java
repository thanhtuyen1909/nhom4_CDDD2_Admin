package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import vn.edu.tdc.cddd2.adapters.ManuAdapter;
import vn.edu.tdc.cddd2.data_models.Category;
import vn.edu.tdc.cddd2.data_models.Manufacture;

public class ListManuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar, totalManu;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Manufacture> listManu;
    private ManuAdapter manuAdapter;
    private NavigationView navigationView;
    private Intent intent;
    private Button btnAdd;
    DatabaseReference manuRef;
    TextView title, mess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_manu);
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách hãng");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        manuRef = FirebaseDatabase.getInstance().getReference("Manufactures");
        totalManu = findViewById(R.id.totalManu);

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
                intent = new Intent(ListManuActivity.this, DetailInformationActivity.class);
                intent.putExtra("to", "ListManu");
                startActivity(intent);
            }
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data() {
        manuRef.addValueEventListener(new ValueEventListener() {
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

    private ManuAdapter.ItemClickListener itemClickListener = new ManuAdapter.ItemClickListener() {
        @Override
        public void deleteManufacture(String key) {
            showWarningDialog(key);
        }

        @Override
        public void editManufacture(Manufacture item) {
            intent = new Intent(ListManuActivity.this, DetailInformationActivity.class);
            intent.putExtra("to", "ListManu");
            intent.putExtra("itemManu", (Parcelable) item);
            startActivity(intent);
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
                intent = new Intent(ListManuActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                intent = new Intent(ListManuActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
                intent = new Intent(ListManuActivity.this, OrderCoordinationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(ListManuActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListManuActivity.this, ListCateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListManuActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListManuActivity.this, LoginActivity.class);
                startActivity(intent);
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

    // Xử lý sự kiện xoá hãng:
    private void showWarningDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListManuActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListManuActivity.this).inflate(
                R.layout.layout_error_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText("THÔNG BÁO");
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận xoá hãng?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                manuRef.child(key).removeValue();
                showSuccesDialog();
            }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(ListManuActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListManuActivity.this).inflate(
                R.layout.layout_succes_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = (TextView) view.findViewById(R.id.textTitle);
        title.setText("THÔNG BÁO");
        mess = (TextView) view.findViewById(R.id.textMessage);
        mess.setText("Xoá hãng thành công!");
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
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
}
