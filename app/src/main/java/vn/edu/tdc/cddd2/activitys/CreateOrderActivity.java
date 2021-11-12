package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.PaymentAdapter;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.AccountHistory;
import vn.edu.tdc.cddd2.data_models.Cart;
import vn.edu.tdc.cddd2.data_models.CartDetail;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.CustomerType;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.OrderDetail;
import vn.edu.tdc.cddd2.data_models.Product;

public class CreateOrderActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến:
    Toolbar toolbar;
    TextView btnBack, txt_tongtien, txt_conlai, txt_giamgia, title, mess;
    Button buttonTTNgay, buttonGiaoHang;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    ArrayList<CartDetail> listCartDetail;
    NavigationView navigationView;
    PaymentAdapter proAdapter;
    private Intent intent;
    String accountID = "", username = "";
    EditText edtDaThanhToan, edtDiaChi, edtHoTenKH, edtSDT;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("Cart");
    DatabaseReference detailRef = db.getReference("Cart_Detail");
    DatabaseReference accountRef = db.getReference("Account");
    DatabaseReference customerRef = db.getReference("Customer");
    DatabaseReference cusTypeRef = db.getReference("CustomerType");
    DatabaseReference orderRef = db.getReference("Order");
    DatabaseReference orderDetailRef = db.getReference("Order_Details");
    DatabaseReference accHistoryRef = db.getReference("AccountHistory");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payment_sm);

        intent = getIntent();
        accountID = intent.getStringExtra("accountID");
        username = intent.getStringExtra("username");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        buttonTTNgay = findViewById(R.id.buttonTTNgay);
        buttonGiaoHang = findViewById(R.id.buttonGiaoHang);
        txt_tongtien = findViewById(R.id.txt_tongtien);
        txt_conlai = findViewById(R.id.txt_conlai);
        txt_giamgia = findViewById(R.id.txt_giamgia);
        edtDaThanhToan = findViewById(R.id.edtDaThanhToan);
        edtDiaChi = findViewById(R.id.edtDC);
        edtHoTenKH = findViewById(R.id.edtHTKH);
        edtSDT = findViewById(R.id.edtSDT);

        txt_giamgia.setText(formatPrice(0));

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> {
            intent = new Intent(CreateOrderActivity.this, ListCartActivity.class);
            intent.putExtra("accountID", accountID);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");

        // Xử lý sự kiện click button "Thanh Toán Ngay":
        buttonTTNgay.setOnClickListener(v -> {
            Date now = new Date();
            String key = "DH" + sdf.format(now).replace("/", "").replace(":", "").replace(" ", "");
            // Kiểm tra nhập đúng số tiền cần thanh toán
            if(String.valueOf(txt_conlai.getText()).equals("0 ₫")) {
                // Lưu đơn hàng với trạng thái "Hoàn thành" - 8 -> Xoá giỏ hàng + chi tiết giỏ hàng
                if (checkError() == 1) {
                    Order order = new Order();
                    order.setAccountID(accountID);
                    order.setAddress(String.valueOf(edtDiaChi.getText()));
                    order.setCreated_at(sdf.format(now));
                    order.setName(String.valueOf(edtHoTenKH.getText()));
                    order.setNote("");
                    order.setPhone(String.valueOf(edtSDT.getText()));
                    order.setShipperID("null");
                    order.setStatus(8);
                    order.setTotal(formatInt(txt_tongtien.getText() + ""));
                    order.setRemain(formatInt(txt_conlai.getText() + ""));
                    orderRef.child(key).setValue(order).addOnSuccessListener(unused -> {
                        order.setOrderID(key);
                        showSuccesDialog("Tạo đơn hàng thành công!", order);
                    });

                    // Tạo order details
                    for (CartDetail detail : listCartDetail) {
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setOrderID(key);
                        orderDetail.setAmount(detail.getAmount());
                        orderDetail.setPrice(detail.getPrice());
                        orderDetail.setProductID(detail.getProductID());
                        orderDetailRef.push().setValue(orderDetail);
                    }

                    //Clear Cart
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot node : snapshot.getChildren()) {
                                Cart cart = node.getValue(Cart.class);
                                if (cart.getAccountID().equals(accountID)) {
                                    cart.setTotal(0);
                                    ref.child(node.getKey()).setValue(cart);
                                    detailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                            for (DataSnapshot node1 : snapshot1.getChildren()) {
                                                CartDetail cartDetail = node1.getValue(CartDetail.class);
                                                if (cartDetail.getCartID().equals(node.getKey())) {
                                                    detailRef.child(node1.getKey()).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    // Tính tổng thanh toán cho khách hàng:
                    accountRef.orderByChild("username").equalTo(String.valueOf(edtSDT.getText())).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for ( DataSnapshot node : snapshot.getChildren()) {
                                Account account = node.getValue(Account.class);
                                if(account.getStatus().equals("unlock")) {
                                    customerRef.orderByChild("accountID").equalTo(node.getKey()).limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                Customer customer = dataSnapshot1.getValue(Customer.class);
                                                int totalPaymentNew = customer.getTotalPayment() + order.getTotal();
                                                String typeID = "";
                                                if (totalPaymentNew >= 15000000) {
                                                    typeID = "Type1";
                                                } else if (totalPaymentNew > 100000000) {
                                                    typeID = "Type2";
                                                } else if (totalPaymentNew > 200000000) {
                                                    typeID = "Type3";
                                                } else typeID = "Type";
                                                customerRef.child(dataSnapshot1.getKey()).child("totalPayment").setValue(totalPaymentNew);
                                                customerRef.child(dataSnapshot1.getKey()).child("type_id").setValue(typeID);
                                                break;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                break;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            else {
                showWarningDialog("Vui lòng kiểm tra lại số tiền đã thanh toán vừa nhập!");
            }
        });

        // Xử lý sự kiện click button "Giao Hàng":
        buttonGiaoHang.setOnClickListener(v -> {
            Date now = new Date();
            String key = "DH" + sdf.format(now).replace("/", "").replace(":", "").replace(" ", "");
            // Kiểm tra nhập đúng số tiền cần thanh toán
            if(checkError() == 1) {
                // Lưu đơn hàng với trạng thái "Đang xử lý" - 2 -> Xoá giỏ hàng + chi tiết giỏ hàng
                Order order = new Order();
                order.setAccountID(accountID);
                order.setAddress(String.valueOf(edtDiaChi.getText()));
                order.setCreated_at(sdf.format(now));
                order.setName(String.valueOf(edtHoTenKH.getText()));
                order.setNote("");
                order.setPhone(String.valueOf(edtSDT.getText()));
                order.setShipperID("null");
                order.setStatus(2);
                order.setTotal(formatInt(txt_tongtien.getText() + ""));
                order.setRemain(formatInt(txt_conlai.getText() + ""));
                orderRef.child(key).setValue(order).addOnSuccessListener(unused -> {
                    order.setOrderID(key);
                    showSuccesDialog("Tạo đơn hàng thành công!", order);
                });

                for (CartDetail detail : listCartDetail) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrderID(key);
                    orderDetail.setAmount(detail.getAmount());
                    orderDetail.setPrice(detail.getPrice());
                    orderDetail.setProductID(detail.getProductID());
                    orderDetailRef.push().setValue(orderDetail);
                }
                //Clear Cart
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Cart cart = node.getValue(Cart.class);
                            if (cart.getAccountID().equals(accountID)) {
                                cart.setTotal(0);
                                ref.child(node.getKey()).setValue(cart);
                                detailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        for (DataSnapshot node1 : snapshot1.getChildren()) {
                                            CartDetail cartDetail = node1.getValue(CartDetail.class);
                                            if (cartDetail.getCartID().equals(node.getKey())) {
                                                detailRef.child(node1.getKey()).removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // Xử lý lọc loại khách hàng theo số điện thoại -> giảm giá:
        edtSDT.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus) {
                if (String.valueOf(edtSDT.getText()).length() == 10 && String.valueOf(edtSDT.getText().charAt(0)).equals("0")) {
                    accountRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot node : snapshot.getChildren()) {
                                Account account = node.getValue(Account.class);
                                account.setKey(node.getKey());
                                if(account.getUsername().equals(edtSDT.getText().toString()) && account.getStatus().equals("unlock")) {
                                    customerRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                            for (DataSnapshot node1 : snapshot1.getChildren()) {
                                                Customer customer = node1.getValue(Customer.class);
                                                if(customer.getAccountID().equals(account.getKey()) && customer.getStatus().equals("green")) {
                                                    edtHoTenKH.setText(customer.getName());
                                                    cusTypeRef.addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                                                            for (DataSnapshot node2 : snapshot2.getChildren()) {
                                                                CustomerType customerType = node2.getValue(CustomerType.class);
                                                                customerType.setKey(node2.getKey());
                                                                if(customerType.getKey().equals(customer.getType_id())) {
                                                                    int total = formatInt(txt_tongtien.getText().toString());
                                                                    txt_giamgia.setText(formatPrice(total * customerType.getDiscount() / 100));
                                                                    txt_conlai.setText(formatPrice(total - (total * customerType.getDiscount() / 100)));
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    break;
                                }
                                else {
                                    int total = formatInt(txt_tongtien.getText().toString());
                                    txt_giamgia.setText(formatPrice(0));
                                    txt_conlai.setText(formatPrice(total));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                else {
                    showWarningDialog("Vui lòng kiểm tra số điện thoại vừa nhập!");
                }
            }
        });

        // Xử lý tính còn lại sau khi nhập "Đã thanh toán":
        edtDaThanhToan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int discount = 0, paid = 0;
                int total = formatInt(txt_tongtien.getText() + "");
                if(String.valueOf(txt_giamgia.getText()).length() > 2) {
                    discount = formatInt(txt_giamgia.getText() + "");
                }
                if(!String.valueOf(txt_giamgia.getText()).isEmpty()) {
                    paid = Integer.parseInt(edtDaThanhToan.getText() + "");
                }
                txt_conlai.setText(formatPrice(total - discount - paid));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listCartDetail = new ArrayList<>();
        data();
        proAdapter = new PaymentAdapter(listCartDetail, this);
        recyclerView.setAdapter(proAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Kiểm tra lỗi
    private int checkError() {
        if (String.valueOf(edtDiaChi.getText()).equals("")) {
            showWarningDialog("Địa chỉ không được để trống");
            return -1;
        }
        if (String.valueOf(edtHoTenKH.getText()).equals("")) {
            showWarningDialog("Tên người nhận không được để trống");
            return -1;
        }
        if (String.valueOf(edtSDT.getText()).equals("")) {
            showWarningDialog("Số điện thoại không được để trống");
            return -1;
        }
        return 1;
    }

    // Xử lý thông báo:
    private void showWarningDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateOrderActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(CreateOrderActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(s);
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String notify, Order item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateOrderActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(CreateOrderActivity.this).inflate(
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

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            // Thêm vào "Lịch sử hoạt động"
            AccountHistory accountHistory = new AccountHistory();
            accountHistory.setAccountID(accountID);
            accountHistory.setAction("Tạo đơn hàng");
            accountHistory.setDetail("Mã đơn hàng: " + item.getOrderID() + "\nTổng tiền: " + item.getTotal());
            accountHistory.setDate(item.getCreated_at());
            accHistoryRef.push().setValue(accountHistory);
            // Điều hướng về lại màn hình chi tiết đơn hàng
            startActivity(new Intent(CreateOrderActivity.this, DetailOrderActivity.class)
                    .putExtra("item", item)
                    .putExtra("from", "PaymentSM")
                    .putExtra("accountID", accountID)
                    .putExtra("username", username));
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Cart cart = node.getValue(Cart.class);
                    cart.setCartID(node.getKey());
                    if (cart.getAccountID().equals(accountID)) {
                        detailRef.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                listCartDetail.clear();
                                for (DataSnapshot node1 : snapshot.getChildren()) {
                                    CartDetail detail = node1.getValue(CartDetail.class);
                                    detail.setKey(node1.getKey());
                                    if (cart.getCartID().equals(detail.getCartID())) {
                                        listCartDetail.add(detail);
                                    }
                                }
                                txt_tongtien.setText(formatPrice(cart.getTotal()));
                                txt_conlai.setText(formatPrice(cart.getTotal()));
                                proAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String formatPrice(int price) {
        String stmp = String.valueOf(price);
        int amount;
        amount = (int) (stmp.length() / 3);
        if (stmp.length() % 3 == 0)
            amount--;
        for (int i = 1; i <= amount; i++) {
            stmp = new StringBuilder(stmp).insert(stmp.length() - (i * 3) - (i - 1), ",").toString();
        }
        return stmp + " ₫";
    }

    private int formatInt(String price) {
        return Integer.parseInt(price.substring(0, price.length() - 2).replace(",", ""));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_dmk:
                intent = new Intent(CreateOrderActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(CreateOrderActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(CreateOrderActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
