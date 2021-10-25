package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.data_models.Order;
import vn.edu.tdc.cddd2.data_models.ShipArea;

public class Order1Adapter extends RecyclerView.Adapter<Order1Adapter.ViewHolder> {
    ArrayList<Order> listOrder;
    private Context context;
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
                    if(item.getShipperID().equals(ship.getEmployeeID())) {
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
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if(v == holder.cb_giaohang && ((CheckBox) v).isChecked()) {
                    item.setStatus(4);
                    holder.cb_giaohang.setEnabled(false);
                } else if (v == holder.cb_hoanthanh && ((CheckBox) v).isChecked()) {
                    item.setStatus(8);
                } else if (v == holder.rb_huygiao && ((RadioButton) v).isChecked()) {
                    item.setStatus(2);
                    item.setShipperID("null");
                } else if (v == holder.rb_huynhan && ((RadioButton) v).isChecked()) {
                    item.setStatus(0);
                } else {
                    itemClickListener.getInfor(item);
                }
            } else {
                return;
            }
        };
        if(item.getStatus() == 4) {
            holder.cb_giaohang.setChecked(true);
        }
        if(item.getStatus() == 5) {
            holder.cb_giaohang.setChecked(true);
            holder.cb_giaohang.setEnabled(false);
            holder.cb_hoanthanh.setEnabled(true);
            holder.rb_huynhan.setEnabled(true);
        }
    }

    private String formatPrice(int price) {
        return NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(price);
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
