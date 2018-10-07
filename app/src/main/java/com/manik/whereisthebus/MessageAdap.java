package com.manik.whereisthebus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdap extends RecyclerView.Adapter<MessageAdap.MessageHolder>{

    private Context context;
    private List<UMessage> messageList;

    public MessageAdap(Context context, List<UMessage> messageList){
        this.context = context;
        this.messageList = messageList;

    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_msg,viewGroup, false);
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder messageHolder, int i) {
        UMessage uMessage = messageList.get(i);
        messageHolder.tv_msg.setText(uMessage.getMessage());

    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class MessageHolder extends RecyclerView.ViewHolder{

        TextView tv_msg;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            tv_msg = itemView.findViewById(R.id.whatmessage);

        }

}


}
