package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.Product;
import vn.edu.tdc.cddd2.data_models.Role;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> {
    ArrayList<Account> listAccounts;
    private Context context;
    AccountAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(AccountAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public AccountAdapter(ArrayList<Account> listAccounts, Context context) {
        this.listAccounts = listAccounts;
        this.context = context;
    }

    @NonNull
    @Override
    public AccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_account_adm,parent,false);
        AccountAdapter.ViewHolder viewHolder = new AccountAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://cddd2-f1bcd-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Role");
        Account item = listAccounts.get(position);
        holder.im_item.setImageResource(R.drawable.baseline_person_24);
        holder.tv_username.setText(item.getUsername());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot node : snapshot.getChildren()){
                    Role role = node.getValue(Role.class);
                    if(item.getRole_id() == role.getId()){
                        holder.tv_role.setText(item.getRole_id());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null) {
                    if(v.getId() == R.id.btnHistory) {
                        itemClickListener.getLayoutHistory();
                    }
                    else itemClickListener.getInfor(item);
                } else {
                    return;
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return listAccounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView im_item, im_lock, im_history;
        TextView tv_username, tv_role;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_username = itemView.findViewById(R.id.txt_username);
            tv_role = itemView.findViewById(R.id.txt_quyen);
            im_lock = itemView.findViewById(R.id.btnLock);
            im_history = itemView.findViewById(R.id.btnHistory);
            im_lock.setOnClickListener(this);
            im_history.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Account item);
        void getLayoutHistory();
    }
}
