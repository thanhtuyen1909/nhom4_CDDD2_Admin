package vn.edu.tdc.ltdd2.activitys;

import android.annotation.SuppressLint;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.adapters.AccountAdapter;
import vn.edu.tdc.ltdd2.data_models.Account;
import vn.edu.tdc.ltdd2.data_models.AccountHistory;
import vn.edu.tdc.ltdd2.data_models.Customer;
import vn.edu.tdc.ltdd2.data_models.Role;

public class ListAccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, txtName, txtRole;
    Button btnAdd;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    private ArrayList<Account> listAccount;
    private ArrayList<Customer> listCustomer;
    NavigationView navigationView;
    private AccountAdapter accountAdapter;
    private Intent intent;
    String accountID = "", role = "", username = "";
    Spinner spinPosition;
    SearchView searchView;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference accountRef = db.getReference("Account");
    DatabaseReference cusRef = db.getReference("Customer");
    DatabaseReference rolepRef = db.getReference("Role");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_account);

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        role = intent.getStringExtra("role");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleDSTK);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        btnAdd = findViewById(R.id.btnAdd);
        searchView = findViewById(R.id.editSearch);
        spinPosition = findViewById(R.id.spinner_position);
        data();
        spinPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Role role = (Role) spinPosition.getItemAtPosition(position);
                int positionID = role.getId();
                String name = searchView.getQuery() + "";
                filter(positionID, name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Role role = (Role) spinPosition.getSelectedItem();
                int positionID = role.getId();
                filter(positionID, newText);
                return false;
            }
        });
        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện click button "+":
        btnAdd.setOnClickListener(v -> addAccount());

        //RecycleView
        recyclerView = findViewById(R.id.listAccount);
        recyclerView.setHasFixedSize(true);
        listAccount = new ArrayList<>();

        accountAdapter = new AccountAdapter(listAccount, this);
        accountAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(accountAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        txtName.setText(accountID);
        txtRole.setText(role);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void filter(int positionID, String name) {
        if (positionID == -1) {
            if (name.equals("")) {
                accountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listAccount.clear();
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Account account = node.getValue(Account.class);
                            account.setKey(node.getKey());
                            listAccount.add(account);
                        }
                        accountAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                accountRef.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listAccount.clear();
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Account account = node.getValue(Account.class);
                            account.setKey(node.getKey());
                            boolean check = false;
                            if (account.getUsername().toLowerCase().trim().contains(name.toLowerCase().trim())) {
                                listAccount.add(account);
                                check = true;
                            }
                            if (!check) {
                                if (account.getRole_id() == 1) {

                                    if (listCustomer.size() > 0) {
                                        for (Customer customer : listCustomer) {
                                            if (customer.getAccountID().equals(node.getKey())) {
                                                Log.d("TAG", "onDataChange: " + customer.getName().toLowerCase().trim());
                                                Log.d("TAG", "name: " + name.toLowerCase().trim());
                                                if (customer.getName().toLowerCase().trim().contains(name.toLowerCase().trim())) {
                                                    listAccount.add(account);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        accountAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        } else {
            if (name.equals("")) {
                accountRef.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listAccount.clear();
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Account account = node.getValue(Account.class);
                            account.setKey(node.getKey());

                            if (account.getUsername().toLowerCase().trim().contains(name.toLowerCase().trim())
                                    && account.getRole_id() == positionID) {
                                listAccount.add(account);

                            }
                        }
                        accountAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                accountRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listAccount.clear();
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Account account = node.getValue(Account.class);
                            account.setKey(node.getKey());
                            boolean check = false;
                            if (account.getUsername().toLowerCase().trim().contains(name.toLowerCase().trim())
                                    && account.getRole_id() == positionID) {
                                listAccount.add(account);
                                check = true;
                            }
                            if (!check) {
                                if (account.getRole_id() == 1) {
                                    if (listCustomer.size() > 0) {
                                        for (Customer customer : listCustomer) {
                                            if (customer.getAccountID().equals(node.getKey())) {
                                                if (customer.getName().toLowerCase().trim().contains(name.toLowerCase().trim())
                                                        && account.getRole_id() == positionID) {
                                                    listAccount.add(account);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        accountAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void data() {
        ArrayList<Role> listPosition = new ArrayList<>();
        ArrayAdapter<Role> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listPosition);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        DatabaseReference roleRef = FirebaseDatabase.getInstance().getReference("Role");
        roleRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listPosition.clear();
                listPosition.add(new Role(-1, "Tất cả"));
                for (DataSnapshot node : snapshot.getChildren()) {
                    Role role = node.getValue(Role.class);
                    listPosition.add(role);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        spinPosition.setAdapter(adapter);

        listCustomer = new ArrayList<>();
        cusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                listCustomer.clear();
                for (DataSnapshot node1 : snapshot1.getChildren()) {
                    Customer customer = node1.getValue(Customer.class);
                    customer.setKey(node1.getKey());
                    listCustomer.add(customer);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private final AccountAdapter.ItemClickListener itemClickListener = new AccountAdapter.ItemClickListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void lockAccount(Account item, String status) {
            if (status.equals("lock")) {
                showWarningDialog(item, status);
            } else {
                item.setStatus(status);
                accountRef.child(item.getKey()).setValue(item);
                accountAdapter.notifyDataSetChanged();
            }
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


        @Override
        public void resetPass(Account item) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("username", item.getUsername());
            map.put("password", item.getUsername());
            map.put("status", item.getStatus());
            map.put("role_id", item.getRole_id());
            accountRef.child(item.getKey()).setValue(map).addOnSuccessListener(unused -> {
                showSuccesDialog("Đặt lại mật khẩu thành công!");
                pushAccountHistory("Đặt lại mật khẩu cho tài khoản", "Tên đăng nhập " + item.getUsername());
            });
            accountAdapter.notifyDataSetChanged();
        }

        @Override
        public void getLayoutHistory(String key) {
            intent = new Intent(ListAccountActivity.this, ListHistoryActivity.class);
            intent.putExtra("key", key);
            startActivity(intent);
        }
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                break;
            case R.id.nav_lsdh:
                intent = new Intent(ListAccountActivity.this, OrderHistoryActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_tk:
                intent = new Intent(ListAccountActivity.this, RevenueStatisticActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlbl:
                intent = new Intent(ListAccountActivity.this, ListRatingActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListAccountActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dx:
                intent = new Intent(ListAccountActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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

    private void showWarningDialog(Account item, String status) {
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
            accountRef.child(item.getKey()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    showSuccesDialog("Khoá tài khoản thành công!");
                    pushAccountHistory("Khóa tài khoản", "Tên đăng nhập " + item.getUsername());
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

    private void addAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ListAccountActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListAccountActivity.this).inflate(
                R.layout.dialog_account,
                findViewById(R.id.layoutDialog)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        Spinner spinRole = view.findViewById(R.id.spinRole);
        EditText edtUsername = view.findViewById(R.id.tv_username);
        ArrayList<Role> listRole = new ArrayList<>();
        ArrayAdapter<Role> empAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listRole);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinRole.setAdapter(empAdapter);
        rolepRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Role role = node.getValue(Role.class);
                    role.setId(Integer.parseInt(node.getKey()));
                    listRole.add(role);
                }
                empAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            Account account = new Account();
            account.setUsername(edtUsername.getText() + "");
            account.setPassword(edtUsername.getText() + "");
            account.setRole_id(((Role)spinRole.getSelectedItem()).getId());
            account.setStatus("unlock");
            accountRef.push().setValue(account).addOnSuccessListener(unused -> {
                showSuccesDialog("Thêm tài khoản thành công !");
                pushAccountHistory("Thêm tài khoản", "Tên đăng nhập " + account.getUsername());
            });
            accountAdapter.notifyDataSetChanged();
        });

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
}
