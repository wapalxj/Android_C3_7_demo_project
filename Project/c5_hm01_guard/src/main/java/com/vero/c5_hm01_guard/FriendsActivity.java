package com.vero.c5_hm01_guard;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Utils.MyConstants;
import domain.ContactBean;
import engine.ReadContactsEngine;


/**
 * 显示所有联系人界面
 */
public class FriendsActivity extends BaseFriendsCallSmsActivity {

    /**
     * 提取数据的核心方法,需要覆盖此方法完成数据的显示
     * @return
     */
    @Override
    public List<ContactBean> getDatas() {
        return ReadContactsEngine.readContacts(getApplicationContext());
    }
}
