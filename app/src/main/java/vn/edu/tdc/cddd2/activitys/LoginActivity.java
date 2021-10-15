package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.DAO.DAOAccount;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Account;

public class LoginActivity extends AppCompatActivity {
    // Khai báo biến:
    private EditText edtUsername,edtPassword;
    private Button btnLogin;
    private Intent intent;
    private DAOAccount dao;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        // Khởi tạo biến:
        edtUsername = findViewById(R.id.edtUser);
        edtPassword = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);

        // Xử lý sự kiện click button "Login"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Account");
                Account account = new Account();
                account.setUsername(String.valueOf(edtUsername.getText()));
                account.setPassword(String.valueOf(edtPassword.getText()));
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot node : snapshot.getChildren()){
                            Account temp = node.getValue(Account.class);
                            if(account.getUsername().compareTo(temp.getUsername()) == 0 && account.getPassword().compareTo(temp.getPassword()) == 0 &&
                                temp.getStatus().compareTo("unlock") == 0){

                                switch (temp.getRole_id()){
                                    case 0 :
                                      intent = new Intent(LoginActivity.this, MainADMActivity.class);
                                      startActivity(intent);
                                        break;
                                    case 1:
                                        break;
                                    case 2:
                                        intent = new Intent(LoginActivity.this, MainQLKActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 3:
                                        intent = new Intent(LoginActivity.this,ListProductSMActivity.class );
                                        startActivity(intent);
                                        break;
                                    case 4:
                                        intent = new Intent(LoginActivity.this, OrderProcessActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 5:
                                        intent = new Intent(LoginActivity.this, AttendanceActivity.class);
                                        startActivity(intent);
                                        break;
                                    case 6:
                                        intent = new Intent(LoginActivity.this, ShipProcessActivity.class);
                                        startActivity(intent);
                                        break;
                                    default:;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }
}
