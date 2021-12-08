package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Order;

public class Order5Adapter extends RecyclerView.Adapter<Order5Adapter.ViewHolder> implements Filterable  {
    ArrayList<Order> listOrder, listOrderFilter, list;
    Context context;
    Order5Adapter.ItemClickListener itemClickListener;
    DatabaseReference cusRef = FirebaseDatabase.getInstance().getReference("Customer");

    public void setItemClickListener(Order5Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Order5Adapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
        this.list = listOrder;
    }

    @NonNull
    @Override
    public Order5Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_order_oh, parent, false);
        return new Order5Adapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Order5Adapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);

        holder.cb_dagiao.setEnabled(true);
        holder.cb_huy.setEnabled(true);
        holder.cb_huy.setChecked(false);
        holder.cb_dagiao.setChecked(false);

        cusRef.orderByChild("accountID").equalTo(item.getAccountID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if(snapshot1.child("status").equals("black")) {
                        holder.cardView.setCardBackgroundColor(Color.GRAY);
                    } else {
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.tv_maDH.setText(item.getOrderID());
        holder.tv_tong.setText("Cần thanh toán: " + formatPrice(item.getRemain()));
        holder.tv_ngaydat.setText("Ngày đặt: " + item.getCreated_at());
        holder.tv_diachi.setText("Địa chỉ: " + item.getAddress());
        int stt = item.getStatus();
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if(v == holder.im_detail) itemClickListener.getInfor(item);
                else if(v == holder.cb_dagiao) {
                    holder.cb_huy.setChecked(false);
                    if (((CheckBox) v).isChecked()) {
                        item.setStatus(2);
                    } else {
                        item.setStatus(stt);
                    }
                } else {
                    holder.cb_dagiao.setChecked(false);
                    if (((CheckBox) v).isChecked()) {
                        item.setStatus(0);
                    } else {
                        item.setStatus(stt);
                    }
                }
            } else {
                return;
            }
        };
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView im_detail;
        TextView tv_maDH, tv_tong, tv_ngaydat, tv_diachi;
        CheckBox cb_dagiao, cb_huy;
        View.OnClickListener onClickListener;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_ngaydat = itemView.findViewById(R.id.txt_ngaydat);
            tv_diachi = itemView.findViewById(R.id.txt_diachi);
            im_detail = itemView.findViewById(R.id.btnDetail);
            cb_dagiao = itemView.findViewById(R.id.checksedat);
            cardView = itemView.findViewById(R.id.card);

            cb_huy = itemView.findViewById(R.id.checkhuy);

            im_detail.setOnClickListener(this);
            cb_dagiao.setOnClickListener(this);
            cb_huy.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
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

    public interface ItemClickListener {
        void getInfor(Order item);
    }
}

