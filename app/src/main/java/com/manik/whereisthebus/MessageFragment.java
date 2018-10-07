package com.manik.whereisthebus;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageFragment extends Fragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private MessageAdap messageAdap;
    private List<UMessage> messageList = new ArrayList<>();


    public MessageFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.messagefrag, container, false);

        // Get a reference to the ImageView in the fragment layout
        recyclerView = rootView.findViewById(R.id.remessage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                    UMessage uMessage = msgSnapshot.getValue(UMessage.class);
                    messageList.add(uMessage);
                }
                messageAdap = new MessageAdap(getContext(),messageList);
                recyclerView.setAdapter(messageAdap);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final EditText em = rootView.findViewById(R.id.em);
        em.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String sdate = new SimpleDateFormat("dd-MMM-yy hh:mm aa", Locale.getDefault()).format(new Date());
                    String msg = em.getText().toString();
                    String key = databaseReference.push().getKey();
                    UMessage message = new UMessage(msg, sdate, "manik");
                    databaseReference.child(key).setValue(message);
                    em.setText("");
                    return true;
                }
                return false;
            }
        });


        return rootView;

    }


}

