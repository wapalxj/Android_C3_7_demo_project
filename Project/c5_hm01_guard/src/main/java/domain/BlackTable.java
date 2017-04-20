package domain;

/**
 * Created by Administrator on 2017/4/6.
 * 黑名单表的结构
 */

public interface BlackTable {
    String PHONE ="phone";//黑名单号码列
    String MODE="mode";//拦截模式列
    String BLACKTABLE="blacktb";//黑名单表明

    /**
     * 标记性常量，每一个标记只对应一个位为1
     * 比如001,010,100
     * 而不能是00,10,11(后两个数第一位为1)
     */
    int SMS=1<<0;//短信拦截--->01
    int TEL=1<<1;//电话拦截--->10
    int ALL=SMS | TEL;//全部拦截--->11

}
