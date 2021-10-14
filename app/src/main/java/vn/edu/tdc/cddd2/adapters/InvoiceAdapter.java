package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Invoice;
import vn.edu.tdc.cddd2.data_models.Product;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.ViewHolder> {
    ArrayList<Invoice> listInvoices;
    private Context context;
    InvoiceAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(InvoiceAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public InvoiceAdapter(ArrayList<Invoice> listInvoices, Context context) {
        this.listInvoices = listInvoices;
        this.context = context;
    }

    @NonNull
    @Override
    public InvoiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_invoice,parent,false);
        InvoiceAdapter.ViewHolder viewHolder = new InvoiceAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceAdapter.ViewHolder holder, int position) {
        Invoice item = listInvoices.get(position);
        holder.im_item.setImageResource(R.drawable.ic_baseline_laptop_mac_24);
        holder.tv_name.setText(item.getMaHD());
        holder.tv_total.setText("Tổng:  " + String.valueOf(item.getTongTien()));
        holder.tv_date.setText("Ngày: " + item.getNgay());
        holder.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null) {
                    itemClickListener.getInfor(item);
                } else {
                    return;
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return listInvoices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView im_item, im_detail, im_print;
        TextView tv_name, tv_total, tv_date;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_madonhang);
            tv_total = itemView.findViewById(R.id.txt_total);
            tv_date = itemView.findViewById(R.id.txt_date);
            im_detail = itemView.findViewById(R.id.btnDetail);
            im_print = itemView.findViewById(R.id.btnPrint);
            im_detail.setOnClickListener(this);
            im_print.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Invoice item);
    }
}
