package com.abdu.firebasechatdemo.Views;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.abdu.firebasechatdemo.DataModel.FriendlyMessage;
import com.abdu.firebasechatdemo.DataModel.User;
import com.abdu.firebasechatdemo.R;
import com.abdu.firebasechatdemo.ViewHolders.ChattingMessages;
import com.abdu.firebasechatdemo.WebServices.Api;
import com.abdu.firebasechatdemo.WebServices.ApiFactory;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.abdu.firebasechatdemo.WebServices.ApiFactory.BASE_URL;

/**
 * Created by Abdullah on 6/3/2018.
 */

public class ChatActivity extends AppCompatActivity {
    private static final String SERVER_KEY = "AAAA23ahsI4:APA91bGoCj__OyHbmu5ES9x4u12Xtm-ZXdF-2t7iKAwmud9jAlGkVljeOpmZSRkarIByiVIOqRqfI3ISFE8kvQmEL9BUmhx0xOAvUDvYdKR2gat2qEfuVrounjFne5tkBjSh5bbelDUaa9Nitf5Xj0bQfzu9KJHHrQ";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    LinearLayoutManager mLinearLayoutManager;
    RecyclerView usersRecycler;
    String roomId;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<FriendlyMessage, ChattingMessages> mFirebaseUsersAdapter;
    private ProgressDialog progressDialog;
    private TextView tvUserName, tvAvailability;
    private String user_id;
    private int mTotalItemCount = 0;
    private int mLastVisibleItemPosition = 0;
    private boolean mIsLoading = false;
    private int mPostsPerPage = 2;
    private String theOtherUserToken = "";
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        user_id = getSharedPreferences("USER", MODE_PRIVATE).getString("childId", "");
        roomId = getIntent().getExtras().getString("RoomId");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);

        tvUserName = findViewById(R.id.tvUserName);
        tvAvailability = findViewById(R.id.tvAvailability);
        usersRecycler = findViewById(R.id.messages);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        usersRecycler.setLayoutManager(mLinearLayoutManager);

        getUserAvailability();

        getIntentData(getIntent().getExtras());
        ImageView mSendButton = (ImageView) findViewById(R.id.send_btn);
        final EditText messageET = (EditText) findViewById(R.id.messageET);

        usersRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mTotalItemCount = mLinearLayoutManager.getItemCount();
                mLastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();

//                if (!mIsLoading && mTotalItemCount <= (mLastVisibleItemPosition + mPostsPerPage)) {
                if (mTotalItemCount >= 2 &&
                        !mIsLoading &&
                        mLinearLayoutManager.findFirstVisibleItemPosition() == 0
//                        && mTotalItemCount < mLastVisibleItemPosition
                        ) {
                    setup(roomId, mLastVisibleItemPosition);
                    mIsLoading = true;
                }
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendlyMessage friendlyMessage = new
                        FriendlyMessage();
                friendlyMessage.setMessage(messageET.getText().toString());
                friendlyMessage.setCreateTimeStamp(getCurrentDate());
                friendlyMessage.setOwner(getSharedPreferences("USER", MODE_PRIVATE).getString("childId", ""));

                mFirebaseDatabaseReference.child("ChatRoomList/" + roomId)
                        .push().setValue(friendlyMessage);

                if (!user.isAvailable()) {
//                    sendNotificationToUser(user_id, messageET.getText().toString());
                    sendNotification(user_id, messageET.getText().toString());
                }
                messageET.setText("");
            }
        });
    }

    private void sendNotificationToUser(String user_name, String message) {
        Api api = ApiFactory.getClient().create(Api.class);
        Map<String, Object> map = new HashMap<>();
        JSONObject dataJson = new JSONObject();
        try {
            dataJson.put("body", message);
            dataJson.put("title", user_name);
        } catch (JSONException e)
            e.printStackTrace();
        }
        map.put("notification", dataJson);
        map.put("to", theOtherUserToken);
        Call<ResponseBody> call = api.sendNotification(BASE_URL, map, "key=" + SERVER_KEY);
        call.enqueue(new Callback<ResponseBody>() {
                         @Override
                         public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                             String output = null;


                             if (response.isSuccessful()) {

                                 try {
                                     BufferedReader reader = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                                     StringBuilder sb = new StringBuilder();
                                     String line = null;
                                     while ((line = reader.readLine()) != null) {
                                         sb.append(line + "\n");
                                     }
                                     output = sb.toString();
                                     Log.e("RESPONSE::", "Response:" + output);
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<ResponseBody> call, Throwable t) {
                         }
                     }
        );

    }

    @SuppressLint("StaticFieldLeak")
    private void sendNotification(final String user_name, final String message) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json = new JSONObject();
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("body", message);
                    dataJson.put("title", user_name);
                    dataJson.put("sound", "default");
                    json.put("data", dataJson);
                    json.put("to", theOtherUserToken);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization", "key=" + SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    okhttp3.Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                    Log.e("RESPONSE::", "Response:" + finalResponse);
                } catch (Exception e) {
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();
    }

    private String getTheOtherUserName() {
        String user_1 = roomId.substring(0, 6);
        String user_2 = roomId.substring(7, 13);

        return user_id.equals(user_1) ? user_2 : user_1;
//        if (user_id==user_1){
//            return user_2;
//        }else{
//            return user_1;
//        }
    }

    private void getIntentData(Bundle extras) {
        //TODO get chat room id from bundle and create adapter listener
        setup(roomId, mLastVisibleItemPosition);
    }

    public void setup(String roomId, int mLastVisibleItemPosition) {
        available(true, roomId);
        // New child entries
        progressDialog.show();
        SnapshotParser<FriendlyMessage> parser = new SnapshotParser<FriendlyMessage>() {
            @Override
            public FriendlyMessage parseSnapshot(DataSnapshot dataSnapshot) {
                FriendlyMessage usersList = dataSnapshot.getValue(FriendlyMessage.class);
               /* if (usersList != null) {
                    usersList.setChatRoomId(dataSnapshot.getKey());
                }*/
                return usersList;
            }
        };
        FirebaseRecyclerOptions<FriendlyMessage> options;
        if (mLastVisibleItemPosition == 0) {
            DatabaseReference messagesRef = mFirebaseDatabaseReference.child("ChatRoomList/" + roomId);
            options = new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                    .setQuery(messagesRef, parser)
                    .build();
        } else {
            Query messagesRef = mFirebaseDatabaseReference.child("ChatRoomList/" + roomId)
                    .orderByKey()
                    .startAt(String.valueOf(mLastVisibleItemPosition))
                    .limitToFirst(mPostsPerPage);
            options = new FirebaseRecyclerOptions.Builder<FriendlyMessage>()
                    .setQuery(messagesRef, parser)
                    .build();
        }
        /*mFirebaseDatabaseReference.child("UsersList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild("cons_id5")) {
                    // run some code
                    mFirebaseDatabaseReference.child("UsersList").child("cons_id5").push()
                            .setValue(new UsersChattingList("room6", "name"));
                    Log.d("adding new child", "child");
                } else
                    Log.d("new child", "child");


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        mFirebaseUsersAdapter = new FirebaseRecyclerAdapter<FriendlyMessage, ChattingMessages>(options) {
            @Override
            public ChattingMessages onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ChattingMessages(inflater.inflate(R.layout.item_messages, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final ChattingMessages viewHolder,
                                            int position,
                                            final FriendlyMessage friendlyMessage) {
                if (friendlyMessage.getMessage() != null) {
                    viewHolder.message.setText(friendlyMessage.getMessage());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (friendlyMessage.getOwner() == user_id) {
                            viewHolder.message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                        } else {
                            viewHolder.message.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                        }
                        mIsLoading = false;
                    }
                }

                progressDialog.dismiss();

            }
        };

        mFirebaseUsersAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseUsersAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    usersRecycler.scrollToPosition(positionStart);
                }
            }
        });

        usersRecycler.setAdapter(mFirebaseUsersAdapter);
        mFirebaseUsersAdapter.startListening();
    }

    @Override
    public void onPause() {
        available(false, "");
        mFirebaseUsersAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseUsersAdapter.startListening();
        available(true, roomId);
    }

    @Override
    protected void onDestroy() {
        available(false, "");
        super.onDestroy();
    }

    public String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = new Date();
        return formatter.format(date);
    }

    public void available(boolean availability, String roomId) {
        User user = new User();
        user.setAvailable(availability);
        user.setName(user_id);
        user.setChat_id(roomId);
        user.setToken(FirebaseInstanceId.getInstance().getToken());

        FirebaseDatabase.getInstance().getReference().child("users/")
                .child(user_id)
//                .push()
                .setValue(user);
    }

    public void getUserAvailability() {
        String theOtherUserName = getTheOtherUserName();
        FirebaseDatabase.getInstance().getReference().child("users/").child(theOtherUserName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                tvUserName.setText(user.getName());
                tvAvailability.setText(user.isAvailable() ? "online" : "offline");
                theOtherUserToken = user.getToken();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
