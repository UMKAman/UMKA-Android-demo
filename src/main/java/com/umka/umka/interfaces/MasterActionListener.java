package com.umka.umka.interfaces;

import com.umka.umka.model.Master;

/**
 * Created by trablone on 11/29/16.
 */

public interface MasterActionListener extends ItemClickListener{
    void onCall(Master item);
    void onMessage(Master item);
    void onFavorite(Master item);
}
