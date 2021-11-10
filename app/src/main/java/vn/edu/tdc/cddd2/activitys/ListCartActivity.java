package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.CartDetailAdapter;
import vn.edu.tdc.cddd2.data_models.Cart;
import vn.edu.tdc.cddd2.data_models.CartDetail;
import vn.edu.tdc.cddd2.data_models.Product;
import vn.edu.tdc.cddd2.helper.RecyclerItemTouchHelper;

public class ListCartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    // Khai báo biến:
    Toolbar toolbar;
    TextView btnBack;
    Button btnIncrease, btnDecrease, btnPayment;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    NavigationView navigationView;
    CartDetailAdapter cartAdapter;
    private Intent intent;
    String accountID = "";
    ArrayList<CartDetail> listCart;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("Cart");
    DatabaseReference detailRef = db.getReference("Cart_Detail");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_cart_sm);

        intent = getIntent();
        accountID = intent.getStringExtra("accountID");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnPayment = findViewById(R.id.buttonThanhToan);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện click button "Thanh toán":
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(ListCartActivity.this, CreateOrderActivity.class);
                //intent.putExtra();
                startActivity(intent);
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listCart = new ArrayList<>();
        data();
        cartAdapter = new CartDetailAdapter(this, listCart);
        cartAdapter.setItemClickListener(itemClickListener);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Trượt xoá giỏ hàng
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartDetailAdapter.ViewHolder) {
            // get the removed item name to display it in snack bar
            String id = listCart.get(viewHolder.getAdapterPosition()).getProductID();

            // backup of removed item for undo purpose
            final CartDetail deletedItem = listCart.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            cartAdapter.removeItem(viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
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
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                listCart.clear();
                                for (DataSnapshot node1 : snapshot.getChildren()) {
                                    CartDetail detail = node1.getValue(CartDetail.class);
                                    detail.setKey(node1.getKey());
                                    if (cart.getCartID().equals(detail.getCartID())) {
                                        listCart.add(detail);
                                    }
                                }
                                btnPayment.setText("Tổng tiền : " + formatPrice(cart.getTotal()));
                                cartAdapter.notifyDataSetChanged();
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

    private CartDetailAdapter.ItemClickListener itemClickListener = new CartDetailAdapter.ItemClickListener() {
        @Override
        public void changeQuantity(String key, int value) {
            detailRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CartDetail cartDetail = snapshot.getValue(CartDetail.class);
                    int amount = cartDetail.getAmount();
                    detailRef.child(key).child("amount").setValue(value);
                    ref.child(cartDetail.getCartID()).child("total").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (amount >= value) {
                                ref.child(cartDetail.getCartID()).child("total").setValue(snapshot.getValue(Integer.class) - cartDetail.getPrice());
                            } else {
                                ref.child(cartDetail.getCartID()).child("total").setValue(snapshot.getValue(Integer.class) + cartDetail.getPrice());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_dmk:
                intent = new Intent(ListCartActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListCartActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListCartActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
}
