package vn.edu.tdc.cddd2.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.AccountAdapter;

public class MainADMActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến
    private Toolbar toolbar;
    private Button btnQLTK, btnLSDH, btnQLHD, btnTK, btnQLBL, btnDX, btnDMK;
    private Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_2);
        // Khởi tạo biến
        btnQLTK = findViewById(R.id.btnQLTK);
        btnLSDH = findViewById(R.id.btnLSDH);
        btnQLHD = findViewById(R.id.btnQLHD);
        btnTK = findViewById(R.id.btnTK);
        btnQLBL = findViewById(R.id.btnQLBL);
        btnDX = findViewById(R.id.btnLogout);
        btnDMK = findViewById(R.id.btnChangePass);

        // Bắt sự kiện xử lý button
        btnQLTK.setOnClickListener(this);
        btnLSDH.setOnClickListener(this);
        btnQLHD.setOnClickListener(this);
        btnTK.setOnClickListener(this);
        btnQLBL.setOnClickListener(this);
        btnDX.setOnClickListener(this);
        btnDMK.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnQLTK:
                intent = new Intent(MainADMActivity.this, AccountAdapter.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnLSDH:
                intent = new Intent(MainADMActivity.this, ListHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnQLHD:
                intent = new Intent(MainADMActivity.this, ListInvoiceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnTK:
                intent = new Intent(MainADMActivity.this, RevenueStatisticActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnQLBL:
                intent = new Intent(MainADMActivity.this, ListRatingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnChangePass:
                intent = new Intent(MainADMActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnLogout:
                intent = new Intent(MainADMActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(MainADMActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
