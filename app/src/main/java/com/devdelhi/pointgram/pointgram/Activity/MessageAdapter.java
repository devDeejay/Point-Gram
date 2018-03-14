package com.devdelhi.pointgram.pointgram.Activity;

import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devdelhi.pointgram.pointgram.Model.messages;
import com.devdelhi.pointgram.pointgram.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<messages> mMessagesList;

    public MessageAdapter(List<messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message_item_layout, parent, false);

        return new MessageViewHolder(v);
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.messageTextLayout);
            profileImage = itemView.findViewById(R.id.messageProfileLayout);

        }
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        messages c = mMessagesList.get(position);
        holder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

}
