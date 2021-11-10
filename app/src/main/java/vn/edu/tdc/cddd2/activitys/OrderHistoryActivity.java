package vn.edu.tdc.cddd2.activitys;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.HistoryOrderAdapter;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.Status;


public class OrderHistoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Intent intent;

    //New data
    Spinner spHistoryCart;
    RecyclerView rcvHistoryCart;
    SearchView searchHistoryCart;
    Button btnHistoryCartSubmit;
    TextView tvHistoryCartPickerTo, tvHistoryCartPickerFrom, tvHistoryCartTotal;

    HistoryOrderAdapter historyOrderAdapter;
    Query queryByOrder, queryStatus;
    ArrayList<Order> arrOrder, arrOrderFilter,arrOrderFilterSpinner;
    ArrayList<String> arrStrStatus;
    ArrayList<Status> arrStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orderhistory);

        setControl();

        arrOrder = new ArrayList<>();
        arrOrderFilter = new ArrayList<>();
        arrOrderFilterSpinner = new ArrayList<>();
        arrStatus = new ArrayList<>();
        arrStrStatus = new ArrayList<>();

        queryByOrder = FirebaseDatabase.getInstance().getReference().
                child("Order").orderByChild("created_at");
        queryStatus = FirebaseDatabase.getInstance().getReference().
                child("Status");

        setRcvManageCard();
        loadDataOrder();

        loadDataStatus();

        //Click filter data spinner
        filterOrderBySpinner();

        //Picker date to
        tvHistoryCartPickerTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(tvHistoryCartPickerTo);
            }
        });
        //Picker date from
        tvHistoryCartPickerFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(tvHistoryCartPickerFrom);
            }
        });
        //Filter by time
        btnHistoryCartSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date dateTo;
                Date dateFrom;
                Date dateOrder;
                try {
                    dateTo = simpleDateFormat.parse(tvHistoryCartPickerTo.getText().toString());
                    dateFrom = simpleDateFormat.parse(tvHistoryCartPickerFrom.getText().toString());
                    if (dateTo.after(dateFrom)) {
                        showDialog("Thời gian không phù hợp");
                    } else {
                        arrOrderFilter.clear();
                        for (int i = 0; i < arrOrderFilterSpinner.size(); i++) {
                            dateOrder = simpleDateFormat.parse(arrOrderFilterSpinner.get(i).getCreated_at().substring(0, 10));
                            if (dateTo.before(dateOrder) && dateFrom.after(dateOrder)
                                    || dateTo.equals(dateOrder)
                                    || dateFrom.equals(dateOrder)) {
                                arrOrderFilter.add(arrOrderFilterSpinner.get(i));
                            }
                        }
                        historyOrderAdapter.notifyDataSetChanged();
                        printTotalNumberCountOrder();
                    }
                } catch (ParseException ex) {
                    showDialog("Thời gian không được để trống");
                }

            }
        });

        //Search by id order
        searchHistoryCart.setOnQueryTextListener(this);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Xử lý sự kiện click button "Xuất thống kê":

    }

    private void printTotalNumberCountOrder(){
        tvHistoryCartTotal.setText("Tìm được: "+arrOrderFilter.size()+"/"+arrOrder.size()+" đơn hàng");
    }

    private void showDialog(String title) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);
        b.setNegativeButton("Xác nhận", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog al = b.create();
        al.show();
    }

    private void showDateDialog(TextView tvData) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
                tvData.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };
        new DatePickerDialog(OrderHistoryActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void filterOrderBySpinner() {
        spHistoryCart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                arrOrderFilter.clear();
                tvHistoryCartPickerFrom.setText("");
                tvHistoryCartPickerTo.setText("");

                arrOrderFilterSpinner.clear();
                if (spHistoryCart.getSelectedItemPosition() == 0) {
                    arrOrderFilter.addAll(arrOrder);
                } else {
                    String id = findIdByValue();
                    for (int x = 0; x < arrOrder.size(); x++) {
                        if (arrOrder.get(x).getStatus() == Integer.parseInt(id)) {
                            arrOrderFilter.add(arrOrder.get(x));
                        }
                    }
                }
                arrOrderFilterSpinner.addAll(arrOrderFilter);
                historyOrderAdapter.notifyDataSetChanged();
                printTotalNumberCountOrder();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private String findIdByValue() {
        String id = null;
        for (int y = 1; y < arrStatus.size(); y++) {
            if (arrStatus.get(y).getValuesStatus().equals(spHistoryCart.getSelectedItem().toString())) {
                id = arrStatus.get(y).getKeyStatus();
            }
        }
        return id;
    }

    private void loadDataOrder() {
        arrOrder.clear();
        queryByOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order;
                    String keyOrder, accountID, address, created_at, name, note, phone, shipperID;
                    int status, total;
                    for (DataSnapshot o : snapshot.getChildren()) {
                        keyOrder = o.getKey();
                        accountID = o.getValue(Order.class).getAccountID();
                        address = o.getValue(Order.class).getAddress();
                        created_at = o.getValue(Order.class).getCreated_at();
                        name = o.getValue(Order.class).getName();
                        note = o.getValue(Order.class).getNote();
                        phone = o.getValue(Order.class).getPhone();
                        shipperID = o.getValue(Order.class).getShipperID();
                        status = o.getValue(Order.class).getStatus();
                        total = o.getValue(Order.class).getTotal();
                        order = new Order(keyOrder, accountID, address, created_at, name, note, phone,
                                shipperID, status, total);

                        arrOrder.add(order);
                        arrOrderFilter.add(order);
                        historyOrderAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setRcvManageCard() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OrderHistoryActivity.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        rcvHistoryCart.setLayoutManager(linearLayoutManager);
        historyOrderAdapter = new HistoryOrderAdapter(OrderHistoryActivity.this
                , arrOrderFilter);
        rcvHistoryCart.setAdapter(historyOrderAdapter);
    }

    private void loadDataStatus() {
        arrStatus.clear();
        queryStatus.addListenerForSingleValueEvent(new ValueEventListener() {
            boolean processDone = false;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && !processDone) {
                    Status status;
                    String keyStatus;
                    Object valuesStatus;

                    for (DataSnapshot s : snapshot.getChildren()) {
                        keyStatus = s.getKey();
                        valuesStatus = s.getValue();
                        status = new Status(keyStatus, valuesStatus);

                        arrStatus.add(status);
                        arrStrStatus.add(valuesStatus + "");
                        processDone = true;
                    }
                }
                if (processDone) {
                    //Add total
                    Status status = new Status("000", "Tất cả đơn hàng");
                    arrStatus.add(0, status);
                    arrStrStatus.add(0, "Tất cả đơn hàng");
                    loadDataSpinner();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadDataSpinner() {
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(OrderHistoryActivity.this
                , android.R.layout.simple_spinner_item
                , arrStrStatus);
        spHistoryCart.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setControl() {
        spHistoryCart = findViewById(R.id.spHistoryCart);
        rcvHistoryCart = findViewById(R.id.rcvHistoryCart);
        searchHistoryCart = findViewById(R.id.searchHistoryCart);
        btnHistoryCartSubmit = findViewById(R.id.btnHistoryCartSubmit);
        tvHistoryCartPickerTo = findViewById(R.id.tvHistoryCartPickerTo);
        tvHistoryCartPickerFrom = findViewById(R.id.tvHistoryCartPickerFrom);
        tvHistoryCartTotal=findViewById(R.id.tvHistoryCartTotal);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Lịch sử đơn hàng");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        arrOrderFilter.clear();
        s =  s.toLowerCase(Locale.getDefault());
        if(s.length()==0){
            arrOrderFilter.addAll(arrOrder);
        }else{
            for(int i=0; i<arrOrder.size();i++){
                //Filter order by name, keyOrder, accountId and phoneNumber
                if(arrOrder.get(i).getName().toLowerCase(Locale.getDefault()).contains(s)
                        || arrOrder.get(i).getKeyOrder().toLowerCase(Locale.getDefault()).contains(s)
                        ||arrOrder.get(i).getAccountID().toLowerCase(Locale.getDefault()).contains(s)
                        ||arrOrder.get(i).getPhone().toLowerCase(Locale.getDefault()).contains(s)){
                    arrOrderFilter.add(arrOrder.get(i));
                }
            }
        }
        historyOrderAdapter.notifyDataSetChanged();
        printTotalNumberCountOrder();
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                intent = new Intent(OrderHistoryActivity.this, ListAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_lsdh:
                break;
            case R.id.nav_qlhd:
                intent = new Intent(OrderHistoryActivity.this, ListInvoiceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_tk:
                intent = new Intent(OrderHistoryActivity.this, RevenueStatisticActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlbl:
                intent = new Intent(OrderHistoryActivity.this, ListRatingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(OrderHistoryActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(OrderHistoryActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(OrderHistoryActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
