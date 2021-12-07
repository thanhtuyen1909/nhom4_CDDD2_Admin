package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.BadSellProductAdapter;
import vn.edu.tdc.cddd2.adapters.BestSellProductAdapter;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.Product;

public class RevenueStatisticActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private BarChart chart;
    BestSellProductAdapter adapter;
    BadSellProductAdapter adapter1;
    RecyclerView recyclerView, recyclerView1;
    ArrayList<Product> list, list1;
    private Spinner spinMonth, spinYear;
    private TextView txtTotalSuccess, txtSuccessOrder, txtTotalCanceled, txtCanceledOrder, txtInventory, txtTotalOrder, txtCountOrder, txtCountInventory;

    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
    DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("Products");

    ArrayList<BarEntry> revenue = new ArrayList<>();
    int totalRevenue = 0, month = 0, year = 0, totalSuccess = 0, totalCancel = 0, countOrder = 0, countSuccess = 0, countCancel = 0, countInvetory = 0;
    long totalInventory = 0;
    int isFirst = 0, isFirst1 = 0;
    String accountID = "", username = "", name = "", role = "", img = "";
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_revenues);
        UIinit();
        setEvent();
    }

    private void setEvent() {
        spinYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = Integer.parseInt((String) spinYear.getItemAtPosition(position));
                if (isFirst == 0) {
                    isFirst++;
                } else {
                    chartData((int) spinMonth.getSelectedItemId(), year);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = position;
                if (isFirst1 == 0) {
                    isFirst1++;
                } else {
                    chartData(month, Integer.parseInt("" + spinYear.getSelectedItem()));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void UIinit() {

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("Thống kê doanh thu");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //NavigationView
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        TextView txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        ImageView iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);

        chart = findViewById(R.id.chartStatistics);
        spinMonth = findViewById(R.id.spinner_month);
        spinYear = findViewById(R.id.spinner_year);
        txtTotalSuccess = findViewById(R.id.txtTotalSuccess);
        txtSuccessOrder = findViewById(R.id.txtSuccessOrder);
        txtTotalCanceled = findViewById(R.id.txtTotalCanceled);
        txtCanceledOrder = findViewById(R.id.txtOrderCanceled);
        txtInventory = findViewById(R.id.txtInventory);
        txtTotalOrder = findViewById(R.id.txt_order);
        txtCountOrder = findViewById(R.id.txtCountOrder);
        txtCountInventory = findViewById(R.id.txtCountInventory);
        recyclerView = findViewById(R.id.listProductGoodSale);
        recyclerView1 = findViewById(R.id.listProductBadSale);
        recyclerView.setHasFixedSize(true);
        recyclerView1.setHasFixedSize(true);
        list = new ArrayList<>();
        list1 = new ArrayList<>();
        adapter = new BestSellProductAdapter(list, this);
        adapter1 = new BadSellProductAdapter(list1, this);
        recyclerView.setAdapter(adapter);
        recyclerView1.setAdapter(adapter1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> years = new ArrayList<>();
        Date now = new Date();
        String[] list = now.toString().split(" ");
        int yearNow = Integer.parseInt(list[list.length - 1]);
        for (int i = 2020; i <= yearNow; i++) {
            years.add("" + i);
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinYear.setAdapter(yearAdapter);
        spinMonth.setSelection(0);
        spinYear.setSelection(spinYear.getCount() - 1);
        chartData(month, Integer.parseInt((String) spinYear.getSelectedItem()));
    }

    private void chartData(int month, int year) {
        if (month != 0) {
            int countDay = countDayInMonth(month, year);
            revenue.clear();
            for (int i = 1; i <= countDay; i++) {
                BarEntry entry = new BarEntry(i, 0);
                revenue.add(entry);
            }
            setChartData();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            orderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    totalRevenue = 0;
                    totalSuccess = 0;
                    totalCancel = 0;
                    countOrder = 0;
                    countSuccess = 0;
                    countCancel = 0;
                    for (DataSnapshot node : dataSnapshot.getChildren()) {
                        Order order = node.getValue(Order.class);
                        Date create_at = sdf.parse(order.getCreated_at(), new ParsePosition(0));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(create_at);
                        if (calendar.get(Calendar.MONTH) + 1 == month && calendar.get(Calendar.YEAR) == year) {
                            //set chart data
                            if (order.getStatus() == 8) {
                                int day = calendar.get(Calendar.DAY_OF_MONTH);
                                float oldRevenue = revenue.get(day - 1).getY();
                                revenue.get(day - 1).setY(oldRevenue + ((float) order.getTotal()) / 1000000);
                                setChartData();
                                //count success
                                countSuccess++;
                                totalSuccess += order.getTotal();
                            }
                            //count total
                            countOrder++;
                            totalRevenue += order.getTotal();
                            if (order.getStatus() == 0) {
                                countCancel++;
                                totalCancel += order.getTotal();
                            }

                        }
                    }
                    txtTotalSuccess.setText((double) totalSuccess / 1000000 + "");
                    txtSuccessOrder.setText(countSuccess + " đơn hàng");
                    txtTotalCanceled.setText((double) totalCancel / 1000000 + "");
                    txtCanceledOrder.setText(countCancel + " huỷ đơn");
                    txtTotalOrder.setText("Đặt hàng: " + formatPrice(totalRevenue));
                    txtCountOrder.setText(countOrder + " phiếu đặt");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            revenue.clear();
            for (int i = 1; i <= 12; i++) {
                BarEntry entry = new BarEntry(i, 0);
                revenue.add(entry);
            }
            setChartData();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            orderRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    totalRevenue = 0;
                    totalSuccess = 0;
                    totalCancel = 0;
                    countOrder = 0;
                    countSuccess = 0;
                    countCancel = 0;
                    for (DataSnapshot node : dataSnapshot.getChildren()) {
                        Order order = node.getValue(Order.class);
                        Date create_at = sdf.parse(order.getCreated_at(), new ParsePosition(0));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(create_at);
                        if (calendar.get(Calendar.YEAR) == year) {
                            //set chart data
                            if (order.getStatus() == 8) {
                                int month = calendar.get(Calendar.MONTH) + 1;
                                float oldRevenue = revenue.get(month - 1).getY();
                                revenue.get(month - 1).setY(oldRevenue + ((float) order.getTotal()) / 1000000);
                                setChartData();
                                //count success
                                countSuccess++;
                                totalSuccess += order.getTotal();
                            }
                            //count total
                            countOrder++;
                            totalRevenue += order.getTotal();
                            if (order.getStatus() == 0) {
                                countCancel++;
                                totalCancel += order.getTotal();
                            }

                        }
                    }

                    txtTotalSuccess.setText((double) totalSuccess / 1000000 + "");
                    txtSuccessOrder.setText(countSuccess + " đơn hàng");
                    txtTotalCanceled.setText((double) totalCancel / 1000000 + "");
                    txtCanceledOrder.setText(countCancel + " huỷ đơn");
                    txtTotalOrder.setText("Đặt hàng: " + formatPrice(totalRevenue));
                    txtCountOrder.setText(countOrder + " phiếu đặt");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        totalInventory = 0;
        countInvetory = 0;
        proRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    Product product = node.getValue(Product.class);
                    if (product.getStatus() != -1) {
                        countInvetory += product.getQuantity();
                        totalInventory += product.getImport_price() * product.getQuantity();
                    }
                }
                txtInventory.setText("Giá trị tồn kho : " + formatPrice(totalInventory));
                txtCountInventory.setText(countInvetory + " sản phẩm");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Query query = proRef.orderByChild("sold").limitToLast(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    list.add(0, node.getValue(Product.class));

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        query = proRef.orderByChild("sold").limitToFirst(5);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list1.clear();
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    list1.add(node.getValue(Product.class));

                }
                adapter1.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setChartData() {
        BarDataSet barDataSet = new BarDataSet(revenue, "Doanh thu");
        barDataSet.setColors(new int[]{getResources().getColor(R.color.colorChart)});
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(10f);
        BarData barData = new BarData(barDataSet);
        chart.setFitBars(true);
        chart.setData(barData);
        chart.setVisibleXRangeMaximum(31);
        chart.getDescription().setText("");
        chart.animateY(2000);
        YAxis axisRight = chart.getAxisRight();
        axisRight.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + " triệu";
            }
        });
        YAxis axisLeft = chart.getAxisLeft();
        axisLeft.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + " triệu";
            }
        });

    }

    private int countDayInMonth(int month, int year) {

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0))
                    return 29;
                else
                    return 28;

        }
        return -1;
    }

    private String formatPrice(long price) {
        String stmp = String.valueOf(price);
        int amount;
        amount = (int) (stmp.length() / 3);
        if (stmp.length() % 3 == 0)
            amount--;
        for (int i = 1; i <= amount; i++) {
            stmp = new StringBuilder(stmp).insert(stmp.length() - (i * 3) - (i - 1), ",").toString();
        }
        return stmp + " ₫";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                intent = new Intent(RevenueStatisticActivity.this, ListAccountActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_lsdh:
                intent = new Intent(RevenueStatisticActivity.this, OrderHistoryActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_tk:
                break;
            case R.id.nav_qlbl:
                intent = new Intent(RevenueStatisticActivity.this, ListRatingActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(RevenueStatisticActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(RevenueStatisticActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(RevenueStatisticActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
