package com.vero.c5_hm01_guard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

import domain.ContactBean;
import engine.ReadContactsEngine;

public class SmsLogsActivity extends BaseFriendsCallSmsActivity {

    /**
     * 提取数据的核心方法,需要覆盖此方法完成数据的显示
     * @return
     */
    @Override
    public List<ContactBean> getDatas() {
        return ReadContactsEngine.readSmsLog(getApplicationContext());
    }
}

