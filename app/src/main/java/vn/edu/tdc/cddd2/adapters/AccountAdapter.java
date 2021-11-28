package vn.edu.tdc.cddd2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.activitys.AttendanceActivity;
import vn.edu.tdc.cddd2.activitys.ListAccountActivity;
import vn.edu.tdc.cddd2.activitys.ListHistoryActivity;
import vn.edu.tdc.cddd2.data_models.Account;
import vn.edu.tdc.cddd2.data_models.Attendance;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.data_models.History;
import vn.edu.tdc.cddd2.data_models.Product;
import vn.edu.tdc.cddd2.data_models.Role;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolder> implements Filterable {
    ArrayList<Account> listAccounts;
    ArrayList<Account> listAccount1;
    private Context context;
    private DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();
    AccountAdapter.ItemClickListener itemClickListener;
    private TextView title,mess;
    private String unlock,lock;
    private Intent intent;

    public void setItemClickListener(AccountAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public AccountAdapter(ArrayList<Account> listAccounts, Context context) {
        this.listAccounts = listAccounts;
        this.context = context;
        this.listAccount1=listAccounts;
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
    public void onBindViewHolder(@NonNull AccountAdapter.ViewHolder holder,@SuppressLint("RecyclerView")  int position) {
        Account item = listAccounts.get(position);
        holder.im_item.setImageResource(R.drawable.baseline_person_24);
        if (item.getUsername().equals("")) {
            if (item.getRole_id() == 1) {
                myRef.child("Customer").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Customer customer = node.getValue(Customer.class);
                            if (customer.getAccountID().equals(item.getAccountID())) {
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


        myRef.child("Role").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot DSRole:dataSnapshot.getChildren()){
                    Role role=DSRole.getValue(Role.class);
                    if(item.getRole_id()==role.getId()){
                        holder.tv_role.setText(role.getName());
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (item.getRole_id() == 1) {
            myRef.child("Customer").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Customer customer = node.getValue(Customer.class);
                        if (customer.getAccountID().equals(item.getAccountID())) {
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
            myRef.child("Employees").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot node : snapshot.getChildren()) {
                        Employee employee = node.getValue(Employee.class);

                        if (employee.getAccountID().equals(item.getAccountID())) {

                            if (!employee.getImage().equals("")) {
                                Picasso.get().load(employee.getImage()).fit().into(holder.im_item);
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (itemClickListener != null) {
                    if (v.getId() == R.id.btnHistory) {
                        itemClickListener.getLayoutHistory(item);
                    } else if(v.getId() == R.id.btnLock){
                        if (item.getStatus().equals("lock")) {
                            itemClickListener.lockAccount(item, "unlock");
                        } else {
                            itemClickListener.lockAccount(item, "lock");
                        }

                    }else {
                        itemClickListener.getInfor(item);
                    }
                } else {
                    return;
                }
            }
        };
        if (item.getStatus().equals("lock")) {
            holder.im_lock.setBackground(context.getDrawable(R.drawable.baseline_lock_24));
        } else {
            holder.im_lock.setBackground(context.getDrawable(R.drawable.baseline_lock_open_24));
        }
        holder.im_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key=item.getAccountID();
                intent=new Intent(context, ListHistoryActivity.class);
                intent.putExtra("key",key);
                Log.d("TAG", "getLayoutHistory: "+item.getAccountID());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listAccounts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search= constraint.toString();
                if(search.isEmpty()){
                    listAccounts=listAccount1;
                }else {
                    ArrayList<Account> list=new ArrayList<>();
                    for (Account employee:listAccount1){
                        if(employee.getUsername().toLowerCase().contains(search.toLowerCase())){
                            list.add(employee);
                        }
                    }
                    listAccounts=list;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=listAccounts;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listAccounts =(ArrayList<Account>) results.values;
                notifyDataSetChanged();
            }
        };
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
        void getLayoutHistory(Account item);
        void lockAccount(Account item, String status);
    }
}
