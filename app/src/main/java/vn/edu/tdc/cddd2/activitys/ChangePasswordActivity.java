package vn.edu.tdc.cddd2.activitys;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import vn.edu.tdc.cddd2.DAO.DAOAccount;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Account;

public class ChangePasswordActivity extends AppCompatActivity {
    // Khai báo biến:
    String username = "";
    Button btnChange;
    Toolbar toolbar;
    Handler handler = new Handler();
    boolean check = true;
    TextView btnBack, subtitleAppbar, title, mess;
    private EditText edtOldPassword,edtNewPassword,edtConfirmPassword;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Account");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_changepassword);
        username = getIntent().getStringExtra("username");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setVisibility(View.INVISIBLE);

        // Khởi tạo biến:
        btnChange = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.txtBack);
        edtOldPassword = findViewById(R.id.edtPass);
        edtNewPassword = findViewById(R.id.edtPassNew);
        edtConfirmPassword = findViewById(R.id.edtPassConfirm);

        // Sự kiện xử lý button "Đổi mật khẩu"
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkError() == 1) {
                    DAOAccount dao = new DAOAccount();
                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot node : snapshot.getChildren()){
                                Account account = node.getValue(Account.class);
                                if(account.getUsername().equals(username) && account.getPassword().equals(String.valueOf(edtOldPassword.getText()))){
                                    check = false;
                                    if(String.valueOf(edtNewPassword.getText()).compareTo(String.valueOf(edtConfirmPassword.getText())) == 0){
                                        account.setPassword(String.valueOf(edtNewPassword.getText()));
                                        dao.update(node.getKey(),account).addOnSuccessListener(suc -> {
                                            showSuccesDialog("Đổi mật khẩu thành công!");
                                            finish();
                                        }).addOnFailureListener(err ->{
                                            showWarningDialog("Đổi mật khẩu không thành công!");
                                        });
                                        break;
                                    }
                                }
                            }
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(check) {
                                        showWarningDialog("Mật khẩu hiện tại không đúng!");
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

        // Sự kiện xử lý button "Trở lại"
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private int checkError(){
        if(String.valueOf(edtOldPassword.getText()).equals("")){
            showWarningDialog("Mật khẩu không được để trống!");
            return -1;
        }
        if(String.valueOf(edtNewPassword.getText()).equals("")){
            showWarningDialog("Mật khẩu mới không được để trống!");
            return -1;
        }
        if(String.valueOf(edtConfirmPassword.getText()).equals("") || !String.valueOf(edtNewPassword.getText()).equals(String.valueOf(edtConfirmPassword.getText()))){
            showWarningDialog("Mật khẩu mới và xác nhận mật khẩu là không trùng khớp!");
            return -1;
        }
        return  1;
    }

    private void showSuccesDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ChangePasswordActivity.this).inflate(
                R.layout.layout_succes_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(notify);
        ((TextView) view.findViewById(R.id.buttonAction)).setText(getResources().getString(R.string.okay));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonAction).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showWarningDialog(String notify) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(ChangePasswordActivity.this).inflate(
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
