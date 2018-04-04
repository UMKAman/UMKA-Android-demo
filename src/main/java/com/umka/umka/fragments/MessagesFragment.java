package com.umka.umka.fragments;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.umka.umka.R;
import com.umka.umka.activity.LoginActivity;
import com.umka.umka.adapters.MessageAdapter;
import com.umka.umka.classes.BaseJsonHandler;
import com.umka.umka.classes.HttpClient;
import com.umka.umka.classes.ParseMessagesTask;
import com.umka.umka.classes.Utils;
import com.umka.umka.model.Message;
import com.umka.umka.model.Profile;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import br.com.instachat.emojilibrary.controller.TelegramPanel;
import br.com.instachat.emojilibrary.model.layout.TelegramPanelEventListener;
import cz.msebera.android.httpclient.Header;

/**
 * Created by trablone on 11/14/16.
 */

public class MessagesFragment extends BaseFragment implements TelegramPanelEventListener {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    public TelegramPanel mBottomPanel;
    private SharedPreferences preferences;
    private SharedPreferences.Editor e;
    private String url;
    private int chat_id;
    private boolean next = true;
    public boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private Profile user;
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    private Handler handler = new Handler();

    private NotificationManager notificationManager;
    private FloatingActionButton buttonDown;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.umka.umka.R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView) view.findViewById(com.umka.umka.R.id.recycler_view);
        buttonDown = (FloatingActionButton)view.findViewById(com.umka.umka.R.id.button_down);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = getBaseActivity().getUser();
        buttonDown.hide();
        url = getActivity().getIntent().getStringExtra("url");
        chat_id = getActivity().getIntent().getIntExtra("chat_id", 0);
        adapter = new MessageAdapter(getBaseActivity(), null, user.id);
        recyclerView.setHasFixedSize(true);
        //layoutManager.setStackFromEnd(Boolean.TRUE);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseActivity());
        String message = preferences.getString("last_message_" + url, null);
        mBottomPanel = new TelegramPanel(getBaseActivity(), getView(), this);
        mBottomPanel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                preferences.edit().putString("last_message" + url, editable.toString()).apply();
            }
        });
        mBottomPanel.setText(message);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {



            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager =  (LinearLayoutManager)recyclerView.getLayoutManager();

                visibleItemCount = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();

                pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();
                Log.e("tr", "pastVisiblesItems: " + pastVisiblesItems);
                Log.e("tr", "visibleItemCount: " + visibleItemCount);
                Log.e("tr", "totalItemCount: " + totalItemCount);
                Log.e("tr", "-------------------------------------");
                int count = pastVisiblesItems + visibleItemCount;
                if (count == totalItemCount){
                    buttonDown.hide();
                }else {
                    buttonDown.show();
                }
                if (isLoadNext() && pastVisiblesItems <= 10) {
                    loading = false;
                    getPrevMessages();
                }

            }
        });

        getMessages();
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.umka.message");
        notificationManager = (NotificationManager) getBaseActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(chat_id);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message m = (Message)intent.getSerializableExtra("message");
                if (m.chat_id == chat_id){
                    adapter.addMessage(m);

                    int count = pastVisiblesItems + visibleItemCount;
                    Log.e("tr", "count: " + count);
                    Log.e("tr", "adapter count: " + adapter.getItemCount());
                    if (totalItemCount == count) {
                        notificationManager.cancel(m.chat_id);
                        linearLayoutManager.scrollToPosition(adapter.getItemCount() - 1);
                    } else {
                        buttonDown.show();
                    }
                }

            }
        };

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notificationManager.cancel(chat_id);
                linearLayoutManager.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }


    private boolean isLoadNext() {
        return loading && next && adapter.getList().size() >= 20;
    }


    private void sendMessage(final String text) {
        RequestParams params = new RequestParams();
        params.put("chat", chat_id);

        if (text != null)
            params.put("text", StringEscapeUtils.escapeJava(text));

        if (file != null) {
            try {
                params.put("pic", file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
        final Message message = new Message();
        message.message = text;
        message.user_id = user.id;
        message.chat_id = chat_id;
        if (file != null) {
            message.image = file.getPath();
            message.imageFile = true;
        }
        adapter.addMessage(message);
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        mBottomPanel.setText("");
        HttpClient.post("/chatmessage", getBaseActivity(), params, new BaseJsonHandler(getBaseActivity()){


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                adapter.removeMessage(message);
                Message message = new Message(response);
                message.user_id = user.id;
                message.chat_id = chat_id;
                adapter.addMessage(message);
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
            }

        });
    }


    @Override
    public void onShowKeyboard() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                Log.e("tr", "show keyboard");
            }
        }, 250);

    }

    private void getPrevMessages() {

        HttpClient.get("/chatmessage?where={\"chat\":\"" + chat_id + "\"}&limit=20&skip=" + adapter.getList().size() + "&sort=\"id DESC\"", getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                next = response.length() == 20;

                new ParseMessagesTask() {
                    @Override
                    protected void onPostExecute(List<Message> messages) {

                        super.onPostExecute(messages);
                        adapter.addPreList(messages);
                        loading = true;
                    }
                }.execute(response);
            }

        });
    }

    public Message getLastMessage() {
        int count = adapter.getItemCount();
        Log.e("tr", "count: " + count);
        if (count > 0) {
            Message message = adapter.getList().get(count - 1);
            if (message.createdAt != null)
            return message;
        }

        return null;
    }

    private void getMessages() {

        HttpClient.get("/chatmessage?where={\"chat\":\"" + chat_id + "\"}&limit=20&sort=\"id DESC\"", getBaseActivity(), null, new BaseJsonHandler(getBaseActivity()){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                new ParseMessagesTask() {
                    @Override
                    protected void onPostExecute(List<Message> messages) {
                        super.onPostExecute(messages);
                        boolean scroll = false;

                        if (adapter.getItemCount() > 0 && messages.size() > 0) {
                            Message oldMessage = adapter.getList().get(adapter.getItemCount() - 1);
                            List<Message> newList = new ArrayList<>();
                            for (Message item : messages) {
                                if (item.id > oldMessage.id) {
                                    newList.add(item);
                                    scroll = user.id == item.user_id;
                                }
                            }
                            if (newList.size() > 0)
                                adapter.addList(newList);

                        } else if (messages.size() > 0) {
                            adapter.updateList(messages);
                            scroll = true;
                        }

                        if (scroll) {
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }
                }.execute(response);


            }

        });
    }


    @Override
    public void onPause() {

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        Utils.hideKeyboard(getBaseActivity());
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();

    }

    public static final int PICK_IMAGE = 700;
    private File file;

    @Override
    public void onAttachClicked() {

        try {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            String pickTitle = getResources().getString(R.string.select_app);
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

            startActivityForResult(chooserIntent, PICK_IMAGE);
        } catch (ActivityNotFoundException e) {

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getBaseActivity().RESULT_OK) {
            try {
                switch (requestCode) {
                    case PICK_IMAGE:
                        file = new File(getRealPathFromURI(data.getData()));
                        sendMessage(null);
                        break;
                }
            } catch (Throwable e) {

            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        Log.e("tr", "result: " + result);
        return result;
    }

    @Override
    public void onMicClicked() {
        //Toast.makeText(getBaseActivity(), "Mic was clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendClicked() {

        String message = mBottomPanel.getText();
        Log.e("tr", "message: " + message);

        if (!TextUtils.isEmpty(message))
            sendMessage(message);
    }
}
