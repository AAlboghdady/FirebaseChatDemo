package com.abdu.firebasechatdemo.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.abdu.firebasechatdemo.DataModel.UsersChattingList;
import com.abdu.firebasechatdemo.R;
import com.abdu.firebasechatdemo.ViewHolders.ChattingListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    LinearLayoutManager mLinearLayoutManager;
    RecyclerView usersRecycler;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<UsersChattingList, ChattingListViewHolder>
            mFirebaseUsersAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);

        usersRecycler = findViewById(R.id.users_chatting_recycler);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        usersRecycler.setLayoutManager(mLinearLayoutManager);
        setup(getSharedPreferences("USER", MODE_PRIVATE).getString("childId", "user_1"));
    }

    public void setup(String childId) {
        getSharedPreferences("USER", MODE_PRIVATE).edit().putString("childId", childId).commit();
//        FireBaseFunctions.available(this, true);
        // New child entries
        progressDialog.show();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        SnapshotParser<UsersChattingList> parser = new SnapshotParser<UsersChattingList>() {
            @Override
            public UsersChattingList parseSnapshot(DataSnapshot dataSnapshot) {
                UsersChattingList usersList = dataSnapshot.getValue(UsersChattingList.class);
               /* if (usersList != null) {
                    usersList.setChatRoomId(dataSnapshot.getKey());
                }*/
                return usersList;
            }
        };

        DatabaseReference messagesRef = mFirebaseDatabaseReference.child("UsersList/" + childId);
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
        FirebaseRecyclerOptions<UsersChattingList> options =
                new FirebaseRecyclerOptions.Builder<UsersChattingList>()
                        .setQuery(messagesRef, parser)
                        .build();

        mFirebaseUsersAdapter = new FirebaseRecyclerAdapter<UsersChattingList, ChattingListViewHolder>(options) {
            @Override
            public ChattingListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new ChattingListViewHolder(inflater.inflate(R.layout.item_users_chatting_list, viewGroup, false));
            }

            @Override
            protected void onBindViewHolder(final ChattingListViewHolder viewHolder,
                                            int position,
                                            final UsersChattingList friendlyMessage) {
                if (friendlyMessage.getChatRoomName() != null) {
                    viewHolder.userName.setText(friendlyMessage.getChatRoomName());
                }
                viewHolder.cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle mBundle = new Bundle();
                        mBundle.putString("RoomId",friendlyMessage.getChatRoomId());
                        Intent mIntent = new Intent(MainActivity.this, ChatActivity.class);
                        mIntent.putExtras(mBundle);
                        startActivity(mIntent);
                        //Toast.makeText(MainActivity.this, "" + friendlyMessage.getChatRoomId(), Toast.LENGTH_LONG).show();
                    }
                });
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
//        FireBaseFunctions.available(this, false);
        mFirebaseUsersAdapter.stopListening();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mFirebaseUsersAdapter.startListening();
//        FireBaseFunctions.available(this, true);
    }

    @Override
    protected void onDestroy() {
//        FireBaseFunctions.available(this, false);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mFirebaseUsersAdapter.stopListening();
        switch (item.getItemId()) {
            case R.id.cons1:
                setup("cons_1");
                break;
            case R.id.cons2:
                setup("cons_2");
                break;
            case R.id.user1:
                setup("user_1");
                break;
            case R.id.user2:
                setup("user_2");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }
}
