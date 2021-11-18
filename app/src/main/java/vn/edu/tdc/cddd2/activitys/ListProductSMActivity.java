package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
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

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.Product2Adapter;
import vn.edu.tdc.cddd2.data_models.Cart;
import vn.edu.tdc.cddd2.data_models.CartDetail;
import vn.edu.tdc.cddd2.data_models.Category;
import vn.edu.tdc.cddd2.data_models.Manufacture;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListProductSMActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Toolbar toolbar;
    ImageView btnCart, iv_user;
    Spinner spinCate, spinManu;
    TextView title, mess, txtName, txtRole;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    private ArrayList<Product> listProduct;
    NavigationView navigationView;
    private Product2Adapter proAdapter;
    private Intent intent;
    ArrayList<Manufacture> listManu;
    ArrayList<Category> listCate;
    SearchView searchView;
    Handler handler = new Handler();
    int isFirst = 0;
    ArrayAdapter manuAdapter, cateAdapter;
    String cartID = "", accountID = "", username = "", name = "", role = "", img = "";
    boolean check = true;

    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("Products");
    DatabaseReference manuRef = db.getReference("Manufactures");
    DatabaseReference cateRef = db.getReference("Categories");
    DatabaseReference cartRef = db.getReference("Cart");
    DatabaseReference cartDetailRef = db.getReference("Cart_Detail");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_product_sm);

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnCart = findViewById(R.id.btnCart);
        listManu = new ArrayList<>();
        listCate = new ArrayList<>();
        spinCate = findViewById(R.id.spinner_cata);
        spinManu = findViewById(R.id.spinner_manu);
        searchView = findViewById(R.id.editSearch);
        manuAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listManu);
        cateAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listCate);

        // Spinner
        manuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinManu.setAdapter(manuAdapter);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCate.setAdapter(cateAdapter);

        // Xử lý sự kiện click button "Giỏ hàng":
        btnCart.setOnClickListener(v -> {
            intent = new Intent(ListProductSMActivity.this, ListCartActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("accountID", accountID);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("image", img);
            startActivity(intent);
        });

        // Xử lý sự kiện lọc theo spinCate:
        spinCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handler.postDelayed(() -> {
                    Manufacture manu = (Manufacture) spinManu.getSelectedItem();
                    Category cate = (Category) spinCate.getSelectedItem();
                    String query = String.valueOf(searchView.getQuery());
                    listProduct.clear();
                    filterProduct(cate.getKey(), manu.getKey(), query, isFirst);
                    isFirst++;
                }, 200);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Xử lý sự kiện lọc theo spinManu:
        spinManu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handler.postDelayed(() -> {
                    Manufacture manu = (Manufacture) spinManu.getSelectedItem();
                    Category cate = (Category) spinCate.getSelectedItem();
                    String query = String.valueOf(searchView.getQuery());
                    listProduct.clear();
                    filterProduct(cate.getKey(), manu.getKey(), query, isFirst);
                }, 200);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listProduct = new ArrayList<>();
        data();
        proAdapter = new Product2Adapter(listProduct, this);
        proAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(proAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);

        // Sự kiện searchview:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.postDelayed(() -> {
                    Manufacture manu = (Manufacture) spinManu.getSelectedItem();
                    Category cate = (Category) spinCate.getSelectedItem();
                    String query = String.valueOf(searchView.getQuery());
                    listProduct.clear();
                    filterProduct(cate.getKey(), manu.getKey(), query, isFirst);
                }, 200);
                return true;
            }
        });
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
                listProduct.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Product product = node.getValue(Product.class);
                    product.setKey(node.getKey());
                    if (product.getStatus() != -1) {
                        listProduct.add(product);
                    }
                }
                proAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listCate.clear();
        listManu.clear();
        listManu.add(new Manufacture("", "Tất cả", ""));
        listCate.add(new Category("", "Tất cả", ""));
        manuRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Manufacture manu = node.getValue(Manufacture.class);
                    manu.setKey(node.getKey());
                    listManu.add(manu);
                    manuAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        cateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Category cate = node.getValue(Category.class);
                    cate.setKey(node.getKey());
                    listCate.add(cate);
                    cateAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filterProduct(String category_id, String manu_id, String query, int isFirst) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProduct.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Product product = snapshot.getValue(Product.class);
                    product.setKey(snapshot.getKey());
                    if (product != null) {
                        if (product.getStatus() != -1) {
                            if (category_id.equals("") && manu_id.equals("") && query.equals("")) {
                                if (isFirst >= 1) {
                                    listProduct.add(product);
                                }
                            } else {
                                if (!category_id.equals("")) {
                                    if (!manu_id.equals("")) {
                                        if (query.equals("")) {
                                            if (manu_id.equals(product.getManu_id()) && category_id.equals(product.getCategory_id())) {
                                                listProduct.add(product);
                                            }
                                        } else {
                                            if (product.getName().toLowerCase().contains(query.toLowerCase())
                                                    && manu_id.equals(product.getManu_id())
                                                    && category_id.equals(product.getCategory_id())) {
                                                listProduct.add(product);
                                            }
                                        }
                                    } else {
                                        if (query.equals("")) {
                                            if (category_id.equals(product.getCategory_id())) {
                                                listProduct.add(product);
                                            }
                                        } else {
                                            if (product.getName().toLowerCase().contains(query.toLowerCase())
                                                    && category_id.equals(product.getCategory_id())) {
                                                listProduct.add(product);
                                            }
                                        }
                                    }
                                } else {
                                    if (!manu_id.equals("")) {
                                        if (query.equals("")) {
                                            if (manu_id.equals(product.getManu_id())) {
                                                listProduct.add(product);
                                            }
                                        } else {
                                            if (product.getName().toLowerCase().contains(query.toLowerCase())
                                                    && manu_id.equals(product.getManu_id())) {
                                                listProduct.add(product);
                                            }
                                        }
                                    } else {
                                        if (query.equals("")) {

                                        } else {
                                            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                                                listProduct.add(product);
                                            }
                                        }
                                    }
                                }
                            }
                            proAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private final Product2Adapter.ItemClickListener itemClickListener = (key, price) -> {
        // Kiểm tra đã có giỏ hàng chưa?
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartID = "";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("accountID").getValue(String.class).equals(accountID)) {
                        // Nếu có thì? -> lấy CartID
                        cartID = dataSnapshot.getKey();
                        cartDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                check = true;
                                int total = dataSnapshot.child("total").getValue(Integer.class);
                                for (DataSnapshot dataSnapshot1 : snapshot1.getChildren()) {
                                    CartDetail cartDetail = dataSnapshot1.getValue(CartDetail.class);
                                    cartDetail.setKey(dataSnapshot1.getKey());
                                    if (cartDetail.getCartID().equals(cartID) && cartDetail.getProductID().equals(key)) {
                                        check = false;
                                        int amount = cartDetail.getAmount() + 1;
                                        cartDetailRef.child(cartDetail.getKey()).child("amount").setValue(amount);
                                        cartDetailRef.child(cartDetail.getKey()).child("price").setValue(price);
                                        cartRef.child(cartID).child("total").setValue(total + price);
                                        break;
                                    }
                                }
                                if (check) {
                                    CartDetail cartDetail = new CartDetail(cartID, key, 1, price);
                                    cartDetailRef.push().setValue(cartDetail);
                                    cartRef.child(cartID).child("total").setValue(total + price);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        break;
                    }
                }
                // Nếu chưa thì? -> tạo mới
                if (cartID.equals("")) {
                    Cart cart = new Cart(accountID, 0);
                    String key = cartRef.push().getKey();
                    cartRef.child(key).setValue(cart);
                    cartRef.child(key).child("total").setValue(price);
                    CartDetail cartDetail = new CartDetail(key, key, 1, price);
                    cartDetailRef.push().setValue(cartDetail);
                    cartRef.child(key).child("total").setValue(price);
                }

                showSuccesDialog("Thêm vào giỏ hàng thành công!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_dmk:
                intent = new Intent(ListProductSMActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListProductSMActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(ListProductSMActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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

    private void showSuccesDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListProductSMActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListProductSMActivity.this).inflate(
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

        view.findViewById(R.id.buttonAction).setVisibility(View.GONE);

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

        handler.postDelayed(alertDialog::dismiss, 1500);
    }

}
