package vn.edu.tdc.ltdd2.adapters;

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

import vn.edu.tdc.ltdd2.R;
import vn.edu.tdc.ltdd2.data_models.Order;
import vn.edu.tdc.ltdd2.data_models.ShipArea;

public class Order1Adapter extends RecyclerView.Adapter<Order1Adapter.ViewHolder> implements Filterable {
    ArrayList<Order> listOrder, listOrderFilter, list;
    Context context;

    Order1Adapter.ItemClickListener itemClickListener;

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
        holder.group.clearCheck();
        holder.cb_hoanthanh.setChecked(false);
        holder.cb_hoanthanh.setEnabled(false);
        holder.cb_giaohang.setChecked(false);
        holder.cb_giaohang.setEnabled(true);
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
        holder.tv_maDH.setText(item.getOrderID());
        holder.tv_tong.setText("Tổng đơn hàng: " + formatPrice(item.getTotal()));
        holder.tv_diachi.setText("Địa chỉ: " + item.getAddress());
        int stt = item.getStatus();
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if (v == holder.cb_giaohang) {
                    if (((CheckBox) v).isChecked()) {
                        item.setStatus(4);
                    } else {
                        item.setStatus(stt);
                    }
                } else if (v == holder.cb_hoanthanh) {
                    if (((CheckBox) v).isChecked()) {
                        item.setStatus(8);
                    } else {
                        item.setStatus(stt);
                    }
                } else if (v == holder.rb_huygiao) {
                    if (v.isSelected()) {
                        item.setStatus(stt);
                        holder.rb_huygiao.setSelected(false);
                        holder.rb_huygiao.setChecked(false);
                    } else {
                        item.setStatus(1);
                        holder.rb_huygiao.setSelected(true);
                        holder.rb_huygiao.setChecked(true);
                    }
                } else if (v == holder.rb_huynhan) {
                    if (v.isSelected()) {
                        item.setStatus(stt);
                        holder.rb_huynhan.setSelected(false);
                        holder.rb_huynhan.setChecked(false);
                    } else {
                        item.setStatus(0);
                        holder.rb_huynhan.setSelected(true);
                        holder.rb_huynhan.setChecked(true);
                    }
                } else if (v == holder.im_detail) {
                    itemClickListener.getInfor(item);
                }
            } else {
                return;
            }
        };

        if (item.getStatus() == 4) {
            holder.cb_giaohang.setChecked(true);
            holder.cb_giaohang.setEnabled(false);
            holder.cb_hoanthanh.setEnabled(true);
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
        TextView tv_maDH, tv_tong, tv_diachi;
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
            cb_giaohang = itemView.findViewById(R.id.checkgiao);
            cb_hoanthanh = itemView.findViewById(R.id.checkhoanthanh);
            rb_huygiao = itemView.findViewById(R.id.checkhuygiao);
            rb_huynhan = itemView.findViewById(R.id.checkhuynhan);
            im_detail = itemView.findViewById(R.id.btnDetail);
            group = itemView.findViewById(R.id.group);
            cardView = itemView.findViewById(R.id.cardView);

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
