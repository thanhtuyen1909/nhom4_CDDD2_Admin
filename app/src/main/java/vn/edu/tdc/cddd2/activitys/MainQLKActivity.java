package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.ProductAdapter;
import vn.edu.tdc.cddd2.data_models.Product;

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
                Toast.makeText(MainQLKActivity.this, "Quản lý khuyến mãi", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnQLMGG:
                Toast.makeText(MainQLKActivity.this, "Quản lý mã giảm giá", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDPH:
                Toast.makeText(MainQLKActivity.this, "Điều phối hàng", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnQLLSP:
                intent = new Intent(MainQLKActivity.this, ListCataActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.btnChangePass:
                Toast.makeText(MainQLKActivity.this, "Đổi mật khẩu", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLogout:
                Toast.makeText(MainQLKActivity.this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(MainQLKActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
