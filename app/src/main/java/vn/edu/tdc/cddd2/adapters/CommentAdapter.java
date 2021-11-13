package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Comment;
import vn.edu.tdc.cddd2.data_models.Product;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    ArrayList<Comment> listProducts;
    private Context context;
    CommentAdapter.ItemClickListener itemClickListener;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference orderRef = db.getReference("Order");
    DatabaseReference customerRef = db.getReference("Customer");
    DatabaseReference empRef = db.getReference("Employees");

    public void setItemClickListener(CommentAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CommentAdapter(ArrayList<Comment> listProducts, Context context) {
        this.listProducts = listProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_comment, parent, false);
        CommentAdapter.ViewHolder viewHolder = new CommentAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comment item = listProducts.get(position);
        holder.layoutReply.setVisibility(View.GONE);
        holder.txt_traloi.setText("Trả lời");
        // Truy xuất và đổ dữ liệu gồm: ảnh người dùng, tên người dùng
        orderRef.child(item.getOrderID()).child("accountID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.txt_account.setText(snapshot.getValue(String.class));
                customerRef.orderByChild("accountID").equalTo(snapshot.getValue(String.class)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot node : snapshot.getChildren()) {
                            Picasso.get().load(node.child("image").getValue(String.class)).fit().into(holder.im_user);
                            holder.tv_name.setText(node.child("name").getValue(String.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.tv_comment.setText(item.getComment());
        holder.tv_time.setText(item.getCreated_at());
        holder.txt_donhang.setText(item.getOrderID());
        holder.ratingBar.setRating((float) item.getRating());

        if (item.getReply() != null) {
            // Thay đổi "Trả lời" -> "Xem"
            holder.txt_traloi.setText("Xem");

            // Truy xuất và đổ dữ liệu tên người trả lời
            empRef.orderByChild("accountID").equalTo(item.getReply().getAccountID()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot node : snapshot.getChildren()) {
                        holder.txt_replyname.setText("Phản hồi từ: " + node.child("name").getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.txt_replycomment.setText(item.getReply().getReplyComment());
        }

        // Xử lý sự kiện click holder.txt_traloi
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                if (String.valueOf(holder.txt_traloi.getText()).equals("Trả lời")) {
                    itemClickListener.getInfor(item.getKey(), holder.tv_name.getText() + "", holder.txt_account.getText() + "");
                } else {
                    if (holder.layoutReply.getVisibility() == View.VISIBLE) {
                        holder.layoutReply.setVisibility(View.GONE);
                        holder.txt_traloi.setText("Xem");
                    } else {
                        holder.layoutReply.setVisibility(View.VISIBLE);
                        holder.txt_traloi.setText("Thu gọn");
                    }
                }
            } else {
                return;
            }
        };
    }

    @Override
    public int getItemCount() {
        return listProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_user;
        RatingBar ratingBar;
        RelativeLayout layoutReply;
        TextView tv_name, tv_comment, tv_time, txt_traloi, txt_donhang, txt_replyname, txt_replycomment, txt_account;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_user = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_comment = itemView.findViewById(R.id.txt_comment);
            tv_time = itemView.findViewById(R.id.txt_time);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            txt_donhang = itemView.findViewById(R.id.txt_donhang);
            txt_traloi = itemView.findViewById(R.id.txt_traloi);
            layoutReply = itemView.findViewById(R.id.reply);
            txt_replyname = itemView.findViewById(R.id.txt_replyname);
            txt_replycomment = itemView.findViewById(R.id.txt_replycomment);
            txt_account = itemView.findViewById(R.id.txt_account);

            txt_traloi.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(String key, String username, String accountID);
    }
}
