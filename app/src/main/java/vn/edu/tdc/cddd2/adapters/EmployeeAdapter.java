package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Employee;
import vn.edu.tdc.cddd2.data_models.Product;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> {
    ArrayList<Employee> listEmployees;
    private Context context;
    EmployeeAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(EmployeeAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public EmployeeAdapter(ArrayList<Employee> listEmployees, Context context) {
        this.listEmployees = listEmployees;
        this.context = context;
    }

    @NonNull
    @Override
    public EmployeeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_employee,parent,false);
        EmployeeAdapter.ViewHolder viewHolder = new EmployeeAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeAdapter.ViewHolder holder, int position) {
        Employee item = listEmployees.get(position);
        holder.im_item.setImageResource(R.drawable.baseline_person_24);
        holder.tv_name.setText(item.getHoTenNV());
        holder.tv_manv.setText(item.getMaNV());
        holder.tv_chucvu.setText(item.getChucVu());
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
        return listEmployees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView im_item;
        TextView tv_name, tv_manv, tv_chucvu;
        Button bt_vang;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_manv);
            tv_manv = itemView.findViewById(R.id.txt_hoten);
            tv_chucvu = itemView.findViewById(R.id.txt_chucvu);
            bt_vang = itemView.findViewById(R.id.btnVang);
            bt_vang.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Employee item);
    }
}
