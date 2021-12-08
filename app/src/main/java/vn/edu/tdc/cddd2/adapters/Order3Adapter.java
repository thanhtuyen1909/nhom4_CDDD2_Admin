package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import java.util.HashMap;
import java.util.Map;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Order;

public class Order3Adapter extends RecyclerView.Adapter<Order3Adapter.ViewHolder> {
    private ArrayList<Order> listOrder;
    private Context context;
    private Order3Adapter.ItemClickListener itemClickListener;

    public void setItemClickListener(Order3Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Order3Adapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
    }

    @NonNull
    @Override
    public Order3Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_willorder_oh, parent, false);
        Order3Adapter.ViewHolder viewHolder = new Order3Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Order3Adapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);
        holder.tv_maDH.setText(item.getOrderID());
        holder.tv_tong.setText("Cần thanh toán: " + formatPrice(item.getRemain()));
        holder.tv_ngaydat.setText("Ngày đặt: " + item.getCreated_at());
        holder.tv_diachi.setText("Địa chỉ: " + item.getAddress());
        int stt = item.getStatus();
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if(v == holder.im_detail) itemClickListener.getInfor(item);
                else {
                    holder.cb_hoantac.setChecked(false);
                    if (((CheckBox) v).isChecked()) {
                        item.setStatus(1);
                    } else {
                        item.setStatus(stt);
                    }
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_detail;
        TextView tv_maDH, tv_tong, tv_ngaydat, tv_diachi;
        CheckBox cb_hoantac;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_ngaydat = itemView.findViewById(R.id.txt_ngaydat);
            tv_diachi = itemView.findViewById(R.id.txt_diachi);
            im_detail = itemView.findViewById(R.id.btnDetail);
            cb_hoantac = itemView.findViewById(R.id.checkhoantac);
            im_detail.setOnClickListener(this);
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
