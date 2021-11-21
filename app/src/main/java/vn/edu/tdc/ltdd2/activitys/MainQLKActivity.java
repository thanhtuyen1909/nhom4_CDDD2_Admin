package vn.edu.tdc.ltdd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tdc.ltdd2.R;

public class MainQLKActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến
    Button btnQLH, btnQLSP, btnQLLSP, btnDPH, btnQLKM, btnQLMGG, btnDX, btnDMK;
    TextView txtUsername;
    Intent intent;
    String username = "", role = "", accountID = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_1);

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        role = intent.getStringExtra("role");

        // Khởi tạo biến
        btnQLH = findViewById(R.id.btnQLH);
        btnQLLSP = findViewById(R.id.btnQLLSP);
        btnQLSP = findViewById(R.id.btnQLSP);
        btnDPH = findViewById(R.id.btnDPH);
        btnQLKM = findViewById(R.id.btnQLKM);
        btnQLMGG = findViewById(R.id.btnQLMGG);
        btnDX = findViewById(R.id.btnLogout);
        btnDMK = findViewById(R.id.btnChangePass);
        txtUsername = findViewById(R.id.username);

        txtUsername.setText(username);

        // Bắt sự kiện xử lý button
        btnQLH.setOnClickListener(this);
        btnQLLSP.setOnClickListener(this);
        btnQLSP.setOnClickListener(this);
        btnDPH.setOnClickListener(this);
        btnQLKM.setOnClickListener(this);
        btnQLMGG.setOnClickListener(this);
        btnDX.setOnClickListener(this);
        btnDMK.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnQLSP:
                intent = new Intent(MainQLKActivity.this, ListProductActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.btnQLH:
                intent = new Intent(MainQLKActivity.this, ListManuActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.btnQLKM:
                intent = new Intent(MainQLKActivity.this, ListPromoActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;

            case R.id.btnDPH:
                intent = new Intent(MainQLKActivity.this, OrderCoordinationActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.btnQLLSP:
                intent = new Intent(MainQLKActivity.this, ListCateActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("accountID", accountID);
                intent.putExtra("role", role);
                startActivity(intent);
                finish();
                break;
            case R.id.btnChangePass:
                intent = new Intent(MainQLKActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.btnLogout:
                intent = new Intent(MainQLKActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(MainQLKActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
