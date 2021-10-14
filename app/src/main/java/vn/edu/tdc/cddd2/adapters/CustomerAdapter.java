package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.activitys.ChangePasswordActivity;
import vn.edu.tdc.cddd2.activitys.DetailProductActivity;
import vn.edu.tdc.cddd2.activitys.ListCataActivity;
import vn.edu.tdc.cddd2.activitys.ListDiscountCodeActivity;
import vn.edu.tdc.cddd2.activitys.ListManuActivity;
import vn.edu.tdc.cddd2.activitys.ListProductActivity;
import vn.edu.tdc.cddd2.activitys.ListPromoActivity;
import vn.edu.tdc.cddd2.activitys.LoginActivity;
import vn.edu.tdc.cddd2.data_models.Catagory;
import vn.edu.tdc.cddd2.data_models.Customer;
import vn.edu.tdc.cddd2.data_models.Product;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {
    ArrayList<Customer> listCus;
    private Context context;
    CustomerAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(CustomerAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CustomerAdapter(ArrayList<Customer> listCus, Context context) {
        this.listCus = listCus;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_customer_1, parent, false);
        CustomerAdapter.ViewHolder viewHolder = new CustomerAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.ViewHolder holder, int position) {
        Customer item = listCus.get(position);
        holder.im_item.setImageResource(R.drawable.ic_baseline_account_circle_24);
        holder.tv_name.setText(item.getName());
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
    }

    @Override
    public int getItemCount() {
        return listCus.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item;
        TextView tv_name;
        CheckBox checkBox;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            checkBox = itemView.findViewById(R.id.checkSelect);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Customer item);
    }
}
