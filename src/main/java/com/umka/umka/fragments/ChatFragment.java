package com.umka.umka.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umka.umka.R;
import com.umka.umka.activity.MessagesActivity;
import com.umka.umka.adapters.ChatAdapter;
import com.umka.umka.classes.ChatUpdateTask;
import com.umka.umka.classes.ParseChatTask;
import com.umka.umka.interfaces.PageLoadListener;
import com.umka.umka.model.BaseModel;
import com.umka.umka.model.Chat;
import com.umka.umka.model.Message;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by trablone on 11/14/16.
 */

public class ChatFragment extends BaseRecyclerFragment {

    public static ChatFragment newInstance(String type){
        ChatFragment fragment = new ChatFragment();
        Bundle params = new Bundle();
        params.putString("type", type);
        fragment.setArguments(params);
        return fragment;
    }
    private ChatAdapter adapter;


    @Override
    public int getNumColumns() {
        return 1;
    }

    @Override
    public void getNextPage(int page) {

    }

    @Override
    public void updateData() {

    }

    @Override
    public boolean isEnableUpdate() {
        return false;
    }

    private View shadow;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    private String type;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("tr", "onActivityResult chat");
        if (resultCode == Activity.RESULT_OK){
            Message message = (Message)data.getSerializableExtra("message");
            updateChat(message);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ChatAdapter(getBaseActivity(), this);
        getRecyclerView().setAdapter(adapter);
        type = getArguments().getString("type");

        intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.umka.message");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message m = (Message)intent.getSerializableExtra("message");
                updateChat(m);
            }
        };
    }

    private void updateChat(Message m){
        new ChatUpdateTask(adapter.getList()){
            @Override
            protected void onPostExecute(List<Chat> list) {
                super.onPostExecute(list);
                adapter.updateList(list);
                Log.e("tr", "updateList");
            }
        }.execute(m);
    }

    private void getChatList(){
        loadPage("/chat", null, new PageLoadListener() {
            @Override
            public void onLoadSuccess(JSONArray array) {
                Log.d("ChatsList", array.toString());
                new ParseChatTask(type){
                    @Override
                    protected void onPostExecute(List<Chat> chats) {
                        super.onPostExecute(chats);
                        adapter.updateList(chats);
                    }
                }.execute(array);
            }

            @Override
            public void onLoadFailure() {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, intentFilter);
        getChatList();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("tr", "stop");
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.e("tr", "start");
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        Log.e("tr", "pause");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }

    @Override
    public void onItemClickListener(int position, BaseModel base) {

        Chat item = (Chat)base;
        Intent intent = new Intent(getContext(), MessagesActivity.class);
        intent.putExtra("chat_id", item.id);
        intent.putExtra("title", item.name);
        startActivityForResult(intent, 500);
    }
}
