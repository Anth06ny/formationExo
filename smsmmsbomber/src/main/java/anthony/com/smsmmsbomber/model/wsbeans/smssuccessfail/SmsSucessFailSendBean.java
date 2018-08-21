package anthony.com.smsmmsbomber.model.wsbeans.smssuccessfail;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import anthony.com.smsmmsbomber.model.AnswerBean;

public class SmsSucessFailSendBean {
    private String host_uid;

    @SerializedName("scheduleds_to_send")
    List<AnswerBean> phoneList;

    public SmsSucessFailSendBean() {
    }

    public SmsSucessFailSendBean(String host_uid, List<AnswerBean> phoneList) {
        this.host_uid = host_uid;
        this.phoneList = phoneList;
    }

    public String getHost_uid() {
        return host_uid;
    }

    public void setHost_uid(String host_uid) {
        this.host_uid = host_uid;
    }

    public List<AnswerBean> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(ArrayList<AnswerBean> phoneList) {
        this.phoneList = phoneList;
    }
}
