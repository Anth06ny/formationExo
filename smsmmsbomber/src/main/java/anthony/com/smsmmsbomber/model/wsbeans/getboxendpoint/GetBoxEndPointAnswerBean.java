package anthony.com.smsmmsbomber.model.wsbeans.getboxendpoint;

import anthony.com.smsmmsbomber.model.wsbeans.AnswerStatusBean;

public class GetBoxEndPointAnswerBean {

    private String endpoint;
    private AnswerStatusBean status;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public AnswerStatusBean getStatus() {
        return status;
    }

    public void setStatus(AnswerStatusBean status) {
        this.status = status;
    }
}
