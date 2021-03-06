package vn.edu.tdc.cddd2.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.adapters.ChatAdapter;
import vn.edu.tdc.cddd2.data_models.Chat;
import vn.edu.tdc.cddd2.data_models.ItemChat;

public class ListMessageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Khai báo biến
    Toolbar toolbar;
    private Intent intent;
    String accountID = "", username = "", name = "", role = "", img = "";
    NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    ArrayList<ItemChat> list;
    SearchView searchView;
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;
    TextView txtRole, txtName;
    ImageView iv_user;

    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("Chats");
    DatabaseReference cusRef = db.getReference("Customer");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list_message);

        intent = getIntent();
        username = intent.getStringExtra("username");
        accountID = intent.getStringExtra("accountID");
        name = intent.getStringExtra("name");
        role = intent.getStringExtra("role");
        img = intent.getStringExtra("image");

        // Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // Khởi tạo biến:
        list = new ArrayList<>();
        searchView = findViewById(R.id.editSearch);

        //NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtName = navigationView.getHeaderView(0).findViewById(R.id.txt_username);
        txtRole = navigationView.getHeaderView(0).findViewById(R.id.txt_chucvu);
        iv_user = navigationView.getHeaderView(0).findViewById(R.id.iv_user);
        txtName.setText(name);
        txtRole.setText(role);
        Picasso.get().load(img).fit().into(iv_user);

        //RecycleView
        recyclerView = findViewById(R.id.listMess);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        data();
        chatAdapter = new ChatAdapter(list, this);
        chatAdapter.setItemClickListener(itemClickListener);
        recyclerView.setAdapter(chatAdapter);

        // Sự kiện searchview:
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                chatAdapter.getFilter().filter(newText);
                return true;
            }
        });

    }

    private final ChatAdapter.ItemClickListener itemClickListener = new ChatAdapter.ItemClickListener() {
        @Override
        public void detail(String userID, String img, String nameUser) {
            intent = new Intent(ListMessageActivity.this, DetailMessageActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("accountID", accountID);
            intent.putExtra("name", name);
            intent.putExtra("role", role);
            intent.putExtra("userID", userID);
            intent.putExtra("image", img);
            intent.putExtra("nameUser", nameUser);
            intent.putExtra("image", img);
            startActivity(intent);
            finish();
        }
    };

    private void data() {
        ArrayList<Chat> chats = new ArrayList<>();
        ref.orderByChild("created_at").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chats.clear();
                list.clear();
                for (DataSnapshot node : snapshot.getChildren()) {
                    Chat chat = node.getValue(Chat.class);
                    chats.add(chat);
                }
                Collections.reverse(chats);
                for (Chat c : chats) {
                    ItemChat item = new ItemChat();
                    item.setCreated_at(c.getCreated_at());
                    if (c.getSendID().equals(accountID)) {
                        item.setUserID(c.getReceiveID());
                        item.setMessageNew("Bạn: " + c.getMessage());
                        item.setSeen(true);
                    } else {
                        item.setUserID(c.getSendID());
                        item.setMessageNew(c.getMessage());
                        item.setSeen(c.getIsSeen());
                    }
                    cusRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.child("accountID").getValue(String.class).equals(item.getUserID()) && checkExists(item.getUserID())) {
                                    item.setImage(dataSnapshot.child("image").getValue(String.class));
                                    item.setName(dataSnapshot.child("name").getValue(String.class));
                                    list.add(item);
                                }
                            }
                            chatAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean checkExists(String key) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUserID().equals(key)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_dmk:
                intent = new Intent(ListMessageActivity.this, ChangePasswordActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                break;
            case R.id.nav_dx:
                intent = new Intent(ListMessageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(ListMessageActivity.this, "Vui lòng chọn chức năng khác", Toast.LENGTH_SHORT).show();
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
