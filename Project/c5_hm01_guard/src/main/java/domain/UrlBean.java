package domain;

/**
 * Created by vero on 2016/9/6.
 * url信息封装
 */
public class UrlBean {
    private String url;//apk下的路径
    private int versionCode;//版本号
    private String desc;//新版本的描述信息

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
