package com.fit.run.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fit.run.R;
import com.fit.run.adapter.FriendAdapter;
import com.fit.run.bean.Rank;
import com.fit.run.event.RefreshFriendEvent;
import com.fit.run.ui.base.BaseFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class FriendFragment extends BaseFragment {

    @BindView(R.id.rv_friend)
    RecyclerView mRvFriend;
    Unbinder unbinder;


    private FriendAdapter mFriendAdapter;
    private List<Rank> mUsers = new ArrayList<>();
    private List<String> mUids = new ArrayList<>();


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;

    private FirebaseUser mFirebaseUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_friend, null, false);
        unbinder = ButterKnife.bind(this, view);
        mRvFriend.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvFriend.setHasFixedSize(true);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        refresh();
        return view;
    }

    private void refresh() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        Query query = mDatabaseReference.child("friends").child(mFirebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                mUsers.clear();
                mUids.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String user = (String) snapshot.getValue();
                    mUids.add(user);
                }


                final int[] index = {0};
                for (String uid : mUids) {
                    mDatabaseReference.child("ranks").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Rank user = dataSnapshot.getValue(Rank.class);
                            index[0]++;
                            mUsers.add(user);
                            if (index[0] == mUids.size()) {

                                Collections.sort(mUsers, new Comparator<Rank>() {
                                    @Override
                                    public int compare(Rank s1, Rank s2) {
                                        return s2.getPoint() - s1.getPoint();
                                    }
                                });
                                mFriendAdapter = new FriendAdapter(mUsers);
                                mRvFriend.setAdapter(mFriendAdapter);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        registerEvent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterEvent();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshFriend(RefreshFriendEvent event) {
        refresh();
    }

}
