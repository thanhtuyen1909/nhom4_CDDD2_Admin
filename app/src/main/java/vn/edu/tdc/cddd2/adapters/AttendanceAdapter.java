package vn.edu.tdc.cddd2.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Attendance;
import vn.edu.tdc.cddd2.data_models.Employee;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> {
    ArrayList<Attendance> listAttend;
    private Context context;
    AttendanceAdapter.ItemClickListener itemClickListener;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("Attendance");
    DatabaseReference empRef = db.getReference("Employees");

    public void setItemClickListener(AttendanceAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public AttendanceAdapter(ArrayList<Attendance> listAttend, Context context) {
        this.listAttend = listAttend;
        this.context = context;
    }

    @NonNull
    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_employee, parent, false);
        AttendanceAdapter.ViewHolder viewHolder = new AttendanceAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Attendance item = listAttend.get(position);

        String currentMoth = new SimpleDateFormat("MM-yyyy", Locale.getDefault()).format(new Date());
        ref.child(currentMoth).child(item.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Attendance attendance = snapshot.getValue(Attendance.class);
                    empRef.child(attendance.getEmployeeID()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if(snapshot.exists()) {
                                Employee item = snapshot1.getValue(Employee.class);
                                holder.im_item.setImageURI(Uri.parse(item.getImage()));
                                holder.tv_name.setText(item.getName());
                                holder.tv_manv.setText(snapshot1.getKey());
                                holder.tv_chucvu.setText(item.getPosition());

                                Picasso.get().load(item.getImage()).fit().into(holder.im_item);
                            }
                            else {
                                ref.child(currentMoth).child(item.getKey()).removeValue();
                                notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (item.getStatus() == -1) {
            holder.bt_vang.setBackground(context.getDrawable(R.drawable.button_confirm));
            holder.bt_vang.setClickable(false);
        } else {
            holder.bt_vang.setBackground(context.getDrawable(R.drawable.button_login));
            holder.bt_vang.setClickable(true);
        }

        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                itemClickListener.makeAbsent(item);
            } else {
                return;
            }
        };
    }

    @Override
    public int getItemCount() {
        return listAttend.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void makeAbsent(Attendance item);

    }
}
