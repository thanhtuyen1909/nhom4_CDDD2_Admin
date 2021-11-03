package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.ShipArea;

public class Order1Adapter extends RecyclerView.Adapter<Order1Adapter.ViewHolder> implements Filterable {
    ArrayList<Order> listOrder, listOrderFilter, list;
    Context context;

    Order1Adapter.ItemClickListener itemClickListener;
    DatabaseReference shiparea = FirebaseDatabase.getInstance().getReference("Ship_area");
    DatabaseReference emRef = FirebaseDatabase.getInstance().getReference("Employees");

    public void setItemClickListener(Order1Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Order1Adapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
    }

    @NonNull
    @Override
    public Order1Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_waitship_whm, parent, false);
        Order1Adapter.ViewHolder viewHolder = new Order1Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Order1Adapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);
        holder.tv_maDH.setText(item.getOrderID());
        holder.tv_tong.setText("Tổng: " + formatPrice(item.getTotal()));
        holder.tv_diachi.setText("Địa chỉ: " + item.getAddress());
        shiparea.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot node : dataSnapshot.getChildren()) {
                    ShipArea ship = node.getValue(ShipArea.class);
                    if (item.getShipperID().equals(ship.getEmployeeID())) {
                        emRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                ship.setShipperName(dataSnapshot.child(ship.getEmployeeID()).getValue(Employee.class).getName());
                                holder.tv_nguoiship.setText(ship.toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        int stt = item.getStatus();
        holder.onClickListener = v -> {
            holder.group.clearCheck();
            if (itemClickListener != null) {
                if(v == holder.cb_giaohang) {
                    if(((CheckBox) v).isChecked()) {
                        item.setStatus(4);
                    }
                    else {
                        item.setStatus(stt);
                    }
                }
                else if (v == holder.cb_hoanthanh) {
                    if(((CheckBox) v).isChecked()) {
                        item.setStatus(8);
                        holder.rb_huygiao.setEnabled(false);
                        holder.rb_huynhan.setEnabled(false);
                    }
                    else {
                        item.setStatus(stt);
                        holder.rb_huynhan.setEnabled(true);
                        holder.rb_huygiao.setEnabled(true);
                    }
                }
                else if (v == holder.rb_huygiao) {
                    if(v.isSelected()) {
                        item.setStatus(stt);
                        holder.rb_huygiao.setSelected(false);
                        holder.rb_huygiao.setChecked(false);
                        if(stt >= 5) {
                            holder.rb_huynhan.setEnabled(true);
                            holder.cb_hoanthanh.setEnabled(true);
                        }
                    }
                    else {
                        item.setStatus(2);
                        item.setShipperID("null");
                        holder.rb_huygiao.setSelected(true);
                        holder.rb_huygiao.setChecked(true);
                        if(stt >= 5) {
                            holder.rb_huynhan.setEnabled(false);
                            holder.cb_hoanthanh.setEnabled(false);
                        }
                    }
                }
                else if (v == holder.rb_huynhan) {
                    if(v.isSelected()) {
                        item.setStatus(stt);
                        holder.cb_hoanthanh.setEnabled(true);
                        holder.rb_huygiao.setEnabled(true);
                        holder.rb_huynhan.setSelected(false);
                        holder.rb_huynhan.setChecked(false);
                    } else {
                        item.setStatus(0);
                        holder.cb_hoanthanh.setEnabled(false);
                        holder.rb_huygiao.setEnabled(false);
                        holder.rb_huynhan.setSelected(true);
                        holder.rb_huynhan.setChecked(true);
                    }
                }
                else if(v == holder.im_detail) {
                    itemClickListener.getInfor(item);
                }
            }
            else {
                return;
            }
        };
        if (item.getStatus() == 4) {
            holder.cb_giaohang.setChecked(true);
            holder.cb_giaohang.setEnabled(false);
        }
        if (item.getStatus() == 5) {
            holder.cb_giaohang.setChecked(true);
            holder.cb_giaohang.setEnabled(false);
        }
        if(item.getStatus() == 7) {
            holder.cb_hoanthanh.setEnabled(true);
        }
        if(item.getStatus() == 9) {
           holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorError1));
        }
        if(item.getStatus() == 10) {
            holder.rb_huynhan.setEnabled(true);
        }
    }

    private String formatPrice(int price) {
        String stmp = String.valueOf(price);
        int amount;
        amount = (int) (stmp.length() / 3);
        if (stmp.length() % 3 == 0)
            amount--;
        for (int i = 1; i <= amount; i++) {
            stmp = new StringBuilder(stmp).insert(stmp.length() - (i * 3) - (i - 1), ",").toString();
        }
        return stmp + " ₫";
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listOrderFilter = list;
                } else {
                    ArrayList<Order> filters = new ArrayList<>();
                    for (Order row : listOrder) {
                        if (row.getOrderID().toLowerCase().contains(charString.toLowerCase())) {
                            filters.add(row);
                        }
                    }
                    listOrderFilter = filters;
                }
                filterResults.values = listOrderFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listOrder = (ArrayList<Order>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_detail;
        TextView tv_maDH, tv_tong, tv_diachi, tv_nguoiship;
        CheckBox cb_giaohang, cb_hoanthanh;
        RadioButton rb_huygiao, rb_huynhan;
        View.OnClickListener onClickListener;
        RadioGroup group;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_diachi = itemView.findViewById(R.id.txt_diachi);
            tv_nguoiship = itemView.findViewById(R.id.txt_nguoigiao);
            cb_giaohang = itemView.findViewById(R.id.checkgiao);
            cb_hoanthanh = itemView.findViewById(R.id.checkhoanthanh);
            rb_huygiao = itemView.findViewById(R.id.checkhuygiao);
            rb_huynhan = itemView.findViewById(R.id.checkhuynhan);
            im_detail = itemView.findViewById(R.id.btnDetail);
            group = itemView.findViewById(R.id.group);
            cardView = itemView.findViewById(R.id.cardView);

            cb_hoanthanh.setChecked(false);
            cb_hoanthanh.setEnabled(false);
            cb_giaohang.setChecked(false);
            cb_giaohang.setEnabled(true);
            cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));

            im_detail.setOnClickListener(this);
            cb_giaohang.setOnClickListener(this);
            cb_hoanthanh.setOnClickListener(this);
            rb_huygiao.setOnClickListener(this);
            rb_huynhan.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Order item);
    }
}
