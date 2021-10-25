package vn.edu.tdc.cddd2.activitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.DAO.DAOAccount;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.Employee;

public class LoginActivity extends AppCompatActivity {
    // Khai báo biến:
    private EditText edtUsername, edtPassword;
    Button btnLogin;
    private Intent intent;
    String name = "", role = "";
    TextView title, mess;
    boolean check = true;
    Handler handler = new Handler();
    DatabaseReference emRef = FirebaseDatabase.getInstance().getReference("Employees");
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Account");

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
                if (checkError() == 1) {
                    Account account = new Account();
                    account.setUsername(String.valueOf(edtUsername.getText()));
                    account.setPassword(String.valueOf(edtPassword.getText()));
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot node : snapshot.getChildren()) {
                                Account temp = node.getValue(Account.class);
                                temp.setId(node.getKey());
                                if (account.getUsername().equals(temp.getUsername()) && account.getPassword().equals(temp.getPassword()) &&
                                        temp.getStatus().equals("unlock")) {
                                    emRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot node1 : snapshot.getChildren()) {
                                                Employee employee = node1.getValue(Employee.class);
                                                if (employee.getAccountID().equals(temp.getId())) {
                                                    name = employee.getName();
                                                    role = employee.getPosition();
                                                    check = false;
                                                    switch (temp.getRole_id()) {
                                                        case 0:
                                                            intent = new Intent(LoginActivity.this, MainADMActivity.class);
                                                            intent.putExtra("username", temp.getUsername());
                                                            intent.putExtra("name", name);
                                                            intent.putExtra("role", role);
                                                            startActivity(intent);
                                                            break;
                                                        case 1:
                                                            break;
                                                        case 2:
                                                            intent = new Intent(LoginActivity.this, MainQLKActivity.class);
                                                            intent.putExtra("username", temp.getUsername());
                                                            intent.putExtra("name", name);
                                                            intent.putExtra("role", role);
                                                            startActivity(intent);
                                                            break;
                                                        case 3:
                                                            intent = new Intent(LoginActivity.this, ListProductSMActivity.class);
                                                            intent.putExtra("username", temp.getUsername());
                                                            intent.putExtra("name", name);
                                                            intent.putExtra("role", role);
                                                            startActivity(intent);
                                                            break;
                                                        case 4:
                                                            intent = new Intent(LoginActivity.this, OrderProcessActivity.class);
                                                            intent.putExtra("username", temp.getUsername());
                                                            intent.putExtra("name", name);
                                                            intent.putExtra("role", role);
                                                            startActivity(intent);
                                                            break;
                                                        case 5:
                                                            intent = new Intent(LoginActivity.this, AttendanceActivity.class);
                                                            intent.putExtra("username", temp.getUsername());
                                                            intent.putExtra("name", name);
                                                            intent.putExtra("role", role);
                                                            startActivity(intent);
                                                            break;
                                                        case 6:
                                                            intent = new Intent(LoginActivity.this, ShipProcessActivity.class);
                                                            intent.putExtra("username", temp.getUsername());
                                                            intent.putExtra("name", name);
                                                            intent.putExtra("role", role);
                                                            startActivity(intent);
                                                            break;
                                                        default:
                                                            ;
                                                    }
                                                    break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                            }
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (check) {
                                        showWarningDialog("Tên đăng nhập/Mật khẩu Không chính xác!");
                                    }
                                }
                            }, 100);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private int checkError() {
        if (String.valueOf(edtUsername.getText()).equals("") && String.valueOf(edtPassword.getText()).equals("")) {
            showWarningDialog("Tên đăng nhập/Mật khẩu không được để trống!");
            return -1;
        }
        if (String.valueOf(edtPassword.getText()).equals("")) {
            showWarningDialog("Mật khẩu không được để trống!");
            return -1;
        }
        if (String.valueOf(edtUsername.getText()).equals("")) {
            showWarningDialog("Tên đăng nhập không được để trống!");
            return -1;
        }
        return 1;
    }

    private void showWarningDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(LoginActivity.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.yes));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> {
            alertDialog.dismiss();
        });


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
