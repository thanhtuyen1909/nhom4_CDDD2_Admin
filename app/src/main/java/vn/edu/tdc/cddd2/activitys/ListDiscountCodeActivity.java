package vn.edu.tdc.cddd2.activitys;

import android.app.AlertDialog;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.DiscountCodeAdapter;
import vn.edu.tdc.cddd2.data_models.DiscountCode;

public class ListDiscountCodeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Intent intent;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<DiscountCode> listDiscountCode;
    private DiscountCodeAdapter discountCodeAdapter;
    private Button btnAdd;
    private TextView title,mess,filter;
    private final static FirebaseDatabase db = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_discountcode);
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách mã giảm giá");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        searchView = findViewById(R.id.editSearch);
        filter = findViewById(R.id.totalDiscountCode);
        listDiscountCode = new ArrayList<>();

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
                intent = new Intent(ListDiscountCodeActivity.this, DetailDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("type","add");
                startActivity(intent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                DatabaseReference codeRef = db.getReference("DiscountCode");
                codeRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        DiscountCode code = snapshot.getValue(DiscountCode.class);
                        if(newText.equals("")){
                            listDiscountCode.add(code);
                        }else {
                            if(code.getCode().toUpperCase().trim().contains(newText.toUpperCase().trim())){
                                listDiscountCode.add(code);
                            }
                        }
                        discountCodeAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });
        //RecycleView
        recyclerView = findViewById(R.id.listDiscount);
        recyclerView.setHasFixedSize(true);
        listDiscountCode = new ArrayList<>();
        data();
        discountCodeAdapter = new DiscountCodeAdapter(listDiscountCode,this);
        discountCodeAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(discountCodeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private DiscountCodeAdapter.ItemClickListener itemClickListener = new DiscountCodeAdapter.ItemClickListener() {
        @Override
        public void deleteProduct(String code) {
            showErrorDialog("Bạn có muốn xoá mã giảm giá ?",code);
        }

        @Override
        public void editProduct(DiscountCode item) {
            intent = new Intent(ListDiscountCodeActivity.this,DetailDiscountCodeActivity.class);
            intent.putExtra("item", (Parcelable) item);
            intent.putExtra("type","update");
            startActivity(intent);
        }
    };

    private void data(){
        listDiscountCode = new ArrayList<>();
        DatabaseReference codeRef = db.getReference("DiscountCode");
        codeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listDiscountCode.clear();
                for(DataSnapshot node : snapshot.getChildren()){
                    DiscountCode code = node.getValue(DiscountCode.class);
                    listDiscountCode.add(code);

                }
                discountCodeAdapter.notifyDataSetChanged();
                filter.setText(discountCodeAdapter.getItemCount() + " sản phẩm từ " + listDiscountCode.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

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
                intent = new Intent(ListDiscountCodeActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                intent = new Intent(ListDiscountCodeActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(ListDiscountCodeActivity.this, "Điều phối hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlmgg:
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListDiscountCodeActivity.this, ListCateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListDiscountCodeActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListDiscountCodeActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(ListDiscountCodeActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListDiscountCodeActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
    private void showErrorDialog(String notify,String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDiscountCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListDiscountCodeActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            DatabaseReference codeRef = db.getReference("DiscountCode");
            codeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot node : snapshot.getChildren()){
                        DiscountCode discountCode = node.getValue(DiscountCode.class);
                        if(discountCode.getCode().equals(code)){
                            codeRef.child(node.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    showSuccesDialog("Xoá mã giảm giá thành công");
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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
    private void showSuccesDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDiscountCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListDiscountCodeActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
