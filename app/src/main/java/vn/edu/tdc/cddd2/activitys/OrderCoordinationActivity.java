package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.AreaAdapter;
import vn.edu.tdc.cddd2.data_models.AccountHistory;
import vn.edu.tdc.cddd2.data_models.Area;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.OrderDetail;
import vn.edu.tdc.cddd2.data_models.Product;
import vn.edu.tdc.cddd2.fragments.FragmentWaitShipWHM;
import vn.edu.tdc.cddd2.fragments.FragmentWillOrderWHM;

public class OrderCoordinationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    // Khai báo biến:
    BottomNavigationView bottomNavigationView;
    ImageView img_help;
    float dx, dy;
    Fragment selectedFragment = null;
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnCancel, title, mess, txtName, txtRole;
    String username = "", name = "", role = "", img = "", accountID = "";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    Intent intent;
    String tagA = "WillOrderWHM";
    ArrayList<Order> listOrder;
    ArrayList<Area> listArea;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference orderRef = db.getReference("Order");
    DatabaseReference orderDetailRef = db.getReference("Order_Details");
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference areaRef = db.getReference("Area");
    DatabaseReference cusRef = db.getReference("Customer");
    DatabaseReference cusTypeRef = db.getReference("CustomerType");

    private GestureDetector gestureDetector;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ordercoor);

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
        subtitleAppbar.setText(R.string.titleLayoutDPH);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        txtName.setText(name);
        txtRole.setText(role);

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        img_help = findViewById(R.id.img_help);
        img_help.setOnClickListener(this);

        img_help.setOnTouchListener((v, event) -> {
            if (gestureDetector.onTouchEvent(event)) {
                // show dialog:
                AlertDialog.Builder dialog = new AlertDialog.Builder(OrderCoordinationActivity.this);
                View customView = LayoutInflater.from(OrderCoordinationActivity.this).inflate(R.layout.layout_detail_area, null);
                dialog.setView(customView);
                listArea = new ArrayList<>();
                ListView listView = customView.findViewById(R.id.listarea);
                AreaAdapter adapter = new AreaAdapter(OrderCoordinationActivity.this, listArea);
                listView.setAdapter(adapter);
                // get data:
                areaRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listArea.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Area area = new Area(snapshot.getKey(), snapshot.child("area").getValue(String.class));
                            listArea.add(area);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final AlertDialog alertDialog = dialog.create();

                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
                return true;
            } else {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dx = v.getX() - event.getRawX();
                        dy = v.getY() - event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        v.animate()
                                .x(event.getRawX() + dx)
                                .y(event.getRawY() + (dy - v.getWidth()))
                                .setDuration(0)
                                .start();
                        break;
                }
            }
            return false;
        });

        // Xử lý sự kiện click button "Lưu":
        btnSave.setOnClickListener(this);

        // Xử lý sự kiện click button "Huỷ":
        btnCancel.setOnClickListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentWillOrderWHM(), tagA).commit();

        // Xử lý sự kiện cho thanh bottomnavigationview
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.willorder) {
                selectedFragment = new FragmentWillOrderWHM();
                tagA = "WillOrderWHM";
            } else {
                selectedFragment = new FragmentWaitShipWHM();
                tagA = "WaitShipWHM";
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, tagA).commit();
            return true;
        });
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
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
            case R.id.nav_qlsp:
                intent = new Intent(OrderCoordinationActivity.this, ListProductActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlkm:
                intent = new Intent(OrderCoordinationActivity.this, ListPromoActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dph:
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(OrderCoordinationActivity.this, ListDiscountCodeActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qllsp:
                intent = new Intent(OrderCoordinationActivity.this, ListCateActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(OrderCoordinationActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(OrderCoordinationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlh:
                intent = new Intent(OrderCoordinationActivity.this, ListManuActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(OrderCoordinationActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnCancel) {
            showErrorDialog();
        } else {
            listOrder = new ArrayList<>();
            FragmentWaitShipWHM fragmentWaitShipWHM = (FragmentWaitShipWHM) getSupportFragmentManager().findFragmentByTag("WaitShipWHM");
            FragmentWillOrderWHM fragmentWillOrderWHM = (FragmentWillOrderWHM) getSupportFragmentManager().findFragmentByTag("WillOrderWHM");
            if (fragmentWaitShipWHM != null && fragmentWaitShipWHM.isVisible()) {
                listOrder = fragmentWaitShipWHM.getList();
            } else {
                listOrder = fragmentWillOrderWHM.getList();
            }
            for (Order order : listOrder) {
                if (order.getStatus() == 2 && !order.getShipperID().equals("null")) {
                    order.setStatus(3);
                    orderRef.child(order.getOrderID()).setValue(order);
                }
                else {
                    orderRef.child(order.getOrderID()).setValue(order);
                    if (order.getStatus() == 8) {
                        pushAccountHistory("Thay đổi trạng thái hoàn thành đơn hàng", "Đơn hàng: " + order.getOrderID());
                        orderDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    OrderDetail orderDetail = snapshot.getValue(OrderDetail.class);
                                    if (order.getOrderID().equals(orderDetail.getOrderID())) {
                                        proRef.child(orderDetail.getProductID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Product product = dataSnapshot.getValue(Product.class);
                                                int sold = product.getSold() + orderDetail.getAmount();
                                                int quantity = product.getQuantity() - orderDetail.getAmount();
                                                proRef.child(orderDetail.getProductID()).child("quantity").setValue(quantity);
                                                proRef.child(orderDetail.getProductID()).child("sold").setValue(sold);
                                                cusRef.orderByChild("accountID").equalTo(order.getAccountID()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                            Customer customer = dataSnapshot1.getValue(Customer.class);
                                                            int totalPaymentNew = customer.getTotalPayment() + order.getTotal();
                                                            cusTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    String typeID = "";
                                                                    for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                                                                        if (totalPaymentNew >= snapshot1.child("consume").getValue(Integer.class)) {
                                                                            typeID = snapshot1.getKey();
                                                                        }
                                                                    }
                                                                    cusRef.child(dataSnapshot1.getKey()).child("type_id").setValue(typeID);
                                                                    cusRef.child(dataSnapshot1.getKey()).child("totalPayment").setValue(totalPaymentNew);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
            showSuccesDialog("Cập nhật đơn hàng thành công!");
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderCoordinationActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(OrderCoordinationActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận huỷ?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            intent = new Intent(OrderCoordinationActivity.this, MainQLKActivity.class);
            intent.putExtra("accountID", accountID);
            intent.putExtra("username", username);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("image", img);
            startActivity(intent);
            finish();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderCoordinationActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(OrderCoordinationActivity.this).inflate(
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
