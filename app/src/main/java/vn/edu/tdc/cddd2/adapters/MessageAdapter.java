package vn.edu.tdc.cddd2.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import vn.edu.tdc.cddd2.R;
import vn.edu.tdc.cddd2.data_models.Chat;

public class MessageAdapter extends RecyclerView.Adapter {
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    Context context;
    ArrayList<Chat> list;
    String accountID = "", imgUser = "";

    public MessageAdapter(Context context, ArrayList<Chat> list, String accountID, String image) {
        this.context = context;
        this.list = list;
        this.accountID = accountID;
        this.imgUser = image;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == MSG_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_out_message, parent, false);
            return new ReceivedMessageHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_in_message, parent, false);
            return new SentMessageHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat message = list.get(position);

        switch (holder.getItemViewType()) {
            case MSG_RIGHT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case MSG_LEFT:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (accountID.equals(list.get(position).getSendID())) {
            return MSG_RIGHT;
        }
        return MSG_LEFT;
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        ImageView layoutImage;
        TextView messageText, timeText, text_gchat_date_me;

        SentMessageHolder(View itemView) {
            super(itemView);
            layoutImage = itemView.findViewById(R.id.messageTv);
            messageText = itemView.findViewById(R.id.show_message);
            timeText = itemView.findViewById(R.id.text_gchat_timestamp_me);
            text_gchat_date_me = itemView.findViewById(R.id.text_gchat_date_me);
        }

        void bind(Chat message) {
            switch (message.getType()) {
                case "text" :
                    messageText.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    messageText.setText(message.getMessage());

                    break;
                case "image" :
                    messageText.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(message.getMessage()).fit().into(layoutImage);
                    break;
            }

            // Format the stored timestamp into a readable String using method.
            text_gchat_date_me.setText(message.getCreated_at().substring(0, 10));
            timeText.setText(message.getCreated_at().substring(11));
        }
    }

    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        ImageView img, layoutImage;
        TextView text_gchat_message, text_gchat_date_other, text_gchat_timestamp_other;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            text_gchat_message = itemView.findViewById(R.id.show_message);
            text_gchat_date_other = itemView.findViewById(R.id.text_gchat_date_other);
            text_gchat_timestamp_other = itemView.findViewById(R.id.text_gchat_timestamp_other);
            layoutImage = itemView.findViewById(R.id.messageTv);
            img = itemView.findViewById(R.id.img);
        }

        void bind(Chat message) {

            switch (message.getType()) {
                case "text" :
                    text_gchat_message.setVisibility(View.VISIBLE);
                    layoutImage.setVisibility(View.GONE);
                    text_gchat_message.setText(message.getMessage());

                    break;
                case "image" :
                    text_gchat_message.setVisibility(View.GONE);
                    layoutImage.setVisibility(View.VISIBLE);
                    Picasso.get().load(message.getMessage()).fit().into(layoutImage);
                    break;
            }

            Picasso.get().load(imgUser).fit().into(img);

            // Format the stored timestamp into a readable String using method.
            text_gchat_date_other.setText(message.getCreated_at().substring(0, 10));
            text_gchat_timestamp_other.setText(message.getCreated_at().substring(11));
        }
    }
}
