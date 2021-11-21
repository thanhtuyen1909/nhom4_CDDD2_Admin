package vn.edu.tdc.ltdd2.adapters;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.data_models.Account;
import vn.edu.tdc.ltdd2.data_models.Customer;
import vn.edu.tdc.ltdd2.data_models.Role;

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
        View itemView = inflater.inflate(R.layout.item_account_adm, parent, false);
        AccountAdapter.ViewHolder viewHolder = new AccountAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder, int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Role");
        DatabaseReference cusRef = FirebaseDatabase.getInstance().getReference("Customer");
        DatabaseReference empRef = FirebaseDatabase.getInstance().getReference("Employees");
        Account item = listAccounts.get(position);
        holder.im_item.setImageResource(R.drawable.baseline_person_24);
        if (item.getUsername().equals("")) {
            if (item.getRole_id() == 1) {
                cusRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Customer customer = node.getValue(Customer.class);
                            if (customer.getAccountID().equals(item.getKey())) {
                                holder.tv_username.setText(customer.getName() + " (Facebook)");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        } else {
            holder.tv_username.setText(item.getUsername());
        }

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot node : snapshot.getChildren()) {
                    Role role = node.getValue(Role.class);
                    if (item.getRole_id() == role.getId()) {
                        holder.tv_role.setText(role.getName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (item.getRole_id() == 1) {
            cusRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Customer customer = node.getValue(Customer.class);
                        if (customer.getAccountID().equals(item.getKey())) {
                            if (!customer.getImage().equals("")) {
                                Picasso.get().load(customer.getImage()).fit().into(holder.im_item);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            holder.im_item.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }
        if (item.getStatus().equals("lock")) {
            holder.im_lock.setBackground(context.getDrawable(R.drawable.baseline_lock_black_24));
        } else {
            holder.im_lock.setBackground(context.getDrawable(R.drawable.baseline_lock_open_24));
        }
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if (v.getId() == R.id.btnHistory) {
                    itemClickListener.getLayoutHistory(item.getKey());
                } else if(v.getId() == R.id.btnLock){
                    if (item.getStatus().equals("lock")) {
                        itemClickListener.lockAccount(item, "unlock");
                    } else {
                        itemClickListener.lockAccount(item, "lock");
                    }

                }else {
                    itemClickListener.resetPass(item);
                }
            } else {
                return;
            }
        };
    }

    @Override
    public int getItemCount() {
        return listAccounts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item, im_lock, im_history,im_reset;
        TextView tv_username, tv_role;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_username = itemView.findViewById(R.id.txt_username);
            tv_role = itemView.findViewById(R.id.txt_quyen);
            im_lock = itemView.findViewById(R.id.btnLock);
            im_history = itemView.findViewById(R.id.btnHistory);
            im_reset = itemView.findViewById(R.id.btnRepass);
            im_lock.setOnClickListener(this);
            im_history.setOnClickListener(this);
            im_reset.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void lockAccount(Account item, String status);
        void resetPass(Account item);
        void getLayoutHistory(String key);
    }

}
