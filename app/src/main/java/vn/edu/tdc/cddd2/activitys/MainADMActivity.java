package vn.edu.tdc.cddd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tdc.cddd2.R;

public class MainADMActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến
    String accountID = "", username = "", name = "", role = "", img = "";
    TextView txtUsername;
    Button btnQLTK, btnLSDH, btnTK, btnQLBL, btnDX, btnDMK;
    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_2);

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        // Khởi tạo biến
        btnQLTK = findViewById(R.id.btnQLTK);
        btnLSDH = findViewById(R.id.btnLSDH);
        btnTK = findViewById(R.id.btnTK);
        btnQLBL = findViewById(R.id.btnQLBL);
        btnDX = findViewById(R.id.btnLogout);
        btnDMK = findViewById(R.id.btnChangePass);
        txtUsername = findViewById(R.id.username);

        if (!username.equals("") && !name.equals("") && !role.equals("")) {
            txtUsername.setText(name);
        }

        // Bắt sự kiện xử lý button
        btnQLTK.setOnClickListener(this);
        btnLSDH.setOnClickListener(this);
        btnTK.setOnClickListener(this);
        btnQLBL.setOnClickListener(this);
        btnDX.setOnClickListener(this);
        btnDMK.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnQLTK:
                intent = new Intent(MainADMActivity.this, ListAccountActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.btnLSDH:
                intent = new Intent(MainADMActivity.this, OrderHistoryActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.btnTK:
                intent = new Intent(MainADMActivity.this, RevenueStatisticActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.btnQLBL:
                intent = new Intent(MainADMActivity.this, ListRatingActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("name", name);
                intent.putExtra("role", role);
                intent.putExtra("image", img);
                startActivity(intent);
                finish();
                break;
            case R.id.btnChangePass:
                intent = new Intent(MainADMActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.btnLogout:
                intent = new Intent(MainADMActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(MainADMActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
