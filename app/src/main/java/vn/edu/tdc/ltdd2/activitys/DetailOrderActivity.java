package vn.edu.tdc.ltdd2.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.adapters.Product5Adapter;
import vn.edu.tdc.ltdd2.data_models.Order;
import vn.edu.tdc.ltdd2.data_models.OrderDetail;
import vn.edu.tdc.ltdd2.data_models.Product;

public class DetailOrderActivity extends AppCompatActivity {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, txtMaDH, txtCreatedAt, txtStatus, txtNote, txtName, txtPhone, txtTotal, txtAddress;
    RecyclerView recyclerView;
    ArrayList<Product> listProducts;
    Product5Adapter product5Adapter;
    Intent intent;
    Order item = null;

    //Map value pdf
    Order order;
    ArrayList<OrderDetail> arrOrderDetail;
    PdfDocument myPdfDocument;
    int startXPosition = 10;
    PdfDocument.PageInfo myPageInfo1;
    int endXPosition;
    int startYPosition = 80;
    Paint myPaint;
    Canvas canvas;
    PdfDocument.Page myPage1;

    DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference("Status");
    DatabaseReference order_detailRef = FirebaseDatabase.getInstance().getReference("Order_Details");
    DatabaseReference proRef = FirebaseDatabase.getInstance().getReference("Products");
    Query queryOrderDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_order);

        intent = getIntent();
        item = (Order) getIntent().getParcelableExtra("item");

        order = new Order();
        arrOrderDetail = new ArrayList<>();
        myPdfDocument = new PdfDocument();
        myPaint = new Paint();

        myPageInfo1 = new PdfDocument.
                PageInfo.Builder(250, 500, 1).create();
        myPage1 = myPdfDocument.startPage(myPageInfo1);
        canvas = myPage1.getCanvas();
        endXPosition = myPageInfo1.getPageWidth() - 10;

        queryOrderDetail = FirebaseDatabase.getInstance().getReference()
                .child("Order_Details").orderByChild("orderID").equalTo(item.getOrderID());
        loadDataOrderDetail();

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleDetaiCTDH);

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        txtMaDH = findViewById(R.id.txt_madonhang);
        txtCreatedAt = findViewById(R.id.txt_ngaytao);
        txtStatus = findViewById(R.id.txt_trangthai);
        txtNote = findViewById(R.id.txt_ghichu);
        txtName = findViewById(R.id.txt_htkh);
        txtPhone = findViewById(R.id.txt_sdt);
        txtTotal = findViewById(R.id.txt_tongtien);
        txtAddress = findViewById(R.id.txt_diachi);

        // Đổ dữ liệu
        if (item != null) {
            txtPhone.setText(item.getPhone());
            txtMaDH.setText(item.getOrderID());
            txtCreatedAt.setText("Ngày tạo: " + item.getCreated_at());
            statusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (Integer.parseInt(snapshot.getKey()) == item.getStatus()) {
                            txtStatus.setText("Trạng thái: " + snapshot.getValue(String.class));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            txtNote.setText("Ghi chú: " + item.getNote());
            txtName.setText("Họ tên khách hàng: " + item.getName());
            txtTotal.setText("Tổng tiền: " + formatPrice(item.getTotal()));
            txtAddress.setText("Địa chỉ: " + item.getAddress());
        }

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Xử lý sự kiện click phone:
        txtPhone.setOnClickListener(v -> {
            intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + txtPhone.getText()));
            if (ActivityCompat.checkSelfPermission(DetailOrderActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        });

        //RecycleView
        recyclerView = findViewById(R.id.listProduct);
        recyclerView.setHasFixedSize(true);
        listProducts = new ArrayList<>();
        data();
        product5Adapter = new Product5Adapter(listProducts, this);
        recyclerView.setAdapter(product5Adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadDataOrderDetail() {
        arrOrderDetail.clear();
        queryOrderDetail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                        //add data
                        String orderID = snapshot1.getKey();
                        String productID = snapshot1.getValue(OrderDetail.class).getProductID();
                        int amount = snapshot1.getValue(OrderDetail.class).getAmount();
                        int price = snapshot1.getValue(OrderDetail.class).getPrice();
                        OrderDetail orderDetail = new OrderDetail(orderID, amount, productID, price);
                        arrOrderDetail.add(orderDetail);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void clickPrintfPdf(View view) {
        myPaint.setTypeface(Typeface.create("Times New Roman", Typeface.NORMAL));
        myPaint.setTextAlign(Paint.Align.CENTER);
        myPaint.setTextSize(12.0f);
        canvas.drawText("Cửa hàng zuke", myPageInfo1.getPageWidth() / 2, 30, myPaint);

        myPaint.setTextSize(6.0f);
        myPaint.setColor(Color.rgb(122, 119, 119));
        canvas.drawText("54 Lê văn chí, Linh Trung, Thủ Đức, TP.HCM", myPageInfo1.getPageWidth() / 2, 40, myPaint);
        myPaint.setTextScaleX(1.2f);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(8.0f);
        myPaint.setColor(Color.rgb(122, 119, 119));
        canvas.drawText("Thông tin khách hàng", 10, 60, myPaint);

        myPaint.setTextAlign(Paint.Align.LEFT);
        myPaint.setTextSize(7.0f);
        myPaint.setColor(Color.rgb(51, 43, 43));

        pdfDataCustomerItem("Mã đơn hàng: ", item.getOrderID());
        pdfDataCustomerItem("Họ và tên: ", item.getName());
        pdfDataCustomerItem("Số điện thoại: ", item.getPhone());
        String address = item.getAddress();
        if (item.getAddress().length() + 9 <= 54) {
            pdfDataCustomerItem("Địa chỉ: ", address);
        } else {
            String itemAddress1 = null, itemAddress2 = null;
            int positionSpace = 0;
            //Tra ve vi tri bat dau tu so 52
            positionSpace = address.indexOf(' ', 49);
            itemAddress1 = address.substring(0, positionSpace);
            itemAddress2 = address.substring(positionSpace, address.length());
            pdfDataCustomerItem("Địa chỉ: ", itemAddress1);
            pdfDataCustomerItem("         ", itemAddress2);
        }

        //Note
        if (item.getNote().length() + 4 <= 54) {
            pdfDataCustomerItem("Ghi chú: ", item.getNote());
        } else {
            String itemNote1 = null, itemNote2 = null;
            int positionSpace = 0;
            //Tra ve vi tri bat dau tu so 52
            positionSpace = item.getNote().indexOf(' ', 49);
            itemNote1 = item.getNote().substring(0, positionSpace);
            itemNote2 = item.getNote().substring(positionSpace, item.getNote().length());
            pdfDataCustomerItem("Ghi chú: ", itemNote1);
            pdfDataCustomerItem("         ", itemNote2);
        }


        //Border end start
        startYPosition += 10;
        canvas.drawLine(startXPosition, startYPosition, endXPosition, startYPosition, myPaint);
        startYPosition += 20;
        //Tổng số lượng sản phẩm
        //Xuat hien
        canvas.drawText("Tổng loại sản phẩm: " + arrOrderDetail.size(), startXPosition, startYPosition, myPaint);
        for (int i = 0; i < arrOrderDetail.size(); i++) {
            String nameProduct = getNameByIdProduct(arrOrderDetail.get(i).getProductID());
            startYPosition += 20;
            String printfData = "   " + (i + 1) + ". " + nameProduct + ", SL: " + arrOrderDetail.get(i).getAmount()
                    + ", " + formatPrice(arrOrderDetail.get(i).getPrice());
            if (printfData.length() <= 52) {
                canvas.drawText(printfData, startXPosition, startYPosition, myPaint);
            } else {
                int positionSpace = 0;
                positionSpace = printfData.indexOf(' ', 45);
                try {
                    String itemData1 = null, itemData2 = null;
                    //Tra ve vi tri bat dau tu so 52

                    itemData1 = printfData.substring(0, positionSpace);
                    itemData2 = printfData.substring(positionSpace, printfData.length());
                    canvas.drawText(itemData1, startXPosition, startYPosition, myPaint);
                    startYPosition += 20;
                    canvas.drawText("   " + itemData2, startXPosition, startYPosition, myPaint);
                    showDialog(positionSpace);
                } catch (Exception e) {
                    showDialog(printfData.length() + "/" + positionSpace);
                }
            }
        }
        //Border end
        startYPosition += 15;
        canvas.drawLine(startXPosition, startYPosition, endXPosition, startYPosition, myPaint);
        startYPosition += 20;
        //Chữ ký
        canvas.drawText("Chữ ký người nhận", myPageInfo1.getPageWidth() / 2 + 10, startYPosition, myPaint);

        myPdfDocument.finishPage(myPage1);


        String filePath = "DonHang_" + item.getOrderID() + ".pdf";

        File file = new File(getExternalFilesDir(null), filePath);
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            myPdfDocument.writeTo(outputStream);
            showDialog("In hóa đơn thành công");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showDialog("In hóa đơn thành công");
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        myPdfDocument.close();
    }

    private String getNameByIdProduct(String productID) {
        String nameProduct = null;
        for (int i = 0; i < listProducts.size(); i++) {
            if (listProducts.get(i).getKey().equals(productID)) {
                nameProduct = listProducts.get(i).getName();
            }
        }
        return nameProduct;
    }


    private void pdfDataCustomerItem(String name, String value) {
        canvas.drawText(name + value + "", startXPosition, startYPosition, myPaint);
        startYPosition += 20;
    }

    private void showDialog(String title) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(title);
        b.setNegativeButton("Xác nhận", (dialog, id) -> dialog.cancel());
        AlertDialog al = b.create();
        al.show();
    }

    private void data() {
        order_detailRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    OrderDetail orderDetail = snapshot.getValue(OrderDetail.class);
                    if (orderDetail.getOrderID().equals(item.getOrderID())) {
                        proRef.addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                listProducts.clear();
                                for (DataSnapshot snapshot1 : dataSnapshot.getChildren()) {
                                    Product product = snapshot1.getValue(Product.class);
                                    product.setPrice(orderDetail.getPrice());
                                    product.setKey(snapshot1.getKey());
                                    product.setQuantity(orderDetail.getAmount());
                                    if (product.getKey().equals(orderDetail.getProductID())) {
                                        listProducts.add(product);
                                    }
                                }
                                product5Adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        return stmp + " ₫";
    }
}
