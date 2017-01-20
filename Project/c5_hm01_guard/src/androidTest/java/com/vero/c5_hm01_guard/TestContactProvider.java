package com.vero.c5_hm01_guard;

import android.test.AndroidTestCase;

import Utils.ServiceUtils;
import engine.ReadContactsEngine;

/**
 * Created by Administrator on 2017/1/2.
 */

public class TestContactProvider extends AndroidTestCase {
    public void testReadContacts(){
        ReadContactsEngine.readContacts(getContext());//获取的是虚拟的上下文
    }
    public void testRunningServices(){
        ServiceUtils.isServiceRunning(getContext(),"");
    }
}
