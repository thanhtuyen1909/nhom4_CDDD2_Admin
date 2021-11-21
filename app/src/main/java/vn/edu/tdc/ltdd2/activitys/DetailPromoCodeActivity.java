package vn.edu.tdc.ltdd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.adapters.ProductPromoAdapter;
import vn.edu.tdc.ltdd2.data_models.AccountHistory;
import vn.edu.tdc.ltdd2.data_models.DetailPromoCode;
import vn.edu.tdc.ltdd2.data_models.Product;

public class DetailPromoCodeActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến
    Handler handler = new Handler();
    Integer percent;
    Toolbar toolbar;
    TextView btnSave, subtitleAppbar, btnCancel, title, mess;
    EditText edtPercent;
    Intent intent;
    RecyclerView recyclerView;
    ArrayList<DetailPromoCode> listProduct;
    ArrayList<Product> listP;
    private Spinner spinProduct, spinGiamGia;
    ProductPromoAdapter productAdapter;
    Button btnAdd;
    String key = "", keyPD = null, username = "", accountID = "";
    boolean check = true;
    ArrayAdapter<Product> spinAdapter;
    DatabaseReference promoDetailRef = FirebaseDatabase.getInstance().getReference("Offer_Details");
    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Products");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_promocode);
        promoDetailRef.keepSynced(true);
        productRef.keepSynced(true);
        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        key = getIntent().getStringExtra("key");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleLayoutCTKM);

        // Khởi tạo biến
        btnSave = findViewById(R.id.txtSave);
        btnCancel = findViewById(R.id.txtCancel);
        btnAdd = findViewById(R.id.btnAdd);
        listProduct = new ArrayList<>();
        listP = new ArrayList<>();
        spinProduct = findViewById(R.id.edtSP);
        spinGiamGia = findViewById(R.id.edtPKM);

        // Xử lý sự kiện click button:
        btnSave.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        productAdapter = new ProductPromoAdapter(listProduct, this);
        productAdapter.setItemClickListener(itemClickListener);
        spinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listP);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinProduct.setAdapter(spinAdapter);
        data();
        recyclerView.setAdapter(productAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    private final ProductPromoAdapter.ItemClickListener itemClickListener = new ProductPromoAdapter.ItemClickListener() {
        @SuppressLint({"NewApi", "NotifyDataSetChanged"})
        @Override
        public void deleteProductInPromo(String s) {
            listProduct.removeIf(listProduct -> listProduct.getProductID().equals(s));
            productAdapter.notifyDataSetChanged();
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void editProductInPromo(String s) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailPromoCodeActivity.this, R.style.AlertDialog);
            builder.setTitle(R.string.titlePercent);

            LayoutInflater inflater = getLayoutInflater();
            View dialoglayout = inflater.inflate(R.layout.text_input_percent, null);
            edtPercent = dialoglayout.findViewById(R.id.edtPercent);

            builder.setPositiveButton(R.string.okay, (dialog, which) -> {
                for (DetailPromoCode dp : listProduct) {
                    if(dp.getProductID().equals(s)) {
                        dp.setPercentSale(Integer.parseInt(edtPercent.getText().toString()));
                    }
                }
                productAdapter.notifyDataSetChanged();
                dialog.dismiss();
            });

            builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

            builder.setView(dialoglayout);
            builder.show();
        }
    };

    private void data() {
        // List:
        promoDetailRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listProduct.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("offerID").getValue(String.class).equals(key)) {
                        DetailPromoCode dp = snapshot.getValue(DetailPromoCode.class);
                        dp.setKey(snapshot.getKey());
                        listProduct.add(dp);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Spinner:
        productRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listP.clear();
                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                    Product product = snapshot1.getValue(Product.class);
                    product.setKey(snapshot1.getKey());
                    if(product.getStatus() != -1) {
                        listP.add(product);
                    }
                }
                spinAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            // Xoá hết detail trước đó trên dtb
            promoDetailRef.addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("offerID").getValue(String.class).equals(key)) {
                            promoDetailRef.child(snapshot.getKey()).removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // Thêm lại list lên dtb
            for(DetailPromoCode dp : listProduct) {
                DetailPromoCode dp1 = new DetailPromoCode();
                dp1.setOfferID(dp.getOfferID());
                dp1.setPercentSale(dp.getPercentSale());
                dp1.setProductID(dp.getProductID());
                promoDetailRef.push().setValue(dp1).addOnSuccessListener(unused -> pushAccountHistory("Thêm sản phẩm vào khuyến mãi", "Mã khuyến mãi " + dp1.getOfferID() + "\n" + "Mã sản phẩm " + dp.getProductID()));
            }

            showSuccesDialog("Cập nhật chi tiết khuyến mãi thành công!");
        }
        else if (v == btnAdd) {
            // Kiểm tra trùng sp
            keyPD = ((Product) spinProduct.getSelectedItem()).getKey();
            percent = Integer.parseInt(spinGiamGia.getSelectedItem().toString().substring(0, spinGiamGia.getSelectedItem().toString().length() - 1));
            checkTrung(keyPD);
            handler.postDelayed(() -> {
                // Add xuống list
                if (check) {
                    DetailPromoCode dp = new DetailPromoCode();
                    dp.setOfferID(key);
                    dp.setPercentSale(percent);
                    dp.setProductID(keyPD);
                    listProduct.add(dp);
                    productAdapter.notifyDataSetChanged();
                } else {
                    showWarningDialog("Khuyến mãi đã được áp dụng trên sản phẩm này!");
                }
            }, 200);
        }
        else {
            showErrorDialog();
        }
    }

    // Thông báo:
    private void showWarningDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailPromoCodeActivity.this).inflate(
                R.layout.layout_warning_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(s);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailPromoCodeActivity.this).inflate(
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
            finish();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailPromoCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailPromoCodeActivity.this).inflate(
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
            finish();
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void checkTrung(String s) {
        check = true;
        for (DetailPromoCode p : listProduct) {
            if(p.getProductID().equals(s)) {
                check = false;
                break;
            }
        }
    }
}
