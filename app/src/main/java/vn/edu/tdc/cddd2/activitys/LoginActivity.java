package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tdc.cddd2.R;

public class LoginActivity extends AppCompatActivity {
    // Khai báo biến:
    private Button btnLogin;
    private Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        // Khởi tạo biến:
        btnLogin = findViewById(R.id.btnLogin);

        // Xử lý sự kiện click button "Login"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, MainQLKActivity.class);
                startActivity(intent);
            }
        });
    }
}
