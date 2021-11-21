package vn.edu.tdc.ltdd2.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.tdc.ltdd2.data_models.Account;

public class DAOAccount {
    private DatabaseReference ref;
    ArrayList<Account> list  = new ArrayList<Account>();
    public DAOAccount() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        ref = db.getReference("Account");
    }

    public Task<Void> update(String key,Account account){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("username",account.getUsername());
        map.put("password",account.getPassword());
        map.put("role_id",account.getRole_id());
        map.put("status",account.getStatus());

        return ref.child(key).updateChildren(map);
    }

}
