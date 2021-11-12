package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Order;

public class Order6Adapter extends RecyclerView.Adapter<Order6Adapter.ViewHolder> implements Filterable {
    ArrayList<Order> listOrder, listOrderFilter, list;
    Context context;
    Order6Adapter.ItemClickListener itemClickListener;

    public void setItemClickListener(Order6Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Order6Adapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
        this.list = listOrder;
    }

    @NonNull
    @Override
    public Order6Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_order_oh, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Order6Adapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);

        holder.cb_dahoantien.setEnabled(true);
        holder.cb_hoantac.setEnabled(true);
        holder.cb_hoantac.setChecked(false);
        holder.cb_dahoantien.setChecked(false);

        holder.tv_maDH.setText(item.getOrderID());
        holder.tv_tong.setText("Cần thanh toán: " + formatPrice(item.getRemain()));
        holder.tv_ngaydat.setText("Ngày đặt: " + item.getCreated_at());
        holder.tv_diachi.setText("Địa chỉ: " + item.getAddress());
        int stt = item.getStatus();
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if(v == holder.im_detail) itemClickListener.getInfor(item);
                else if(v == holder.cb_dahoantien) {
                    holder.cb_hoantac.setChecked(false);
                    if (((CheckBox) v).isChecked()) {
                        item.setStatus(7);
                    } else {
                        item.setStatus(stt);
                    }
                } else {
                    holder.cb_dahoantien.setChecked(false);
                    if (((CheckBox) v).isChecked()) {
                        item.setStatus(5);
                    } else {
                        item.setStatus(stt);
                    }
                }
            } else {
                return;
            }
        };

        if(item.getStatus() == 7) {
            holder.cb_dahoantien.setEnabled(false);
            holder.cb_dahoantien.setChecked(true);
            holder.cb_hoantac.setEnabled(false);
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_detail;
        TextView tv_maDH, tv_tong, tv_ngaydat, tv_diachi;
        CheckBox cb_dahoantien, cb_hoantac;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_ngaydat = itemView.findViewById(R.id.txt_ngaydat);
            tv_diachi = itemView.findViewById(R.id.txt_diachi);
            im_detail = itemView.findViewById(R.id.btnDetail);
            cb_dahoantien = itemView.findViewById(R.id.checksedat);
            cb_hoantac = itemView.findViewById(R.id.checkhuy);

            cb_dahoantien.setText("Đã giao tiền");
            cb_hoantac.setText("Hoàn tác");

            cb_dahoantien.setEnabled(true);
            cb_dahoantien.setChecked(false);

            im_detail.setOnClickListener(this);
            cb_dahoantien.setOnClickListener(this);
            cb_hoantac.setOnClickListener(this);
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
