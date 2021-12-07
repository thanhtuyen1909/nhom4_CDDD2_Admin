package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.fragments.FragmentCancelOrderOH;
import vn.edu.tdc.cddd2.fragments.FragmentListOrderOH;
import vn.edu.tdc.cddd2.fragments.FragmentWillOrderOH;

public class OrderProcessActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến:
    BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, title, mess, txtName, txtRole;
    ImageView iv_user;
    private String tag = "ListOrderOH";
    String accountID = "", username = "", name = "", role = "", img = "";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    private Intent intent;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Order");
    DatabaseReference cusRef = FirebaseDatabase.getInstance().getReference("Customer");
    private ArrayList<Order> listOrder;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orderprocess_oh);

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Xử lý đơn hàng");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtBack);
        btnSave.setText("Lưu");
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Xử lý sự kiện click button "Lưu":
        btnSave.setOnClickListener(v -> {
            listOrder = new ArrayList<>();
            FragmentListOrderOH fragmentListOrderOH = (FragmentListOrderOH) getSupportFragmentManager().findFragmentByTag("ListOrderOH");
            FragmentCancelOrderOH fragmentCancelOrderOH = (FragmentCancelOrderOH) getSupportFragmentManager().findFragmentByTag("CancelOrderOH");
            FragmentWillOrderOH fragmentWillOrderOH = (FragmentWillOrderOH) getSupportFragmentManager().findFragmentByTag("WillOrderOH");

            if (fragmentListOrderOH != null && fragmentListOrderOH.isVisible()) {
                listOrder = fragmentListOrderOH.getListOrder();

            } else if (fragmentWillOrderOH != null && fragmentWillOrderOH.isVisible()) {
                listOrder = fragmentWillOrderOH.getListOrder();

            } else {
                listOrder = fragmentCancelOrderOH.getListOrder();
            }
            for (Order order : listOrder) {
                if (order.getStatus() == 0) {
                    myRef.orderByChild("accountID").equalTo(order.getAccountID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int sum = 0;
                            for (DataSnapshot node : snapshot.getChildren()) {
                                Order order1 = node.getValue(Order.class);
                                if(order1.getStatus() == 0) {
                                    sum++;
                                }
                            }
                            if(sum >= 10) {
                                cusRef.orderByChild("accountID").equalTo(order.getAccountID()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            Customer customer = snapshot1.getValue(Customer.class);
                                            customer.setStatus("black");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                cusRef.orderByChild("accountID").equalTo(order.getAccountID()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            Customer customer = snapshot1.getValue(Customer.class);
                                            customer.setStatus("green");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                myRef.child(order.getOrderID()).setValue(order);
            }
            showSuccesDialog("Cập nhập trạng thái đơn hàng thành công!");
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentListOrderOH(), tag).commit();

        // Xử lý sự kiện cho thanh bottomnavigationview
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.odering) {
                selectedFragment = new FragmentListOrderOH();
                tag = "ListOrderOH";
            } else if (item.getItemId() == R.id.willorder) {
                selectedFragment = new FragmentWillOrderOH();
                tag = "WillOrderOH";
            } else {
                selectedFragment = new FragmentCancelOrderOH();
                tag = "CancelOrderOH";
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, tag).commit();
            return true;
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
            case R.id.nav_dmk:
                intent = new Intent(OrderProcessActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(OrderProcessActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(OrderProcessActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderProcessActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(OrderProcessActivity.this).inflate(
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
