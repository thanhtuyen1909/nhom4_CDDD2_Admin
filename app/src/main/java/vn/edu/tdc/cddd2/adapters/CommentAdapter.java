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
import vn.edu.tdc.cddd2.data_models.Comment;
import vn.edu.tdc.cddd2.data_models.Product;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    ArrayList<Comment> listProducts;
    private Context context;
    CommentAdapter.ItemClickListener itemClickListener;

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
        holder.im_item.setImageResource(R.drawable.ic_baseline_account_circle_24);
        holder.tv_name.setText(item.getName());
        holder.tv_comment.setText(item.getComment());
        holder.tv_time.setText(item.getDate());
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
        return listProducts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item, im_delete;
        TextView tv_name, tv_comment, tv_time;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.img);
            tv_name = itemView.findViewById(R.id.txt_name);
            tv_comment = itemView.findViewById(R.id.txt_comment);
            tv_time = itemView.findViewById(R.id.txt_time);
            im_item.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void getInfor(Comment item);
    }
}
