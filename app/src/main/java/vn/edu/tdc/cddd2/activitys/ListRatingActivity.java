package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
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
import java.util.Collections;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.Product6Adapter;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListRatingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Handler handler = new Handler();
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, totalRating, txtName, txtRole;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    ArrayList<Product> listProduct;
    NavigationView navigationView;
    Product6Adapter product6Adapter;
    Intent intent;
    Spinner spinner_filterrating;
    SearchView editSearch;
    String accountID = "", username = "", name = "", role = "", img = "";
    int size = 0;
    ImageView iv_user;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference proRef = db.getReference("Products");
    DatabaseReference ratRef = db.getReference("Rating");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_rating);

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Đánh giá & bình luận");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        spinner_filterrating = findViewById(R.id.spinner_filterrating);
        editSearch = findViewById(R.id.editSearch);
        totalRating = findViewById(R.id.totalRating);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> {
            intent = new Intent(ListRatingActivity.this, MainADMActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("accountID", accountID);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("image", img);
            startActivity(intent);
            finish();
        });

        //RecycleView
        recyclerView = findViewById(R.id.listRating);
        recyclerView.setHasFixedSize(true);
        listProduct = new ArrayList<>();
        data();
        product6Adapter = new Product6Adapter(listProduct, this);
        product6Adapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(product6Adapter);
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

        // Xử lý sự kiện thay đổi dữ liệu searchview:
        editSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextChange(String newText) {
                handler.postDelayed(() -> {
                    filterRating(String.valueOf(spinner_filterrating.getSelectedItem()), newText);
                }, 200);
                return false;
            }
        });

        // Xử lý sự kiện thay đổi dữ liệu spinner:
        spinner_filterrating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handler.postDelayed(() -> {
                    String query = String.valueOf(editSearch.getQuery());
                    filterRating(parent.getItemAtPosition(position).toString(), query);
                }, 200);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void filterRating(String criteria, String query) {
        if (criteria.equals("Mới nhất")) {
            if (query.isEmpty()) {
                ratRef.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listProduct.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            proRef.addValueEventListener(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot node : snapshot.getChildren()) {
                                        Product product = node.getValue(Product.class);
                                        product.setKey(node.getKey());
                                        if (product.getStatus() != -1 && product.getKey().equals(dataSnapshot.child("productID").getValue(String.class))
                                                && !listProduct.contains(product)) {
                                            if(checkExists(product.getKey())) listProduct.add(product);
                                        }
                                    }
                                    Collections.reverse(listProduct);
                                    product6Adapter.notifyDataSetChanged();
                                    totalRating.setText(recyclerView.getAdapter().getItemCount() + " đánh giá từ " + size);
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
            else {
                ratRef.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listProduct.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            proRef.addValueEventListener(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot node : snapshot.getChildren()) {
                                        Product product = node.getValue(Product.class);
                                        product.setKey(node.getKey());
                                        if (product.getStatus() != -1 && product.getKey().equals(dataSnapshot.child("productID").getValue(String.class))
                                                && product.getName().toLowerCase().contains(query.toLowerCase())) {
                                            if(checkExists(product.getKey())) listProduct.add(product);
                                        }
                                    }
                                    Collections.reverse(listProduct);
                                    product6Adapter.notifyDataSetChanged();
                                    totalRating.setText(recyclerView.getAdapter().getItemCount() + " đánh giá từ " + size);
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
        }
        else if (criteria.equals("Cũ nhất")) {
            if (query.isEmpty()) {
                ratRef.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listProduct.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            proRef.addValueEventListener(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot node : snapshot.getChildren()) {
                                        Product product = node.getValue(Product.class);
                                        product.setKey(node.getKey());
                                        if (product.getStatus() != -1 && product.getKey().equals(dataSnapshot.child("productID").getValue(String.class))) {
                                            if(checkExists(product.getKey())) listProduct.add(product);
                                        }
                                    }
                                    product6Adapter.notifyDataSetChanged();
                                    totalRating.setText(recyclerView.getAdapter().getItemCount() + " đánh giá từ " + size);
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
            else {
                ratRef.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listProduct.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            proRef.addValueEventListener(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot node : snapshot.getChildren()) {
                                        Product product = node.getValue(Product.class);
                                        product.setKey(node.getKey());
                                        if (product.getStatus() != -1 && product.getKey().equals(dataSnapshot.child("productID").getValue(String.class))
                                                && product.getName().toLowerCase().contains(query.toLowerCase())) {
                                            if(checkExists(product.getKey())) listProduct.add(product);
                                        }
                                    }
                                    product6Adapter.notifyDataSetChanged();
                                    totalRating.setText(recyclerView.getAdapter().getItemCount() + " đánh giá từ " + size);
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
        }
        else if (criteria.equals("Thấp nhất")) {
            if (query.isEmpty()) {
                proRef.orderByChild("rating").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listProduct.clear();
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Product product = node.getValue(Product.class);
                            product.setKey(node.getKey());
                            if (product.getStatus() != -1 && product.getName().toLowerCase().contains(query.toLowerCase())) {
                                listProduct.add(product);
                            }
                        }
                        product6Adapter.notifyDataSetChanged();
                        totalRating.setText(recyclerView.getAdapter().getItemCount() + " đánh giá từ " + size);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else {
                proRef.orderByChild("rating").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listProduct.clear();
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Product product = node.getValue(Product.class);
                            product.setKey(node.getKey());
                            if (product.getStatus() != -1 && product.getName().toLowerCase().contains(query.toLowerCase())) {
                                listProduct.add(product);
                            }
                        }
                        product6Adapter.notifyDataSetChanged();
                        totalRating.setText(recyclerView.getAdapter().getItemCount() + " đánh giá từ " + size);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
        else {
            if (query.isEmpty()) {
                data();
            } else {
                proRef.orderByChild("rating").addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listProduct.clear();
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Product product = node.getValue(Product.class);
                            product.setKey(node.getKey());
                            if (product.getStatus() != -1 && product.getName().toLowerCase().contains(query.toLowerCase())) {
                                listProduct.add(product);
                            }
                        }
                        Collections.reverse(listProduct);
                        product6Adapter.notifyDataSetChanged();
                        totalRating.setText(recyclerView.getAdapter().getItemCount() + " đánh giá từ " + size);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private boolean checkExists(String key) {
        for(int i = 0; i < listProduct.size(); i++) {
            if(listProduct.get(i).getKey().equals(key)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data() {
        proRef.orderByChild("rating").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                size = 0;
                for (DataSnapshot node : snapshot.getChildren()) {
                    Product product = node.getValue(Product.class);
                    product.setKey(node.getKey());
                    if (product.getStatus() != -1) {
                        listProduct.add(product);
                        size++;
                    }
                }
                Collections.reverse(listProduct);
                product6Adapter.notifyDataSetChanged();
                totalRating.setText(recyclerView.getAdapter().getItemCount() + " đánh giá từ " + size);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Product6Adapter.ItemClickListener itemClickListener = new Product6Adapter.ItemClickListener() {
        @Override
        public void getInfor(String key) {
            intent = new Intent(ListRatingActivity.this, DetailRatingActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("accountID", accountID);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("productID", key);
            intent.putExtra("image", img);
            startActivity(intent);
            finish();
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                intent = new Intent(ListRatingActivity.this, ListAccountActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_lsdh:
                intent = new Intent(ListRatingActivity.this, OrderHistoryActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_tk:
                intent = new Intent(ListRatingActivity.this, RevenueStatisticActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlbl:
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListRatingActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", accountID);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListRatingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(ListRatingActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
