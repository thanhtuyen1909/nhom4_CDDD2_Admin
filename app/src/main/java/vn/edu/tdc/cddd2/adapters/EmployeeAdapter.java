package vn.edu.tdc.cddd2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Employee;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> implements Filterable {
    ArrayList<Employee> listEmployees;
    ArrayList<Employee> listEmployees1;
    private Context context;
    EmployeeAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(EmployeeAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public EmployeeAdapter(ArrayList<Employee> listEmployees, Context context) {
        this.listEmployees = listEmployees;
        this.context = context;
        this.listEmployees1=listEmployees;
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
    public void onBindViewHolder(@NonNull EmployeeAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Employee item = listEmployees.get(position);
        holder.im_item.setImageURI(Uri.parse(item.getImage()));
        holder.tv_name.setText(item.getName());
        holder.tv_manv.setText(item.getId());
        holder.tv_chucvu.setText(item.getPosition());
        Picasso.get().load(item.getImage()).into(holder.im_item);

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
    //seach
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search= constraint.toString();
                if(search.isEmpty()){
                    listEmployees=listEmployees1;
                }else {
                    ArrayList<Employee> list=new ArrayList<>();
                    for (Employee employee:listEmployees1){
                        if(employee.getId().toLowerCase().contains(search.toLowerCase())){
                            list.add(employee);
                        }
                    }
                    listEmployees=list;
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=listEmployees;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listEmployees =(ArrayList<Employee>) results.values;
                notifyDataSetChanged();
            }
        };
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
