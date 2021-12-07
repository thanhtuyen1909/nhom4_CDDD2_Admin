package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.AccountAdapter;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.AccountHistory;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.data_models.Role;

public class ListAccountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, txtName, txtRole;
    Button btnAdd;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private RecyclerView recyclerView;
    private ArrayList<Account> listAccount;
    private ArrayList<Customer> listCustomer;
    private NavigationView navigationView;
    private AccountAdapter accountAdapter;
    private Intent intent;
    Spinner spinPosition;
    SearchView searchView;
    String accountID = "", username = "", name = "", role = "", img = "";

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference accountRef = db.getReference("Account");
    DatabaseReference roleRef = db.getReference("Role");
    DatabaseReference cusRef = db.getReference("Customer");
    DatabaseReference empRef = db.getReference("Employees");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_account);

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
        subtitleAppbar.setText("Danh sách tài khoản");
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
        btnBack.setOnClickListener(v -> {
            intent = new Intent(ListAccountActivity.this, MainADMActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("accountID", accountID);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("image", img);
            startActivity(intent);
            finish();
        });

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
        ImageView iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);
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

    private AccountAdapter.ItemClickListener itemClickListener = new AccountAdapter.ItemClickListener() {
        @Override
        public void lockAccount(Account item, String status) {
            if(item.getKey().equals(accountID)) {
                showErrorDialog("Không thể sử dụng thao tác này trên tài khoản đang được đăng nhập!");
            } else {
                if (item.getStatus().equals("unlock")) {
                    showWarningDialog(item, "lock");
                } else {
                    accountRef.child(item.getKey()).child("status").setValue("unlock");
                    showSuccesDialog("Mở khóa tài khoản thành công!");
                    pushAccountHistory("Mở khóa tài khoản", "Tên đăng nhập " + item.getUsername());
                }
            }
        }

        @Override
        public void resetPass(Account item) {
            if(item.getKey().equals(accountID)) {
                showErrorDialog("Không thể sử dụng thao tác này trên tài khoản đang được đăng nhập!");
            } else {
                accountRef.child(item.getKey()).child("password").setValue(item.getUsername()).addOnSuccessListener(unused -> {
                    showSuccesDialog("Đặt lại mật khẩu thành công!");
                    pushAccountHistory("Đặt lại mật khẩu cho tài khoản", "Tên đăng nhập " + item.getUsername());
                });
            }
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
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlmgg:
                intent = new Intent(ListAccountActivity.this, RevenueStatisticActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qllsp:
                intent = new Intent(ListAccountActivity.this, ListRatingActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(ListAccountActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
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

    private void showErrorDialog(String notify) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ListAccountActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ListAccountActivity.this).inflate(
                R.layout.layout_warning_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        TextView title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        TextView mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.yes));

        final android.app.AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
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
        mess.setText("Xác nhận khoá tài khoản " + item.getUsername() + "?");
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
            item.setStatus(status);
            accountRef.child(item.getKey()).setValue(item).addOnSuccessListener(unused -> {
                showSuccesDialog("Khoá tài khoản thành công !");
                pushAccountHistory("Khoá tài khoản", "Tên đăng nhập " + item.getUsername());
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
        Spinner spinEmployees = view.findViewById(R.id.spinEmployee);
        TextView txtUsername = view.findViewById(R.id.tv_username);
        ArrayList<Employee> listEmployee = new ArrayList<>();
        ArrayAdapter<Employee> empAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listEmployee);
        empAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinEmployees.setAdapter(empAdapter);
        empRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Employee employee = node.getValue(Employee.class);
                    employee.setId(node.getKey());
                    if(employee.getAccountID().equals("")) {
                        listEmployee.add(employee);
                    }
                }
                empAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        spinEmployees.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Employee employee = (Employee) spinEmployees.getItemAtPosition(position);
                txtUsername.setText(employee.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
            Account account = new Account();
            account.setUsername(txtUsername.getText() + "");
            account.setPassword(txtUsername.getText() + "");
            account.setStatus("unlock");
            Employee employee = (Employee) spinEmployees.getSelectedItem();
            roleRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Role role = node.getValue(Role.class);
                        if (role.getName().equals(employee.getPosition())) {
                            account.setRole_id(role.getId());
                            String key = accountRef.push().getKey();
                            accountRef.child(key).setValue(account).addOnSuccessListener(unused -> {
                                showSuccesDialog("Thêm tài khoản thành công!");
                                pushAccountHistory("Thêm tài khoản", "Tên đăng nhập " + account.getUsername());
                            });
                            empRef.child(employee.getId()).child("accountID").setValue(key);
                            accountAdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

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
}
