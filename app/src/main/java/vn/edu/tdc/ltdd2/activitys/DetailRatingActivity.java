package vn.edu.tdc.ltdd2.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.adapters.CommentAdapter;
import vn.edu.tdc.ltdd2.data_models.AccountHistory;
import vn.edu.tdc.ltdd2.data_models.Comment;
import vn.edu.tdc.ltdd2.data_models.Notification;

public class DetailRatingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Toolbar toolbar;
    TextView btnBack, subtitleAppbar, txt_rating, txt_totalrating, txtReply, title, mess;
    RatingBar ratingBar;
    ProgressBar progressBar5, progressBar4, progressBar3, progressBar2, progressBar1;
    EditText edtReply;
    Button btnAdd;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    RecyclerView recyclerView;
    private ArrayList<Comment> listComments;
    NavigationView navigationView;
    CommentAdapter commentAdapter;
    String accountID = "", productID = "", keyComment = "", accountUser = "", role = "", username = "";
    Intent intent;

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ratingRef = db.getReference("Rating");
    DatabaseReference notiRef = db.getReference("Notification");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail_rating);

        intent = getIntent();
        accountID = intent.getStringExtra("accountID");
        username = intent.getStringExtra("username");
        productID = intent.getStringExtra("productID");
        role = intent.getStringExtra("role");

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subtitleAppbar = findViewById(R.id.subtitleAppbar);
        subtitleAppbar.setText(R.string.titleDGBL);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến
        btnBack = findViewById(R.id.txtBack);
        txt_rating = findViewById(R.id.txt_rating);
        ratingBar = findViewById(R.id.ratingBar);
        txt_totalrating = findViewById(R.id.txt_totalrating);
        progressBar5 = findViewById(R.id.progressBar5);
        progressBar4 = findViewById(R.id.progressBar4);
        progressBar3 = findViewById(R.id.progressBar3);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar1 = findViewById(R.id.progressBar1);
        txtReply = findViewById(R.id.txtReply);
        edtReply = findViewById(R.id.edtReply);
        btnAdd = findViewById(R.id.btnAdd);

        // Xử lý sự kiện click button "Trở lại":
        btnBack.setOnClickListener(v -> {
            intent = new Intent(DetailRatingActivity.this, ListRatingActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("role", role);
            startActivity(intent);
            finish();
        });

        // Xử lý sự kiện click button "Gửi":
        btnAdd.setOnClickListener(v -> {
            // Lưu dtb:
            if (!String.valueOf(edtReply.getText()).isEmpty()) {
                String comment = edtReply.getText() + "";
                ratingRef.child(keyComment).child("reply").child("accountID").setValue(accountID);
                ratingRef.child(keyComment).child("reply").child("replyComment").setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Thông báo cho người dùng:
                        pushNotification(comment, accountUser);
                        pushAccountHistory("Trả lời bình luận", comment);
                    }
                });
            } else {
                showWarningDialog("Vui lòng nhập câu trả lời!");
            }
        });

        //RecycleView
        recyclerView = findViewById(R.id.listComment);
        recyclerView.setHasFixedSize(true);
        listComments = new ArrayList<>();
        data();
        commentAdapter = new CommentAdapter(listComments, this);
        commentAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(commentAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // Lưu thông báo:
    private void pushNotification(String comment, String accountID) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if (!comment.equals("")) {
            Notification noti = new Notification();
            noti.setTitle("Bạn đã nhận được phản hồi đánh giá");
            noti.setAccountID(accountID);
            noti.setCreated_at(sdf.format(new Date()));
            noti.setContent("Nội dung: " + comment);
            //get image uri
            Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.comment_icon)).getBitmap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] data = bytes.toByteArray();

            FirebaseStorage.getInstance().getReference("images/comments").putBytes(data).addOnSuccessListener(taskSnapshot -> FirebaseStorage.getInstance().getReference("images/comments").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    noti.setImage(uri.toString());
                    notiRef.push().setValue(noti);

                    // Trả về trạng thái mặc định
                    edtReply.setText("");
                    txtReply.setText("");
                    keyComment = "";
                    accountUser = "";
                }
            }));
        }
    }

    // Xử lý thông báo:
    private void showWarningDialog(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailRatingActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(DetailRatingActivity.this).inflate(
                R.layout.layout_error_dialog,
                findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        title = view.findViewById(R.id.textTitle);
        title.setText(R.string.title);
        mess = view.findViewById(R.id.textMessage);
        mess.setText(s);
        ((TextView) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((TextView) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(v -> {
            alertDialog.dismiss();
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(v -> alertDialog.dismiss());

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void data() {
        // Lấy dữ liệu vào ratingbar, các textview và các progressbar:
        ratingRef.orderByChild("productID").equalTo(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float ratingTotal = 0.0f;
                int rating = 0, total = 0, ra5 = 0, ra4 = 0, ra3 = 0, ra2 = 0, ra1 = 0;
                for (DataSnapshot node : snapshot.getChildren()) {
                    total++;
                    rating = node.child("rating").getValue(Integer.class);
                    if (rating == 5) {
                        ra5++;
                    } else if (rating == 4) {
                        ra4++;
                    } else if (rating == 3) {
                        ra3++;
                    } else if (rating == 2) {
                        ra2++;
                    } else ra1++;
                }
                ratingTotal = (float) (ra5 * 5 + ra4 * 4 + ra3 * 3 + ra2 * 2 + ra1 * 1) / total;

                // Set dữ liệu:
                if(ratingTotal > 0) txt_rating.setText(ratingTotal + "");
                txt_totalrating.setText(total + "");
                ratingBar.setRating(ratingTotal);
                progressBar1.setMax(total);
                progressBar2.setMax(total);
                progressBar3.setMax(total);
                progressBar4.setMax(total);
                progressBar5.setMax(total);
                progressBar1.setProgress(ra1);
                progressBar2.setProgress(ra2);
                progressBar3.setProgress(ra3);
                progressBar4.setProgress(ra4);
                progressBar5.setProgress(ra5);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Lấy dữ liệu vào list:
        ratingRef.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listComments.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Comment comment = node.getValue(Comment.class);
                    comment.setKey(node.getKey());
                    if (comment.getProductID().equals(productID)) {
                        listComments.add(comment);
                    }
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private CommentAdapter.ItemClickListener itemClickListener = new CommentAdapter.ItemClickListener() {
        @Override
        public void getInfor(String key, String username1, String account) {
            // Định nghĩa lại trạng thái:
            btnAdd.setEnabled(true);
            txtReply.setText(username1);
            keyComment = key;
            accountUser = account;
        }
    };

    public void pushAccountHistory(String action, String detail) {
        // Thêm vào "Lịch sử hoạt động"
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        AccountHistory accountHistory = new AccountHistory();
        accountHistory.setAccountID(accountID);
        accountHistory.setAction(action);
        accountHistory.setDetail(detail);
        accountHistory.setDate(sdf.format(new Date()));
        db.getReference("AccountHistory").push().setValue(accountHistory);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_qltk:
                intent = new Intent(DetailRatingActivity.this, ListAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_lsdh:
                intent = new Intent(DetailRatingActivity.this, OrderHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_tk:
                intent = new Intent(DetailRatingActivity.this, RevenueStatisticActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_qlbl:
                intent = new Intent(DetailRatingActivity.this, ListRatingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dmk:
                intent = new Intent(DetailRatingActivity.this, ChangePasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(DetailRatingActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(DetailRatingActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
