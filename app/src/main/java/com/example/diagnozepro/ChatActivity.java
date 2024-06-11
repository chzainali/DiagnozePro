package com.example.diagnozepro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.diagnozepro.adapter.MessageAdapter;
import com.example.diagnozepro.databinding.ActivityChatBinding;
import com.example.diagnozepro.model.MessagesModel;
import com.example.diagnozepro.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    private MessageAdapter adapter;
    private List<MessagesModel> chatList;
    String strImageURI = "";
    private FirebaseAuth mAuth;
    private DatabaseReference messagesRef, usersRef;
    private StorageReference storageReference;
    private String userId = "";
    ProgressDialog progressDialog;
    int PICK_IMAGE = 123;
    Uri imageUri;
    Boolean isUser = false;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                // if No error is found then only it will run
                if (i != TextToSpeech.ERROR) {
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        //Initializing arrayList and Setting the Adapter
        chatList = new ArrayList<>();
        adapter = new MessageAdapter(this, chatList, textToSpeech);
        binding.chatRecycleView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecycleView.setAdapter(adapter);
        //Initializing the Progress Dialog
        progressDialog = new ProgressDialog(this);
        //Initializing the Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        //Getting Data from previous Screen
        userId = getIntent().getStringExtra("userId");
        //Initializing the Firebase Database for messages
        messagesRef = FirebaseDatabase.getInstance().getReference("Chats");
        //Initializing the Firebase Database for user info
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        //Initializing the Firebase Storage for images
        storageReference = FirebaseStorage.getInstance().getReference("ChatImages");
        //Calling send Message method when click on send image
        binding.ivSend.setOnClickListener(view -> sendMessage());

        binding.ivMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Clicking on link and moving to gallery
        binding.ivLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //Calling methods for loading the User Details
        loadUserDetails();
        //Calling methods for loading the Messages
        loadMessages();
    }

    private void loadMessages() {
        //Calling Method to get the data
        messagesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Clearing the list
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessagesModel message = snapshot.getValue(MessagesModel.class);
                    //Putting data into list
                    if (message.getSenderId().equals(mAuth.getCurrentUser().getUid()) && message.getReceiverId().equals(userId)) {
                        chatList.add(message);
                    } else if (message.getSenderId().equals(userId) && message.getReceiverId().equals(mAuth.getCurrentUser().getUid())) {
                        chatList.add(message);
                    }
                }
                //notifying the adapter
                adapter.notifyDataSetChanged();
                binding.chatRecycleView.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Send Message Method
    private void sendMessage() {
        @SuppressLint("SimpleDateFormat")
        //Getting Current date
        String formattedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        String messageText = binding.etMessage.getText().toString().trim();
        //Checking if image string is empty
        if (strImageURI.isEmpty()) {
            //If input text is empty showing the toast message
            if (messageText.isEmpty()) {
                Toast.makeText(this, "Please write message", Toast.LENGTH_SHORT).show();
            } else {
                //If image is empty and text is not empty then storing data into database with type message and image empty
                String messageId = messagesRef.push().getKey();
                MessagesModel message = new MessagesModel(messageId, mAuth.getCurrentUser().getUid(), userId, messageText, formattedDate, "", "message");
                messagesRef.child(messageId).setValue(message);
                binding.etMessage.setText("");
            }
        } else {
            //If the image contains the data
            //if message is empty then calling the send Image Method and putting the message empty
            if (messageText.isEmpty()) {
                sendImageMessage("", formattedDate);
            } else {
                //if message is non empty then calling the send Image Method and putting the message text
                sendImageMessage(messageText, formattedDate);
            }

        }
    }

    private void sendImageMessage(String message, String date) {
        // Show Progress Dialog
        progressDialog.setTitle("Vita Connect");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        //Making child of image path
        StorageReference imageReference = storageReference.child(imageUri.getLastPathSegment());
        //Putting image into firebase storage
        imageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //Downloading the url of the image from storage
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Storing the uri of image into string
                        String downloadedUrl = uri.toString();
                        String messageId = messagesRef.push().getKey();
                        //Passing values into model
                        MessagesModel model = new MessagesModel(messageId, mAuth.getCurrentUser().getUid(), userId, message, date, downloadedUrl, "image");
                        //Putting all the data into Firebase Database
                        messagesRef.child(messageId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    strImageURI = "";
                                    binding.etMessage.setText("");
                                    binding.cvImage.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserDetails() {
            //Reading the data from firebase to show the username
            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    assert user != null;
                    binding.username.setText(user.getName());
                    if (!user.getImage().isEmpty()){
                        Glide.with(ChatActivity.this).load(user.getImage()).into(binding.ivProfile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            strImageURI = String.valueOf(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                binding.ivImage.setImageBitmap(bitmap);
                binding.cvImage.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == 1 && resultCode == RESULT_OK && data!= null){
            ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            binding.etMessage.setText(text.get(0));
        }
    }
}