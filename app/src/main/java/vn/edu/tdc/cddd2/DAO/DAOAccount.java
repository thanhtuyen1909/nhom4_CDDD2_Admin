package vn.edu.tdc.cddd2.DAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.tdc.cddd2.data_models.Account;

public class DAOAccount {
    private DatabaseReference ref;
    ArrayList<Account> list  = new ArrayList<Account>();
    public DAOAccount() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        ref = db.getReference("Account");
    }

    public Task<Void> update(String key,Account account){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("id",account.getId());
        map.put("username",account.getUsername());
        map.put("password",account.getPassword());
        map.put("role_id",account.getRole_id());
        map.put("status",account.getStatus());
        map.put("image",account.getImage());

        return ref.child(key).updateChildren(map);
    }

}
