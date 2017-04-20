package domain;

/**
 * Created by Administrator on 2017/4/6.
 * 黑名单
 */

public class BlackBean {
    private String phone;
    private int mode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "[ phone= "+phone+",mode= "+mode+" ]";
    }

    @Override
    public int hashCode() {
        return phone.hashCode();//号码一些，hashCode也一样
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof BlackBean){
            BlackBean bean= (BlackBean) o;
            return phone.equals(bean.getPhone());
        }else {
            return false;
        }
    }
}
