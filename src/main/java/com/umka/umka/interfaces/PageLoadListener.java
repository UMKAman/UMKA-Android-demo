package com.umka.umka.interfaces;

import org.json.JSONArray;

/**
 * Created by trablone on 11/29/16.
 */

public interface PageLoadListener {
    void onLoadSuccess(JSONArray array);
    void onLoadFailure();
}
