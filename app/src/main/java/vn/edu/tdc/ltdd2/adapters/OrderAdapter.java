package vn.edu.tdc.ltdd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> implements Filterable {
    ArrayList<Order> listOrder, list, listOrderFilter;
    Context context;
    OrderAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(OrderAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderAdapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
        this.list = listOrder;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_willorder_whm, parent, false);
        OrderAdapter.ViewHolder viewHolder = new OrderAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);
        holder.tv_maDH.setText(item.getOrderID());
        holder.tv_tong.setText("Tổng đơn hàng: " + formatPrice(item.getTotal()));
        holder.tv_diachi.setText("Địa chỉ: " + item.getAddress());

        int stt = item.getStatus();
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if (v == holder.checkgiao) {
                    if (((CheckBox) v).isChecked()) {
                        item.setStatus(2);
                    } else {
                        item.setStatus(stt);
                    }
                } else {
                    itemClickListener.getInfor(item);
                }
            } else {
                return;
            }
        };
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
    public int getItemCount() {
        return listOrder.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_detail;
        CheckBox checkgiao;
        TextView tv_maDH, tv_tong, tv_diachi;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_diachi = itemView.findViewById(R.id.txt_diachi);
            checkgiao = itemView.findViewById(R.id.checkgiao);
            im_detail = itemView.findViewById(R.id.btnDetail);
            im_detail.setOnClickListener(this);
            checkgiao.setOnClickListener(this);
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
