package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import vn.edu.tdc.cddd2.R;

public class MainQLKActivity extends AppCompatActivity implements View.OnClickListener {
    // Khai báo biến
    private Toolbar toolbar;
    private Button btnQLH, btnQLSP, btnQLLSP, btnDPH, btnQLKM, btnQLMGG, btnDX, btnDMK;
    private Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_1);
        // Khởi tạo biến
        btnQLH = findViewById(R.id.btnQLH);
        btnQLLSP = findViewById(R.id.btnQLLSP);
        btnQLSP = findViewById(R.id.btnQLSP);
        btnDPH = findViewById(R.id.btnDPH);
        btnQLKM = findViewById(R.id.btnQLKM);
        btnQLMGG = findViewById(R.id.btnQLMGG);
        btnDX = findViewById(R.id.btnLogout);
        btnDMK = findViewById(R.id.btnChangePass);

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnQLSP:
                intent = new Intent(MainQLKActivity.this, ListProductActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnQLH:
                intent = new Intent(MainQLKActivity.this, ListManuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnQLKM:
                intent = new Intent(MainQLKActivity.this, ListPromoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnQLMGG:
                intent = new Intent(MainQLKActivity.this, ListDiscountCodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnDPH:
                intent = new Intent(MainQLKActivity.this, OrderCoordinationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnQLLSP:
                intent = new Intent(MainQLKActivity.this, ListCateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnChangePass:
                intent = new Intent(MainQLKActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnLogout:
                intent = new Intent(MainQLKActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(MainQLKActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
