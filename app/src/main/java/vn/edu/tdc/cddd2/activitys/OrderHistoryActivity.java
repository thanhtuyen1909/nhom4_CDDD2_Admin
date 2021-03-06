package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.HistoryOrderAdapter;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.ReportOrder;
import vn.edu.tdc.cddd2.data_models.Status;


public class OrderHistoryActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener {
    // Khai b??o bi???n
    private Toolbar toolbar;
    private TextView btnBack, subtitleAppbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView navigationView;
    private Intent intent;
    String accountID = "", username = "", name = "", role = "", img = "";

    //New data
    Spinner spHistoryCart;
    RecyclerView rcvHistoryCart;
    SearchView searchHistoryCart;
    Button btnHistoryCartSubmit;
    TextView tvHistoryCartPickerTo, tvHistoryCartPickerFrom, tvHistoryCartTotal, tvHistoryCartRender;

    HistoryOrderAdapter historyOrderAdapter;
    Query queryByOrder, queryStatus;
    ArrayList<Order> arrOrder, arrOrderFilter, arrOrderFilterSpinner;
    ArrayList<String> arrStrStatus;
    ArrayList<Status> arrStatus;

    //Define property of sheets
    Cell cell = null;
    CellStyle cellStyleTitle, cellStyleValue, cellStyleTitleMain, cellStyleTitleResult, cellStyleValueResult;
    Row row;
    Sheet sheet;
    Workbook wb;
    Font fontTitleMain, fontTitle;
    ReportOrder reportOrder;
    boolean isFilter=true;

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
        reportOrder = new ReportOrder();
        //Define of sheets
        wb = new HSSFWorkbook();
        fontTitle = wb.createFont();
        fontTitleMain = wb.createFont();
        styleFont();
        styleCell();

        sheet = wb.createSheet("HO?? ????N");

        //Query get data
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
        tvHistoryCartPickerTo.setOnClickListener(view -> {
            isFilter=false;
            showDateDialog(tvHistoryCartPickerTo);
        });
        //Picker date from
        tvHistoryCartPickerFrom.setOnClickListener(view -> {
            isFilter=false;
            showDateDialog(tvHistoryCartPickerFrom);
        });
        //Filter by time
        btnHistoryCartSubmit.setOnClickListener(view -> {
            isFilter=true;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date dateTo;
            Date dateFrom;
            Date dateOrder;
            try {
                dateTo = simpleDateFormat.parse(tvHistoryCartPickerTo.getText().toString());
                dateFrom = simpleDateFormat.parse(tvHistoryCartPickerFrom.getText().toString());
                if (dateTo.after(dateFrom)) {
                    showDialog("Th???i gian kh??ng ph?? h???p");
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
                showDialog("Th???i gian kh??ng ???????c ????? tr???ng");
            }

        });

        //Search by id order
        searchHistoryCart.setOnQueryTextListener(this);

        // X??? l?? s??? ki???n click button "Tr??? l???i":
        btnBack.setOnClickListener(v -> {
            intent = new Intent(OrderHistoryActivity.this, MainADMActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("accountID", accountID);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("image", img);
            startActivity(intent);
            finish();
        });

        // X??? l?? s??? ki???n click button "Xu???t th???ng k??":
        tvHistoryCartRender.setOnClickListener(v -> {
            //If user isn't selected fliter
               if(isFilter){
                   //Data not null
                   if(arrOrderFilter.size()==0){
                       showDialog("D??? li???u r???ng in th???t b???i");
                   }else{
                       renderDataExcel();
                       wb = new HSSFWorkbook();
                       fontTitle = wb.createFont();
                       fontTitleMain = wb.createFont();
                       styleFont();
                       styleCell();
                       sheet = wb.createSheet("HO?? ????N");
                   }
               }else{
                   showDialog("B???n ch??a ch???n l???c theo th???i gian n??y");
               }
        });
    }

    private void renderDataExcel(){
        //Handle name path
        String strPathName= "BaoCao";
        String strDateTo = tvHistoryCartPickerTo.getText().toString();
        String strDateFrom = tvHistoryCartPickerFrom.getText().toString();
        if(!strDateTo.equals("")&&!strDateFrom.equals("")){
            strDateTo= strDateTo.replace("/","_");
            strDateFrom = strDateFrom.replace("/","_");
            strPathName = "BaoCao_"+strDateTo+"-"+strDateFrom;
        }

        setShowTitleMain(0,3,"TH???NG K?? HO?? ????N T??? NG??Y "+tvHistoryCartPickerTo.getText().toString()
                +"-"+tvHistoryCartPickerFrom.getText().toString());

        //SET ROW 1 TITLE
        setCellDataTitle();
        //SET VALUE
        setRowDataValue();
        //SET ROW RESULT
        setRowTitleResult();
        //SET VALUE RESULT
        setRowValueResult();
        //Set size column
        setSizeColumn();
        //Printf
        File file = new File(getExternalFilesDir(null),strPathName+".xls");
        FileOutputStream outputStream =null;

        try {
            outputStream=new FileOutputStream(file);
            wb.write(outputStream);
            showDialog("In file excel th??nh c??ng");
        } catch (java.io.IOException e) {
            e.printStackTrace();

            showDialog("In file excel th??nh c??ng");
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void printTotalNumberCountOrder() {
        tvHistoryCartTotal.setText("T??m ???????c: " + arrOrderFilter.size() + "/" + arrOrder.size() + " ????n h??ng");
    }

    private void showDialog(String title) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);
        b.setNegativeButton("X??c nh???n", (dialog, id) -> dialog.cancel());
        AlertDialog al = b.create();
        al.show();
    }

    private void showDateDialog(TextView tvData) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, i, i1, i2) -> {
            calendar.set(Calendar.YEAR, i);
            calendar.set(Calendar.MONTH, i1);
            calendar.set(Calendar.DAY_OF_MONTH, i2);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            tvData.setText(simpleDateFormat.format(calendar.getTime()));
        };
        new DatePickerDialog(OrderHistoryActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void filterOrderBySpinner() {
        spHistoryCart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                isFilter=true;
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
                        order = new Order(keyOrder, accountID, address, created_at, note, shipperID,status,
                                total, phone, name);

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
                    Status status = new Status("000", "T???t c??? ????n h??ng");
                    arrStatus.add(0, status);
                    arrStrStatus.add(0, "T???t c??? ????n h??ng");
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

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        spHistoryCart = findViewById(R.id.spHistoryCart);
        rcvHistoryCart = findViewById(R.id.rcvHistoryCart);
        searchHistoryCart = findViewById(R.id.searchHistoryCart);
        btnHistoryCartSubmit = findViewById(R.id.btnHistoryCartSubmit);
        tvHistoryCartPickerTo = findViewById(R.id.tvHistoryCartPickerTo);
        tvHistoryCartPickerFrom = findViewById(R.id.tvHistoryCartPickerFrom);
        tvHistoryCartTotal = findViewById(R.id.tvHistoryCartTotal);
        tvHistoryCartRender = findViewById(R.id.tvHistoryCartRender);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText("L???ch s??? ????n h??ng");
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Kh???i t???o bi???n
        btnBack = findViewById(R.id.txtBack);

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        TextView txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        ImageView iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        arrOrderFilter.clear();
        s = s.toLowerCase(Locale.getDefault());
        if (s.length() == 0) {
            arrOrderFilter.addAll(arrOrder);
        } else {
            for (int i = 0; i < arrOrder.size(); i++) {
                //Filter order by name, keyOrder, accountId and phoneNumber
                if (arrOrder.get(i).getName().toLowerCase(Locale.getDefault()).contains(s)
                        || arrOrder.get(i).getOrderID().toLowerCase(Locale.getDefault()).contains(s)
                        || arrOrder.get(i).getAccountID().toLowerCase(Locale.getDefault()).contains(s)
                        || arrOrder.get(i).getPhone().toLowerCase(Locale.getDefault()).contains(s)) {
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

    private void styleFont() {
        //Font title main
        fontTitleMain = wb.createFont();
        fontTitleMain.setBold(true);
        fontTitleMain.setColor(IndexedColors.RED.getIndex());
        //Font title
        fontTitle = wb.createFont();
        fontTitle.setBold(true);

    }

    private void setCellStyleFullBorder(CellStyle cellStyle) {
        cellStyle.setBorderTop((short) 1);
        cellStyle.setBorderBottom((short) 1);
        cellStyle.setBorderLeft((short) 1);
        cellStyle.setBorderRight((short) 1);
    }

    //Define style cell
    private void styleCell() {
        //CELL TITLE 1
        cellStyleTitleMain = wb.createCellStyle();
        cellStyleTitleMain.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleTitleMain.setFillForegroundColor(HSSFColor.YELLOW.index);
        cellStyleTitleMain.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        setCellStyleFullBorder(cellStyleTitleMain);
        cellStyleTitleMain.setFont(fontTitleMain);

        //CELL BASIC
        cellStyleValue = wb.createCellStyle();
        setCellStyleFullBorder(cellStyleValue);

        //CELL TITLE
        cellStyleTitle = wb.createCellStyle();
        setCellStyleFullBorder(cellStyleTitle);
        cellStyleTitle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleTitle.setFont(fontTitle);

        //CELL TITLE RESULT
        cellStyleTitleResult = wb.createCellStyle();
        setCellStyleFullBorder(cellStyleTitleResult);
        cellStyleTitleResult.setFillForegroundColor(HSSFColor.YELLOW.index);
        cellStyleTitleResult.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyleTitleResult.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyleTitleResult.setFont(fontTitle);

        //CELL STYLE VALUE RESULT
        cellStyleValueResult = wb.createCellStyle();
        setCellStyleFullBorder(cellStyleValueResult);
        cellStyleValueResult.setFillForegroundColor(HSSFColor.YELLOW.index);
        cellStyleValueResult.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
    }

    private void setCellDataTitle() {
        row = sheet.createRow(1);
        setCellTitle("ID ho?? ????n", 0);
        setCellTitle("ID ng?????i d??ng", 1);
        setCellTitle("ID Shipper", 2);
        setCellTitle("?????a ch???", 3);
        setCellTitle("Ghi ch??", 4);
        setCellTitle("Tr???ng th??i ????n h??ng", 5);
        setCellTitle("T???ng ho?? ????n", 6);
    }

    private void setSizeColumn() {
        sheet.setColumnWidth(0, (30 * 200));
        sheet.setColumnWidth(1, (30 * 200));
        sheet.setColumnWidth(2, (30 * 200));
        sheet.setColumnWidth(3, (70 * 200));
        sheet.setColumnWidth(4, (40 * 200));
        sheet.setColumnWidth(5, (30 * 200));
        sheet.setColumnWidth(6, (20 * 200));
    }

    private void setShowTitleMain(int rowIndex, int cellIndex, String title) {
        row = sheet.createRow(rowIndex);
        cell = row.createCell(cellIndex);
        cell.setCellValue(title);
        cell.setCellStyle(cellStyleTitleMain);
    }


    private void setRowTitleResult() {
        //Title tong ket
        setShowTitleMain(arrOrderFilter.size() + 4, 0, "T???NG K???T");
        row = sheet.createRow(arrOrderFilter.size() + 5);
        setCellTitleResult(row, "STT", 0);
        setCellTitleResult(row, "Tr???ng th??i", 1);
        setCellTitleResult(row, "T???ng s??? l?????ng", 2);
        setCellTitleResult(row, "T???ng", 3);
    }
    private void setRowValueResult(){
        caculatorReportExcelTotal();
        //load status 0
        row = sheet.createRow(arrOrderFilter.size() + 6);
        setCellValueResult(row,  "1", 0);
        setCellValueResult(row,  "Hu???", 1);
        setCellValueResult(row,  reportOrder.getAmount0()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal0()), 3);
        //load status 1
        row = sheet.createRow(arrOrderFilter.size() + 6+1);
        setCellValueResult(row,  "2", 0);
        setCellValueResult(row,  "Ch??? x??? l??", 1);
        setCellValueResult(row,  reportOrder.getAmount1()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal1()), 3);
        //load status 2
        row = sheet.createRow(arrOrderFilter.size() + 6+2);
        setCellValueResult(row,  "3", 0);
        setCellValueResult(row,  "??ang x??? l??", 1);
        setCellValueResult(row,  reportOrder.getAmount2()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal2()), 3);
        //load status 3
        row = sheet.createRow(arrOrderFilter.size() + 6+3);
        setCellValueResult(row,  "4", 0);
        setCellValueResult(row,  "Ch??? giao", 1);
        setCellValueResult(row,  reportOrder.getAmount3()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal3()), 3);
        //load status 4
        row = sheet.createRow(arrOrderFilter.size() + 6+4);
        setCellValueResult(row,  "5", 0);
        setCellValueResult(row,  "Giao h??ng cho shipper", 1);
        setCellValueResult(row,  reportOrder.getAmount4()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal4()), 3);
        //load status 5
        row = sheet.createRow(arrOrderFilter.size() + 6+5);
        setCellValueResult(row,  "6", 0);
        setCellValueResult(row,  "Shipper nh???n h??ng", 1);
        setCellValueResult(row,  reportOrder.getAmount5()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal5()), 3);
        //load status 6
        row = sheet.createRow(arrOrderFilter.size() + 6+6);
        setCellValueResult(row,  "7", 0);
        setCellValueResult(row,  "????n h??ng ???? giao", 1);
        setCellValueResult(row,  reportOrder.getAmount6()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal6()), 3);
        //load status 7
        row = sheet.createRow(arrOrderFilter.size() + 6+7);
        setCellValueResult(row,  "8", 0);
        setCellValueResult(row,  "????n h??ng ???? giao ti???n", 1);
        setCellValueResult(row,  reportOrder.getAmount7()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal7()), 3);
        //load status 8
        row = sheet.createRow(arrOrderFilter.size() + 6+8);
        setCellValueResult(row,  "9", 0);
        setCellValueResult(row,  "????n h??ng ho??n th??nh", 1);
        setCellValueResult(row,  reportOrder.getAmount8()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal8()), 3);
        //load status 9
        row = sheet.createRow(arrOrderFilter.size() + 6+9);
        setCellValueResult(row,  "10", 0);
        setCellValueResult(row,  "B??o hu???", 1);
        setCellValueResult(row,  reportOrder.getAmount9()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal9()), 3);
        //load status 10
        row = sheet.createRow(arrOrderFilter.size() + 6+10);
        setCellValueResult(row,  "11", 0);
        setCellValueResult(row,  "???? ho??n h??ng", 1);
        setCellValueResult(row,  reportOrder.getAmount10()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotal10()), 3);
        //load status total
        row = sheet.createRow(arrOrderFilter.size() + 6+11);
        setCellValueResult(row,  "12", 0);
        setCellValueResult(row,  "T???ng", 1);
        setCellValueResult(row,  reportOrder.getAmountTotal()+"", 2);
        setCellValueResult(row,  formatPrice(reportOrder.getTotalTotal()), 3);
    }

    private void caculatorReportExcelTotal() {
        int amount0 = 0, amount1 = 0, amount2 = 0, amount3 = 0, amount4 = 0, amount5 = 0,
                amount6 = 0, amount7 = 0, amount8 = 0, amount9 = 0, amount10 = 0, amountTotal = 0;
        int total0 = 0, total1 = 0, total2 = 0, total3 = 0, total4 = 0, total5 = 0, total6 = 0,
                total7 = 0, total8 = 0, total9 = 0, total10 = 0, totalTotal = 0;
        for (int i = 0; i < arrOrderFilter.size(); i++) {
            //If status 0 => Hu???
            if (arrOrderFilter.get(i).getStatus() == 0) {
                amount0++;
                total0 += arrOrderFilter.get(i).getTotal();
            }
            //If status 1 => ch??? x??? l??
            if (arrOrderFilter.get(i).getStatus() == 1) {
                amount1++;
                total1 += arrOrderFilter.get(i).getTotal();
            }
            //If status 2 => ??ang x??? l??
            if (arrOrderFilter.get(i).getStatus() == 2) {
                amount2++;
                total2 += arrOrderFilter.get(i).getTotal();
            }
            //If status 3 => ch??? giao
            if (arrOrderFilter.get(i).getStatus() == 3) {
                amount3++;
                total3 += arrOrderFilter.get(i).getTotal();
            }
            //If status 4 => Giao h??ng cho shipper
            if (arrOrderFilter.get(i).getStatus() == 4) {
                amount4++;
                total4 += arrOrderFilter.get(i).getTotal();
            }
            //If status 5 => Shipper nh???n h??ng
            if (arrOrderFilter.get(i).getStatus() == 5) {
                amount5++;
                total5 += arrOrderFilter.get(i).getTotal();
            }
            //If status 6 => ????n h??ng ???? giao
            if (arrOrderFilter.get(i).getStatus() == 6) {
                amount6++;
                total6 += arrOrderFilter.get(i).getTotal();
            }
            //If status 7 => ????n h??ng ???? giao ti???n
            if (arrOrderFilter.get(i).getStatus() == 7) {
                amount7++;
                total7 += arrOrderFilter.get(i).getTotal();
            }
            //If status 8 => ????n h??ng ho??n th??nh
            if (arrOrderFilter.get(i).getStatus() == 8) {
                amount8++;
                total8 += arrOrderFilter.get(i).getTotal();
            }
            //If status 9 => B??o hu???
            if (arrOrderFilter.get(i).getStatus() == 9) {
                amount9++;
                total9 += arrOrderFilter.get(i).getTotal();
            }
            //If status 10 => ???? ho??n th??nh
            if (arrOrderFilter.get(i).getStatus() == 10) {
                amount10++;
                total10 += arrOrderFilter.get(i).getTotal();
            }
        }
        //ResultTotal
        amountTotal = amount0 + amount1 + amount2 + amount3 + amount4 + amount5 + amount6 +
                amount7 + amount8 + amount9 + amount10;
        totalTotal = total0+total1+total2+total3+total4+total5+total6+total7+
                total8+total9+total10;

        //set amount
        reportOrder.setAmount0(amount0);
        reportOrder.setAmount1(amount1);
        reportOrder.setAmount2(amount2);
        reportOrder.setAmount3(amount3);
        reportOrder.setAmount4(amount4);
        reportOrder.setAmount5(amount5);
        reportOrder.setAmount6(amount6);
        reportOrder.setAmount7(amount7);
        reportOrder.setAmount8(amount8);
        reportOrder.setAmount9(amount9);
        reportOrder.setAmount10(amount10);
        reportOrder.setAmountTotal(amountTotal);
        //set total
        reportOrder.setTotal0(total0);
        reportOrder.setTotal1(total1);
        reportOrder.setTotal2(total2);
        reportOrder.setTotal3(total3);
        reportOrder.setTotal4(total4);
        reportOrder.setTotal5(total5);
        reportOrder.setTotal6(total6);
        reportOrder.setTotal7(total7);
        reportOrder.setTotal8(total8);
        reportOrder.setTotal9(total9);
        reportOrder.setTotal10(total10);
        reportOrder.setTotalTotal(totalTotal);
    }

    private void setCellTitleResult(Row rIndex, String title, int position) {
        cell = rIndex.createCell(position);
        cell.setCellValue(title);
        cell.setCellStyle(cellStyleTitleResult);
    }

    private void setCellValueResult(Row rIndex, String title, int position) {
        cell = rIndex.createCell(position);
        cell.setCellValue(title);
        cell.setCellStyle(cellStyleValueResult);
    }

    private void setCellTitle(String title, int position) {
        cell = row.createCell(position);
        cell.setCellValue(title);
        cell.setCellStyle(cellStyleTitle);
    }

    private void setCellValue(String title, int position) {
        cell = row.createCell(position);
        cell.setCellValue(title);
        cell.setCellStyle(cellStyleValue);
    }

    private void setRowDataValue() {
        String valueStatus=null;
        for (int i = 0; i < arrOrderFilter.size(); i++) {
            valueStatus= getStrStatusById(arrOrderFilter.get(i).getStatus()+"");
            row = sheet.createRow(i + 2);
            setCellValue(arrOrderFilter.get(i).getOrderID(), 0);
            setCellValue(arrOrderFilter.get(i).getAccountID(), 1);
            setCellValue(arrOrderFilter.get(i).getShipperID(), 2);
            setCellValue(arrOrderFilter.get(i).getAddress(), 3);
            setCellValue(arrOrderFilter.get(i).getNote(), 4);
            setCellValue( valueStatus, 5);
            setCellValue(formatPrice(arrOrderFilter.get(i).getTotal()), 6);
        }
    }
    private String getStrStatusById(String idStatus){
        String str=null;
        for(int i=0;i<arrStatus.size();i++){
            if(arrStatus.get(i).getKeyStatus().equals(idStatus)){
                str = arrStatus.get(i).getValuesStatus().toString();
            }
        }
        return str;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                intent = new Intent(OrderHistoryActivity.this, ListAccountActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_lsdh:
                break;
            case R.id.nav_tk:
                intent = new Intent(OrderHistoryActivity.this, RevenueStatisticActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_qlbl:
                intent = new Intent(OrderHistoryActivity.this, ListRatingActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_dmk:
                intent = new Intent(OrderHistoryActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(OrderHistoryActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(OrderHistoryActivity.this, "Vui l??ng ch???n ch???c n??ng kh??c", Toast.LENGTH_SHORT).show();
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
    private String formatPrice(int price) {
        String stmp = String.valueOf(price);
        int amount;
        amount = (int) (stmp.length() / 3);
        if (stmp.length() % 3 == 0)
            amount--;
        for (int i = 1; i <= amount; i++) {
            stmp = new StringBuilder(stmp).insert(stmp.length() - (i * 3) - (i - 1), ",").toString();
        }
        return stmp + " ???";
    }
}