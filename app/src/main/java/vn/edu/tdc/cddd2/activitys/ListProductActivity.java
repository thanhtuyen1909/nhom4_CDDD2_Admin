package vn.edu.tdc.cddd2.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.cddd2.DAO.DAOProduct;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.ProductAdapter;
import vn.edu.tdc.cddd2.data_models.Category;
import vn.edu.tdc.cddd2.data_models.Manufacture;
import vn.edu.tdc.cddd2.data_models.Product;

public class ListProductActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    // Khai báo biến
    Toolbar toolbar;
    Handler handler = new Handler();
    TextView btnBack, totalProduct;
    SearchView searchView;
    Button btnAdd;
    private Spinner spinCate, spinManu;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    private ArrayList<Product> listProduct;
    private ArrayList<Manufacture> listManu;
    private ArrayList<Category> listCate;
    NavigationView navigationView;
    private ProductAdapter proAdapter;
    private Intent intent;
    TextView title, mess;
    private static FirebaseDatabase db = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_product);
        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        searchView = findViewById(R.id.editSearch);
        totalProduct = findViewById(R.id.totalProduct);
        spinCate = findViewById(R.id.spinner_cata);
        spinManu = findViewById(R.id.spinner_manu);
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
                intent = new Intent(ListProductActivity.this, DetailProductActivity.class);
                startActivity(intent);
            }
        });
        spinCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Manufacture manu = (Manufacture) spinManu.getSelectedItem();
                        Category cate = (Category) spinCate.getSelectedItem();
                        String query = String.valueOf(searchView.getQuery());
                        listProduct.clear();
                        filterProduct(cate.getKey(),manu.getKey(),query,1);
                    }
                }, 200);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinManu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Manufacture manu = (Manufacture) spinManu.getSelectedItem();
                        Category cate = (Category) spinCate.getSelectedItem();
                        String query = String.valueOf(searchView.getQuery());
                        listProduct.clear();
                        filterProduct(cate.getKey(),manu.getKey(),query,-1);
                    }
                }, 200);;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listProduct = new ArrayList<>();

        proAdapter = new ProductAdapter(listProduct,this);
        data();
        proAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(proAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Xự kiện searchview:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                proAdapter.getFilter().filter(newText);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Manufacture manu = (Manufacture) spinManu.getSelectedItem();
                        Category cate = (Category) spinCate.getSelectedItem();
                        String query = String.valueOf(searchView.getQuery());
                        listProduct.clear();
                        filterProduct(cate.getKey(),manu.getKey(),query,-1);
                    }
                }, 200);

                return false;
            }
        });
    }
    private void filterProduct(String category_id,String manu_id,String query,int isFirst){
        DatabaseReference ref = db.getReference("Products");
        ref.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Product product = snapshot.getValue(Product.class);
                product.setKey(snapshot.getKey());
                if(product != null){
                    if(category_id.equals("") && manu_id.equals("") && query.equals("")){
                              if(isFirst == 1){
                                  listProduct.add(product);
                              }

                    }else{
                        if(!category_id.equals("")){

                            if(!manu_id.equals("")){

                                if(query.equals("")){
                                    if(manu_id.equals(product.getManu_id()) && category_id.equals(product.getCategory_id())){
                                        listProduct.add(product);
                                    }
                                }else {
                                    if(product.getName().toLowerCase().contains(query.toLowerCase())
                                            && manu_id.equals(product.getManu_id())
                                            && category_id.equals(product.getCategory_id())){
                                        listProduct.add(product);
                                    }
                                }
                            }else {

                                if(query.equals("")){
                                    if(category_id.equals(product.getCategory_id())){
                                        Log.d("aaa", "onChildAdded: "+product.getName());
                                        listProduct.add(product);
                                    }
                                }else {
                                    if(product.getName().toLowerCase().contains(query.toLowerCase())
                                            && category_id.equals(product.getCategory_id())){
                                        listProduct.add(product);
                                    }
                                }
                            }


                        }else {
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
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data(){
        DatabaseReference ref = db.getReference("Products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listProduct.clear();
                for(DataSnapshot node : snapshot.getChildren()){
                    Product product = node.getValue(Product.class);
                    product.setKey(node.getKey());
                    listProduct.add(product);
                }
                proAdapter.notifyDataSetChanged();
                totalProduct.setText(proAdapter.getItemCount() + " sản phẩm từ " + listProduct.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference refManu = db.getReference("Manufactures");
        DatabaseReference refCate = db.getReference("Categories");
        listManu = new ArrayList<Manufacture>();
        listCate = new ArrayList<Category>();
        ArrayAdapter manuAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, listManu);

        ArrayAdapter cateAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, listCate);

        manuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinManu.setAdapter(manuAdapter);
        cateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCate.setAdapter(cateAdapter);
        listManu.add(new Manufacture("","Tất cả",""));
        listCate.add(new Category("","Tất cả",""));
        refManu.addListenerForSingleValueEvent(new ValueEventListener() {
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
        refCate.addListenerForSingleValueEvent(new ValueEventListener() {
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

    private ProductAdapter.ItemClickListener itemClickListener = new ProductAdapter.ItemClickListener() {
        @Override
        public void deleteProduct(String key) {
            showWarningDialog(key);
        }

        @Override
        public void editProduct(Product item) {
            intent = new Intent(ListProductActivity.this,DetailEditProductActivity.class);
            intent.putExtra("item", (Parcelable) item);
            startActivity(intent);
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qlsp:
                break;
            case R.id.nav_qlkm:
                intent = new Intent(ListProductActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
                intent = new Intent(ListProductActivity.this, OrderProcessActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(ListProductActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListProductActivity.this, ListCataActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListProductActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListProductActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(ListProductActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListProductActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showWarningDialog(String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListProductActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListProductActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận xoá sản phẩm?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            DAOProduct dao = new DAOProduct();
            dao.delete(key).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showSuccesDialog();
                }
            });
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListProductActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListProductActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText("Xoá sản phẩm thành công!");
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}