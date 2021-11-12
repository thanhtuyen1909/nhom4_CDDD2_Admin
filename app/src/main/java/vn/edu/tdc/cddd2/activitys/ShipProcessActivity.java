package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.OrderDetail;
import vn.edu.tdc.cddd2.data_models.Product;
import vn.edu.tdc.cddd2.fragments.FragmentCancelOrderSP;
import vn.edu.tdc.cddd2.fragments.FragmentListOrderSP;
import vn.edu.tdc.cddd2.fragments.FragmentOrderDeliveredSP;
import vn.edu.tdc.cddd2.fragments.FragmentOrderingSP;
import vn.edu.tdc.cddd2.fragments.FragmentWaitShipWHM;
import vn.edu.tdc.cddd2.fragments.FragmentWillOrderWHM;

public class ShipProcessActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến:
    BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, title, mess;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    Intent intent;
    String tagA = "ListOrderSP", username = "shipper";
    ArrayList<Order> listOrder;
    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_shipprocess);

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Giao hàng");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtBack);
        btnSave.setText("Lưu");
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentListOrderSP(), tagA).commit();

        // Xử lý sự kiện cho thanh bottomnavigationview
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.odering) {
                    selectedFragment = new FragmentListOrderSP();
                    tagA = "ListOrderSP";
                } else if (id == R.id.delivering) {
                    selectedFragment = new FragmentOrderingSP();
                    tagA = "OrderingSP";
                } else if (id == R.id.delivered) {
                    selectedFragment = new FragmentOrderDeliveredSP();
                    tagA = "OrderDeliveredSP";
                } else {
                    selectedFragment = new FragmentCancelOrderSP();
                    tagA = "CancelOrderSP";
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, tagA).commit();
                return true;
            }
        });

        // Xử lý sự kiện click button "Lưu":
        btnSave.setOnClickListener(v -> {
            listOrder = new ArrayList<>();
            FragmentListOrderSP fragmentListOrderSP = (FragmentListOrderSP) getSupportFragmentManager().findFragmentByTag("ListOrderSP");
            FragmentOrderingSP fragmentOrderingSP = (FragmentOrderingSP) getSupportFragmentManager().findFragmentByTag("OrderingSP");
            FragmentOrderDeliveredSP fragmentOrderDeliveredSP = (FragmentOrderDeliveredSP) getSupportFragmentManager().findFragmentByTag("OrderDeliveredSP");
            FragmentCancelOrderSP fragmentCancelOrderSP = (FragmentCancelOrderSP) getSupportFragmentManager().findFragmentByTag("CancelOrderSP");
            if (fragmentListOrderSP != null && fragmentListOrderSP.isVisible()) {
                listOrder = fragmentListOrderSP.getList();
            } else if (fragmentOrderingSP != null && fragmentOrderingSP.isVisible()) {
                listOrder = fragmentOrderingSP.getList();
            } else if (fragmentOrderDeliveredSP != null && fragmentOrderDeliveredSP.isVisible()) {
                listOrder = fragmentOrderDeliveredSP.getList();
            } else {
                listOrder = fragmentCancelOrderSP.getList();
            }
            for (Order order : listOrder) {
                orderRef.child(order.getOrderID()).setValue(order);
            }
            showSuccesDialog("Cập nhật đơn hàng thành công!");
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_dmk:
                intent = new Intent(ShipProcessActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                intent.putExtra("username", username);
                break;
            case R.id.nav_dx:
                intent = new Intent(ShipProcessActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(ShipProcessActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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

    private void showSuccesDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShipProcessActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ShipProcessActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(message);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
