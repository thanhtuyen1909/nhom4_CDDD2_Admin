package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.activitys.DetailOrderActivity;
import vn.edu.tdc.cddd2.data_models.Order;


public class HistoryOrderAdapter extends RecyclerView.Adapter<HistoryOrderAdapter.HistoryOrderViewHolder>{
    Context mContext;
    ArrayList<Order> listOrder;

    public HistoryOrderAdapter(Context mContext, ArrayList<Order> listOrder) {
        this.mContext = mContext;
        this.listOrder = listOrder;
    }

    @NonNull
    @Override
    public HistoryOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_history_cart,parent,false);
       return new HistoryOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryOrderViewHolder holder, int position) {
       Order order = listOrder.get(position);
       holder.tvCardHistoryOrderTotal.setText("Tổng: "+formatPrice(order.getTotal())+"");
       holder.tvCardHistoryOrderId.setText(order.getOrderID());
       holder.tvCardHistoryOrderDate.setText(order.getCreated_at());
       holder.imgCardHistoryOrder.setImageResource(R.drawable.order);

       holder.CardHistoryOrder.setOnClickListener(view -> {
           Intent intent = new Intent(mContext, DetailOrderActivity.class);
           intent.putExtra("item", (Parcelable) order);
           mContext.startActivity(intent);
       });
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

    protected class HistoryOrderViewHolder extends RecyclerView.ViewHolder{
        private CardView CardHistoryOrder;
        private CircleImageView imgCardHistoryOrder;
        private TextView tvCardHistoryOrderId,tvCardHistoryOrderTotal,tvCardHistoryOrderDate;

        public HistoryOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            CardHistoryOrder=itemView.findViewById(R.id.CardHistoryOrder);
            imgCardHistoryOrder=itemView.findViewById(R.id.imgCardHistoryOrder);
            tvCardHistoryOrderDate=itemView.findViewById(R.id.tvCardHistoryOrderDate);
            tvCardHistoryOrderId=itemView.findViewById(R.id.tvCardHistoryOrderId);
            tvCardHistoryOrderTotal=itemView.findViewById(R.id.tvCardHistoryOrderTotal);
        }
    }
}
