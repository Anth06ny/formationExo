package anthony.com.smsmmsbomber.model.wsbeans.getboxendpoint;

public class GetBoxEndPointSendBean {

    private String host_uid;

    public GetBoxEndPointSendBean(String host_uid) {
        this.host_uid = host_uid;
    }

    public GetBoxEndPointSendBean() {
    }

    public String getHost_uid() {
        return host_uid;
    }

    public void setHost_uid(String host_uid) {
        this.host_uid = host_uid;
    }
}
