package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.AccountAdapter;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.History;
import vn.edu.tdc.cddd2.data_models.Role;

public class ListAccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar, title;
    private EditText edtHoten, edtMatKhau,edtRole,ID;
    private Spinner  spinnerRole1;
    private Button btnAdd;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Account> listAccount;
    private ArrayList<Role> dsRole;
    private ArrayAdapter<Role> adapter;


    private NavigationView navigationView;
    private AccountAdapter accountAdapter;
    private Intent intent;
    private SearchView searchView;
    private DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_account);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Danh sách tài khoản");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        spinnerRole1 = findViewById(R.id.spinner_cata);

        dsRole = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, dsRole);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole1.setAdapter(adapter);
        dataRole();
        //xử lý spinner và đổ dữ liệu recycleview
        spinnerRole1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillter(dsRole.get(position).getId(), searchView.getQuery().toString());
                Log.d("TAG", "onItemSelected: " + dsRole.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                //Toast.makeText(ListAccountActivity.this, "Thêm tài khoản", Toast.LENGTH_SHORT).show();
                showAddAccountDialog();
            }
        });
        //search
        searchView = findViewById(R.id.editSearch);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                accountAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                accountAdapter.getFilter().filter(newText);
                return false;
            }
        });
        //RecycleView
        recyclerView = findViewById(R.id.listAccount);
        recyclerView.setHasFixedSize(true);
        listAccount = new ArrayList<>();
        // listAccount1=new ArrayList<>();

        //data();
        accountAdapter = new AccountAdapter(listAccount, this);
        accountAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(accountAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void fillter(int roleID, String query) {
        if (roleID == -1) {
            if (query.equals("")) {
                data();
                Log.d("TAG", "fillter: ");
            } else {
                myRef.child("Account").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listAccount.clear();
                        for (DataSnapshot DSAccount : dataSnapshot.getChildren()) {
                            Account account = DSAccount.getValue(Account.class);
                            if (account.getUsername().toLowerCase().trim().contains(query.toLowerCase().trim())) {
                                listAccount.add(account);
                            } else {
                                if (account.getRole_id() == 1 && account.getUsername().equals("")) {
                                    myRef.child("Customer").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot DSCustomer : dataSnapshot.getChildren()) {
                                                Customer customer = DSCustomer.getValue(Customer.class);
                                                customer.setKey(DSCustomer.getKey());
                                                if (customer.getAccountID().equals(account.getAccountID())) {
                                                    account.setUsername(customer.getName());
                                                    listAccount.add(account);
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                accountAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        } else {
            if (query.equals("")) {
                myRef.child("Account").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listAccount.clear();
                        for (DataSnapshot DSAccount : dataSnapshot.getChildren()) {
                            Account account = DSAccount.getValue(Account.class);
                            if (account.getRole_id() == roleID) {
                                listAccount.add(account);
                            }

                        }
                        accountAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                Log.d("TAG", "fillter: ");
            } else {
                myRef.child("Account").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        listAccount.clear();
                        for (DataSnapshot DSAccount : dataSnapshot.getChildren()) {
                            Account account = DSAccount.getValue(Account.class);
                            if (account.getUsername().toLowerCase().trim().contains(query.toLowerCase().trim()) && account.getRole_id() == roleID) {
                                listAccount.add(account);
                            } else {
                                if (account.getRole_id() == 1 && account.getUsername().equals("")) {
                                    myRef.child("Customer").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot DSCustomer : dataSnapshot.getChildren()) {
                                                // listAccount.clear();
                                                Customer customer = DSCustomer.getValue(Customer.class);
                                                customer.setKey(DSCustomer.getKey());
                                                if (customer.getAccountID().equals(account.getAccountID())) {
                                                    account.setUsername(customer.getName());
                                                    listAccount.add(account);
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                                accountAdapter.notifyDataSetChanged();
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

    private void data() {
        myRef.child("Account").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // listAccount.clear();
                for (DataSnapshot DSAccount : dataSnapshot.getChildren()) {
                    Account account = DSAccount.getValue(Account.class);
                    account.setAccountID(DSAccount.getKey());
                    listAccount.add(account);
                }
                accountAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

                    private AccountAdapter.ItemClickListener itemClickListener = new AccountAdapter.ItemClickListener() {
        @Override
        public void getInfor(Account item) {
            Toast.makeText(ListAccountActivity.this, item.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void getLayoutHistory(Account item) {

               intent = new Intent(ListAccountActivity.this, ListHistoryActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
               startActivity(intent);
        }

        @Override
        public void lockAccount(Account item, String status) {
            if(status.equals("lock")){
                showWarningDialog(item,status);
            }
            else {
                item.setStatus(status);
                myRef.child("Account").child(item.getAccountID()).setValue(item);
                accountAdapter.notifyDataSetChanged();
                showSuccesDialog("Mở khóa tài khoản thành công");
            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                break;
            case R.id.nav_lsdh:
                intent = new Intent(ListAccountActivity.this, OrderHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlhd:
                intent = new Intent(ListAccountActivity.this, ListInvoiceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(ListAccountActivity.this, RevenueStatisticActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListAccountActivity.this, ListRatingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListAccountActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(ListAccountActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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

    private void showAddAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListAccountActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListAccountActivity.this).inflate(
                R.layout.layout_account_dialog,
                findViewById(R.id.layoutDialogAccount)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle1);
        title.setText(R.string.title);
        edtHoten = view.findViewById(R.id.edtHoten);
        edtMatKhau = view.findViewById(R.id.edtMatKhau);
       edtRole=view.findViewById(R.id.edtRole);




        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.titleAdd));
        ((TextView) view.findViewById(R.id.buttonCancel)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonCancel).setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        //xư lý button them
        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            Map<String, Object> map = new HashMap<>();
            myRef.child("Account").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    map.put("username", edtHoten.getText().toString());
                    map.put("password", edtMatKhau.getText().toString());
                    map.put("role_id",edtRole.getText().toString());
                    map.put("status", "unlock");
                    myRef.child("Account").push().setValue(map);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    private void showWarningDialog(Account item,String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListAccountActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListAccountActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        TextView mess = view.findViewById(R.id.textMessage);
        mess.setText("Xác nhận khoá tài khoản?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            item.setStatus(status);
            myRef.child("Account").child(item.getAccountID()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showSuccesDialog("Khoá tài khoản thành công !");
                }
            });
            accountAdapter.notifyDataSetChanged();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    private void showSuccesDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListAccountActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListAccountActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        TextView mess = view.findViewById(R.id.textMessage);
        mess.setText(s);
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
    private void dataRole() {
        myRef.child("Role").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dsRole.clear();
                dsRole.add(new Role(-1, "Tất cả"));
                for (DataSnapshot DSRole : dataSnapshot.getChildren()) {
                    Role role = DSRole.getValue(Role.class);
                    dsRole.add(role);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
