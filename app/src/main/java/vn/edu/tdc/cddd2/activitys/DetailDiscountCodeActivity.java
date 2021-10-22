package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.CustomerAdapter;
import vn.edu.tdc.cddd2.adapters.Product1Adapter;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.DiscountCode;
import vn.edu.tdc.cddd2.data_models.Product;

public class DetailDiscountCodeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private EditText edtCode, edtValue;
    private RadioGroup radType;
    private TextView btnSave, subtitleAppbar, btnCancel, btnAll;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Intent intent;
    private RecyclerView recyclerView;
    private ArrayList<Customer> listCustomer;
    private Spinner spinDisType;
    private CustomerAdapter customerAdapter;
    private final static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private TextView title, mess;
    boolean check = true;
    String type = "";
    DiscountCode item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_discountcode);
        item = getIntent().getParcelableExtra("item");
        type = getIntent().getExtras().getString("type");
        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Chi tiết mã giảm giá");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        btnAll = findViewById(R.id.btnAll);
        edtCode = findViewById(R.id.edtMGG);
        edtValue = findViewById(R.id.edtGiaTri);
        radType = findViewById(R.id.radioGroup);
        spinDisType = findViewById(R.id.loaiKH);

        listCustomer = new ArrayList<>();
        data();

        // Xử lý sự kiện click button "Lưu":
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkError() == 1) {
                    DiscountCode discountCode = new DiscountCode();
                    discountCode.setCode(String.valueOf(edtCode.getText()));
                    discountCode.setValue(Integer.parseInt(String.valueOf(edtValue.getText())));
                    for (int i = 0; i < radType.getChildCount(); i++) {
                        RadioButton rad = (RadioButton) radType.getChildAt(i);
                        if (rad.isChecked()) {
                            discountCode.setType(String.valueOf(rad.getText()));
                        }
                    }
                    discountCode.setEvent(String.valueOf(spinDisType.getSelectedItem()));
                    if (type.equals("add")) {
                        db.getReference("DiscountCode").push().setValue(discountCode).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                showSuccesDialog("Lưu mã giảm giá thành công");
                            }
                        });
                    } else {
                        db.getReference("DiscountCode").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot node : snapshot.getChildren()) {
                                    DiscountCode code = node.getValue(DiscountCode.class);
                                    if (code.getCode().equals(discountCode.getCode())) {
                                        db.getReference("DiscountCode").child(node.getKey()).updateChildren(discountCode.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                showSuccesDialog("Cập nhật mã giảm giá thành công");
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
                }
            }
        });
        radType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radTransfee) {
                    edtValue.setText("");
                    edtValue.setEnabled(false);
                } else {
                    edtValue.setEnabled(true);
                }
            }
        });
        // Xử lý sự kiện click button "Chọn tất cả":
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailDiscountCodeActivity.this, "Chọn tất cả", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện click button "Huỷ":
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        //RecycleView
//        recyclerView = findViewById(R.id.listCustomer);
//        recyclerView.setHasFixedSize(true);
//        data();
//        customerAdapter = new CustomerAdapter(listCustomer,this);
//        customerAdapter.setItemClickListener(itemClickListener);
//        recyclerView.setAdapter(customerAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private CustomerAdapter.ItemClickListener itemClickListener = new CustomerAdapter.ItemClickListener() {
        @Override
        public void getInfor(Customer item) {
            Toast.makeText(DetailDiscountCodeActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void data() {
        if (type.equals("update") && item != null) {
            edtCode.setText(item.getCode());
            edtValue.setText(String.valueOf(item.getValue()));
            for (int i = 0; i < radType.getChildCount(); i++) {
                RadioButton rad = (RadioButton) radType.getChildAt(i);
                if (String.valueOf(rad.getText()).equals(item.getType())) {
                    rad.setChecked(true);
                }
            }
            for (int i = 0; i < spinDisType.getCount(); i++) {
                String event = String.valueOf(spinDisType.getItemAtPosition(i));
                if (event.equals(item.getEvent())) {
                    spinDisType.setSelection(i);
                }
            }
        }
    }

    private int checkError() {
        if (String.valueOf(edtCode.getText()).equals("")) {
            showWarningDialog("Mã giảm giá không được để trống");
            return -1;
        }
        if (!type.equals("update")) {
            checkTrungID(String.valueOf(edtCode.getText()));
            if (!check) {
                showWarningDialog("Mã giảm giá không được trùng");
                if (edtCode.requestFocus()) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
                edtCode.setText("");
                return -1;
            }
        }
        check = false;
        for (int i = 0; i < radType.getChildCount(); i++) {
            RadioButton rad = (RadioButton) radType.getChildAt(i);
            if (String.valueOf(rad.getText()).equals("VND") ||
                    String.valueOf(rad.getText()).equals("%")) {
                if (String.valueOf(edtValue.getText()).equals("")) {
                    showWarningDialog("Giá trị mã giảm giá không được trống");
                    check = true;

                }
            }
        }
        if (!type.equals("update")) {
            if (!check) {
                showWarningDialog("Vui lòng chọn hình thức giảm giá");
                return -1;
            }
        }
        return 1;
    }

    private void checkTrungID(String code) {
        check = true;
        //Check trùng mã sản phẩm
        DatabaseReference ref = db.getReference("DiscountCode");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                check = true;
                for (DataSnapshot node : snapshot.getChildren()) {
                    DiscountCode temp = node.getValue(DiscountCode.class);
                    if (temp.getCode() == code) {

                        check = false;
                        break;
                    }
                }
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
                intent = new Intent(DetailDiscountCodeActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlkm:
                intent = new Intent(DetailDiscountCodeActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dph:
//                Intent intent = new Intent(ListManuActivity.this, ListProductActivity.class);
//                startActivity(intent);
                Toast.makeText(DetailDiscountCodeActivity.this, "Điều phối hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(DetailDiscountCodeActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(DetailDiscountCodeActivity.this, ListCataActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(DetailDiscountCodeActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(DetailDiscountCodeActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_qlh:
                intent = new Intent(DetailDiscountCodeActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            default:
                Toast.makeText(DetailDiscountCodeActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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

    private void showWarningDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailDiscountCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailDiscountCodeActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.yes));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
        });


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailDiscountCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailDiscountCodeActivity.this).inflate(
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
            finish();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
