package vn.edu.tdc.cddd2.DAO;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.data_models.Account;

public class DAOAccount {
    private DatabaseReference ref;
    ArrayList<Account> list  = new ArrayList<Account>();
    public DAOAccount() {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/");
        ref = db.getReference("Account");
    }

    public int Login(Account account){
        int role_id = -1;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot node : snapshot.getChildren()){
                    Account account1 = node.getValue(Account.class);
                    list.add(account1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d("size", ""+list.size());
        for (Account account1 : list){
            if(account1.getUsername() == account.getUsername() && account1.getPassword() == account.getPassword()){
                return  account1.getRole_id();
            }
        }
        return  role_id;
    }
}
