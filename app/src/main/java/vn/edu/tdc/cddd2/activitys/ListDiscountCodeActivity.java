package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.DiscountCodeAdapter;
import vn.edu.tdc.cddd2.data_models.AccountHistory;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.DiscountCode;
import vn.edu.tdc.cddd2.data_models.DiscountCode_Customer;
import vn.edu.tdc.cddd2.data_models.Notification;

public class ListDiscountCodeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai b??o bi???n
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, title, mess, filter, txtName, txtRole;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    NavigationView navigationView;
    Intent intent;
    SearchView searchView;
    RecyclerView recyclerView;
    private ArrayList<DiscountCode> listDiscountCode;
    private DiscountCodeAdapter discountCodeAdapter;
    Button btnAdd;
    EditText edtCode, edtValue;
    RadioGroup radType;
    Spinner spinDisType;
    int size = 0;
    boolean check = true;
    private final static FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference codeRef = db.getReference("DiscountCode");
    DatabaseReference notiRef = db.getReference("Notification");
    DatabaseReference cusRef = db.getReference("Customer");
    DatabaseReference ref = db.getReference("DiscountCode_Customer");
    String username = "", name = "", role = "", img = "", accountID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_discountcode);

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
        subtitleAppbar.setText(R.string.title8);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        ImageView iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);

        // Kh???i t???o bi???n
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        searchView = findViewById(R.id.editSearch);
        filter = findViewById(R.id.totalDiscountCode);
        listDiscountCode = new ArrayList<>();

        // X??? l?? s??? ki???n click button "Tr??? l???i":
        btnBack.setOnClickListener(v -> {
            intent = new Intent(ListDiscountCodeActivity.this, MainQLKActivity.class);
            intent.putExtra("accountID", accountID);
            intent.putExtra("username", username);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("image", img);
            startActivity(intent);
            finish();
        });

        // X??? l?? s??? ki???n click button "+":
        btnAdd.setOnClickListener(v -> showDialog("add", null));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listDiscountCode.clear();
                codeRef.addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        DiscountCode code = snapshot.getValue(DiscountCode.class);
                        if (newText.equals("")) {
                            listDiscountCode.add(code);
                        } else {
                            if (code.getCode().toUpperCase().trim().contains(newText.toUpperCase().trim())) {
                                listDiscountCode.add(code);
                            }
                        }
                        discountCodeAdapter.notifyDataSetChanged();
                        filter.setText(discountCodeAdapter.getItemCount() + " m?? gi???m gi?? t??? " + size);
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
                return false;
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listDiscount);
        recyclerView.setHasFixedSize(true);
        listDiscountCode = new ArrayList<>();
        data();
        discountCodeAdapter = new DiscountCodeAdapter(listDiscountCode, this);
        discountCodeAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(discountCodeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void pushNotification(String code, String accountID) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (!code.equals("")) {
            Notification noti = new Notification();
            noti.setTitle("B???n ???? nh???n ???????c 1 m?? gi???m gi??");
            noti.setAccountID(accountID);
            noti.setCreated_at(sdf.format(new Date()));
            noti.setContent("M?? gi???m gi?? c???a b???n l??: " + code);
            //get image uri
            Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.discount_code)).getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(ListDiscountCodeActivity.this.getContentResolver(), bitmap, "Title", null);
            Uri imageUri = Uri.parse(path);
            FirebaseStorage.getInstance().getReference("images/promocodes").child("discount_code").putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    FirebaseStorage.getInstance().getReference("images/promocodes/discount_code").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            noti.setImage(uri.toString());
                            notiRef.push().setValue(noti);
                        }
                    });
                }
            });
        }
    }

    private void createCodeCustomer(String code, String event) {
        switch (event) {
            case "T???t c???":
                cusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {

                            DiscountCode_Customer discus = new DiscountCode_Customer(code, node.getKey());
                            ref.push().setValue(discus);
                            Customer customer = node.getValue(Customer.class);
                            pushNotification(code, customer.getAccountID());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case "Kh??ch h??ng th?????ng":
                cusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Customer cus = node.getValue(Customer.class);
                            if (cus.getType_id().equals("Type")) {
                                DiscountCode_Customer discus = new DiscountCode_Customer(code, node.getKey());
                                ref.push().setValue(discus);
                                Customer customer = node.getValue(Customer.class);
                                pushNotification(code, customer.getAccountID());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case "Kh??ch h??ng B???c":
                cusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Customer cus = node.getValue(Customer.class);
                            if (cus.getType_id().equals("Type1")) {
                                DiscountCode_Customer discus = new DiscountCode_Customer(code, node.getKey());
                                ref.push().setValue(discus);
                                Customer customer = node.getValue(Customer.class);
                                pushNotification(code, customer.getAccountID());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case "Kh??ch h??ng V??ng":
                cusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Customer cus = node.getValue(Customer.class);
                            if (cus.getType_id().equals("Type2")) {
                                DiscountCode_Customer discus = new DiscountCode_Customer(code, node.getKey());
                                ref.push().setValue(discus);
                                Customer customer = node.getValue(Customer.class);
                                pushNotification(code, customer.getAccountID());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            case "Kh??ch h??ng Kim C????ng":
                cusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Customer cus = node.getValue(Customer.class);
                            if (cus.getType_id().equals("Type3")) {
                                DiscountCode_Customer discus = new DiscountCode_Customer(code, node.getKey());
                                ref.push().setValue(discus);
                                Customer customer = node.getValue(Customer.class);
                                pushNotification(code, customer.getAccountID());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            default:
                Calendar cal = Calendar.getInstance();
                int month = cal.get(Calendar.MONTH) + 1;
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
                cusRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Customer cus = node.getValue(Customer.class);
                            Date temp = formatter.parse(cus.getDob(), new ParsePosition(0));

                            if (temp.getMonth() + 1 == month) {
                                DiscountCode_Customer discus = new DiscountCode_Customer(code, node.getKey());
                                ref.push().setValue(discus);
                                Customer customer = node.getValue(Customer.class);
                                pushNotification(code, customer.getAccountID());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
        }
    }

    private DiscountCodeAdapter.ItemClickListener itemClickListener = new DiscountCodeAdapter.ItemClickListener() {
        @Override
        public void deleteProduct(String code) {
            showErrorDialog("B???n c?? mu???n xo?? m?? gi???m gi???", code);
        }

        @Override
        public void editProduct(DiscountCode item) {
            showDialog("update", item);
        }
    };

    private void data() {
        listDiscountCode = new ArrayList<>();
        codeRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                size = 0;
                listDiscountCode.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    DiscountCode code = node.getValue(DiscountCode.class);
                    listDiscountCode.add(code);
                    size++;
                }
                discountCodeAdapter.notifyDataSetChanged();
                filter.setText(discountCodeAdapter.getItemCount() + " m?? gi???m gi?? t??? " + listDiscountCode.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int checkError() {
        if (String.valueOf(edtCode.getText()).equals("")) {
            showWarningDialog("M?? gi???m gi?? kh??ng ???????c ????? tr???ng");
            return -1;
        }
        checkTrungID(String.valueOf(edtCode.getText()));
        if (!check) {
            showWarningDialog("M?? gi???m gi?? kh??ng ???????c tr??ng");
            if (edtCode.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            edtCode.setText("");
            return -1;
        }
        check = false;
        for (int i = 0; i < radType.getChildCount(); i++) {
            RadioButton rad = (RadioButton) radType.getChildAt(i);
            if ((String.valueOf(rad.getText()).equals("VND") ||
                    String.valueOf(rad.getText()).equals("%")) &&
                    rad.isChecked()) {
                if (String.valueOf(edtValue.getText()).equals("")) {
                    showWarningDialog("Gi?? tr??? m?? gi???m gi?? kh??ng ???????c tr???ng");
                    check = true;

                }
            }
        }
        check = false;
        for (int i = 0; i < radType.getChildCount(); i++) {
            RadioButton rad = (RadioButton) radType.getChildAt(i);
            if (rad.isChecked()) {
                check = true;
            }
        }

        if (!check) {
            showWarningDialog("Vui l??ng ch???n h??nh th???c gi???m gi??");
            return -1;
        }

        return 1;
    }

    private void checkTrungID(String code) {
        check = true;
        //Check tr??ng m?? s???n ph???m
        codeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                check = true;
                for (DataSnapshot node : snapshot.getChildren()) {
                    DiscountCode temp = node.getValue(DiscountCode.class);
                    if (temp.getCode().equals(code)) {
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

    private void showDialog(String s, DiscountCode item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDiscountCodeActivity.this, R.style.AlertDialog);
        builder.setTitle(R.string.titleAdd);

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.layout_detail_discountcode, null);
        edtCode = dialoglayout.findViewById(R.id.edtMGG);
        edtValue = dialoglayout.findViewById(R.id.edtGiaTri);
        radType = dialoglayout.findViewById(R.id.radioGroup);
        spinDisType = dialoglayout.findViewById(R.id.loaiKH);

        if (s.equals("update") && item != null) {
            edtCode.setText(item.getCode());
            builder.setTitle(R.string.titleUpdate);
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
            edtCode.setEnabled(false);
            spinDisType.setEnabled(false);
        }

        radType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radTransfee) {
                edtValue.setText("");
                edtValue.setEnabled(false);
            } else {
                edtValue.setEnabled(true);
            }
        });

        builder.setPositiveButton(R.string.okay, (dialog, which) -> {
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
                if (s.equals("add")) {
                    codeRef.push().setValue(discountCode).addOnSuccessListener(unused -> {
                        createCodeCustomer(discountCode.getCode(), discountCode.getEvent());
                        showSuccesDialog("L??u m?? gi???m gi?? th??nh c??ng");
                        if(discountCode.getType().equals("%")) {
                            pushAccountHistory("Th??m m?? gi???m gi??", "M?? gi???m gi?? " + discountCode.getCode()
                                    + "\n??u ????i d??nh cho: " + discountCode.getEvent()
                                    + "\nGi?? tr???: " + discountCode.getValue() + discountCode.getType());
                        }
                        else if (discountCode.getType().equals("VND")) {
                            pushAccountHistory("Th??m m?? gi???m gi??", "M?? gi???m gi?? " + discountCode.getCode()
                                    + "\n??u ????i d??nh cho: " + discountCode.getEvent()
                                    + "\nGi?? tr???: " + discountCode.getValue() + " " + discountCode.getType());
                        }
                        else {
                            pushAccountHistory("Th??m m?? gi???m gi??", "M?? gi???m gi?? " + discountCode.getCode()
                                    + "\n??u ????i d??nh cho: " + discountCode.getEvent()
                                    + "\nGi?? tr???: "  + discountCode.getType());
                        }
                    });
                } else {
                    db.getReference("DiscountCode").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot node : snapshot.getChildren()) {
                                DiscountCode code = node.getValue(DiscountCode.class);
                                if (code.getCode().equals(discountCode.getCode())) {
                                    codeRef.child(node.getKey()).updateChildren(discountCode.toMap());
                                }
                            }
                            showSuccesDialog("C???p nh???t m?? gi???m gi?? th??nh c??ng");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
            dialog.dismiss();
        });

        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());

        builder.setView(dialoglayout);
        builder.show();
    }

    private void showWarningDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDiscountCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListDiscountCodeActivity.this).inflate(
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
            case R.id.nav_qlsp:
                intent = new Intent(ListDiscountCodeActivity.this, ListProductActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlkm:
                intent = new Intent(ListDiscountCodeActivity.this, ListPromoActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dph:
                intent = new Intent(ListDiscountCodeActivity.this, OrderCoordinationActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlmgg:
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListDiscountCodeActivity.this, ListCateActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListDiscountCodeActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dx:
                intent = new Intent(ListDiscountCodeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlh:
                intent = new Intent(ListDiscountCodeActivity.this, ListManuActivity.class);
                intent.putExtra("accountID", accountID);
                intent.putExtra("username", username);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(ListDiscountCodeActivity.this, "Vui l??ng ch???n ch???c n??ng kh??c", Toast.LENGTH_SHORT).show();
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

    private void showErrorDialog(String notify, String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDiscountCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListDiscountCodeActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            DatabaseReference codeRef = db.getReference("DiscountCode");
            codeRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot node : snapshot.getChildren()) {
                        DiscountCode discountCode = node.getValue(DiscountCode.class);
                        if (discountCode.getCode().equals(code)) {
                            codeRef.child(node.getKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    showSuccesDialog("Xo?? m?? gi???m gi?? th??nh c??ng");
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            alertDialog.dismiss();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showSuccesDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListDiscountCodeActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListDiscountCodeActivity.this).inflate(
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

        view.findViewById(R.id.buttonAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public void pushAccountHistory(String action, String detail) {
        // Th??m v??o "L???ch s??? ho???t ?????ng"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setAccountID(accountID);
        accountHistory.setAction(action);
        accountHistory.setDetail(detail);
        accountHistory.setDate(sdf.format(new Date()));
        FirebaseDatabase.getInstance().getReference("AccountHistory").push().setValue(accountHistory);
    }
}
