package com.example.diagnozepro.adapter;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.diagnozepro.R;
import com.example.diagnozepro.model.MessagesModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Left Side and Right Side Values
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<MessagesModel> messages;
    private String currentUserId;
    TextToSpeech textToSpeech;

    public MessageAdapter(Context context, List<MessagesModel> messages, TextToSpeech textToSpeech) {
        this.context = context;
        this.messages = messages;
        this.textToSpeech = textToSpeech;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //Checking View type with above values and inflating the layouts
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false);
            return new SenderVH(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false);
            return new ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesModel message = messages.get(position);

        if (holder instanceof ViewHolder) {
            //Setting Data fro Receiver
            ((ViewHolder) holder).dateText.setText(message.getDateTime());
            ((ViewHolder) holder).messageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textToSpeech.isSpeaking()) {
                        // If it's already speaking, stop the speech
                        textToSpeech.stop();
                    } else {
                        // If it's not speaking, start the speech
                        textToSpeech.speak(message.getText(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
            //Checking if type is image
            if (message.getType().contains("image")) {
                if (message.getText().isEmpty()) {
                    //If type id image but text is empty
                    ((ViewHolder) holder).messageText.setVisibility(View.GONE);
                    ((ViewHolder) holder).cvImage.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getImage()).into(((ViewHolder) holder).ivImage);
                } else {
                    //if type is image and text is also not empty
                    ((ViewHolder) holder).messageText.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).cvImage.setVisibility(View.VISIBLE);
                    ((ViewHolder) holder).messageText.setText(message.getText());
                    Glide.with(context).load(message.getImage()).into(((ViewHolder) holder).ivImage);
                }
            } else {
                //If type is message not image
                ((ViewHolder) holder).messageText.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).cvImage.setVisibility(View.GONE);
                ((ViewHolder) holder).messageText.setText(message.getText());
            }
        } else {
            //Setting Data for Sender
            ((SenderVH) holder).dateText.setText(message.getDateTime());
            ((SenderVH) holder).messageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (textToSpeech.isSpeaking()) {
                        // If it's already speaking, stop the speech
                        textToSpeech.stop();
                    } else {
                        // If it's not speaking, start the speech
                        textToSpeech.speak(message.getText(), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            });
            //Checking if type is image
            if (message.getType().contains("image")) {
                if (message.getText().isEmpty()) {
                    //If type id image but text is empty
                    ((SenderVH) holder).messageText.setVisibility(View.GONE);
                    ((SenderVH) holder).cvImage.setVisibility(View.VISIBLE);
                    Glide.with(context).load(message.getImage()).into(((SenderVH) holder).ivImage);
                } else {
                    //if type is image and text is also not empty
                    ((SenderVH) holder).messageText.setVisibility(View.VISIBLE);
                    ((SenderVH) holder).cvImage.setVisibility(View.VISIBLE);
                    ((SenderVH) holder).messageText.setText(message.getText());

                    Glide.with(context).load(message.getImage()).into(((SenderVH) holder).ivImage);
                }
            } else {
                //If type is message not image
                ((SenderVH) holder).messageText.setVisibility(View.VISIBLE);
                ((SenderVH) holder).cvImage.setVisibility(View.GONE);
                ((SenderVH) holder).messageText.setText(message.getText());
            }

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getSenderId().equals(currentUserId)) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public TextView dateText;
        public CardView cvImage;
        public ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            dateText = itemView.findViewById(R.id.dateText);
            cvImage = itemView.findViewById(R.id.cvImage);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }

    public class SenderVH extends RecyclerView.ViewHolder {
        public TextView messageText;
        public TextView dateText;
        public CardView cvImage;
        public ImageView ivImage;

        public SenderVH(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            dateText = itemView.findViewById(R.id.dateText);
            cvImage = itemView.findViewById(R.id.cvImage);
            ivImage = itemView.findViewById(R.id.ivImage);
        }
    }
}
