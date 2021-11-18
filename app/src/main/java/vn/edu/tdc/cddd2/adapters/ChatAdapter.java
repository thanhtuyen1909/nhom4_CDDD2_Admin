package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.ItemChat;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> implements Filterable {
    ArrayList<ItemChat> listChat, listChatFilter, list;
    Context context;
    ChatAdapter.ItemClickListener itemClickListener;

    public void setItemClickListener(ChatAdapter.ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ChatAdapter(ArrayList<ItemChat> listChat, Context context) {
        this.listChat = listChat;
        this.context = context;
        this.list = listChat;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_message, parent, false);
        ChatAdapter.ViewHolder viewHolder = new ChatAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ItemChat item = listChat.get(position);
        Picasso.get().load(item.getImage()).fit().into(holder.im_item);
        holder.tv_name.setText(item.getName());
        holder.tv_mess.setText(item.getMessageNew());
        holder.tv_created.setText(item.getCreated_at());
        if(!item.isSeen()) {
            holder.tv_mess.setTypeface(null, Typeface.BOLD);
            holder.tv_mess.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            holder.tv_mess.setTypeface(null, Typeface.NORMAL);
        }
        holder.onClickListener = v -> {
            if (itemClickListener != null) {
                itemClickListener.detail(item.getUserID(), item.getImage(), item.getName());
            } else {
                return;
            }
        };
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    listChatFilter = list;
                } else {
                    ArrayList<ItemChat> filters = new ArrayList<>();
                    for (ItemChat row : listChat) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filters.add(row);
                        }
                    }
                    listChatFilter = filters;
                }
                filterResults.values = listChatFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listChat = (ArrayList<ItemChat>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView im_item;
        TextView tv_name, tv_mess, tv_created;
        View.OnClickListener onClickListener;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            im_item = itemView.findViewById(R.id.imgAvatar);
            tv_name = itemView.findViewById(R.id.txtTen);
            tv_mess = itemView.findViewById(R.id.txtTinMoi);
            tv_created = itemView.findViewById(R.id.txtTime);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }
    }

    public interface ItemClickListener {
        void detail(String userID, String img, String nameUser);
    }
}
