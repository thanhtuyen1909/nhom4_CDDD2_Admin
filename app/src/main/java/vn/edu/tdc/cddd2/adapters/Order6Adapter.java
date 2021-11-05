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

public class Order6Adapter extends RecyclerView.Adapter<Order6Adapter.ViewHolder>{
    private ArrayList<Order> listOrder;
    private Context context;
    private Order6Adapter.ItemClickListener itemClickListener;
    private DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();

    public void setItemClickListener(Order6Adapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public Order6Adapter(ArrayList<Order> listOrder, Context context) {
        this.listOrder = listOrder;
        this.context = context;
    }

    @NonNull
    @Override
    public Order6Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_willorder_oh, parent, false);
        Order6Adapter.ViewHolder viewHolder = new Order6Adapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Order6Adapter.ViewHolder holder, int position) {
        Order item = listOrder.get(position);
        holder.tv_maDH.setText(item.getName());
        holder.tv_tong.setText("Tổng: " + item.getTotal());
        holder.tv_ngaydat.setText("Địa chỉ: " + item.getShipperID());
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.getInfor(item);
                } else {
                    return;
                }
            }
        };
        holder.cb_hoantac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               item.setStatus(1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView im_detail;
        TextView tv_maDH, tv_tong, tv_ngaydat;
        CheckBox cb_hoantac;
        View.OnClickListener onClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_maDH = itemView.findViewById(R.id.txt_madonhang);
            tv_tong = itemView.findViewById(R.id.txt_tongtien);
            tv_ngaydat = itemView.findViewById(R.id.txt_ngaydat);
            im_detail = itemView.findViewById(R.id.btnDetail);
            cb_hoantac = itemView.findViewById(R.id.checkhoantac);
        }
    }
    public interface ItemClickListener {
        void getInfor(Order item);
    }

}
