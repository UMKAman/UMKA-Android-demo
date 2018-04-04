package com.umka.umka.classes;

import android.os.AsyncTask;
import android.util.Log;

import com.umka.umka.model.Chat;
import com.umka.umka.model.Message;

import java.util.List;

/**
 * Created by trablone on 5/22/17.
 */

public class ChatUpdateTask extends AsyncTask<Message, Void, List<Chat>> {

    private List<Chat> list;

    public ChatUpdateTask(List<Chat> list) {
        this.list = list;
    }

    @Override
    protected List<Chat> doInBackground(Message... messages) {
        Message message = messages[0];
        Log.e("tr", "chat.id: " + message.chat_id);
        for (int i = 0; i < list.size(); i++) {
            Chat item = list.get(i);
            Log.e("tr", "item.id: " + item.id);

            if (item.id == message.chat_id) {
                item.message = message;
                list.set(i, item);
                return list;
            }
        }

        Chat item = new Chat();
        item.message = message;
        item.id = message.chat_id;
        item.name = message.user_name;
        item.avatar = message.user_avatar;
        list.add(item);

        return list;
    }
}
