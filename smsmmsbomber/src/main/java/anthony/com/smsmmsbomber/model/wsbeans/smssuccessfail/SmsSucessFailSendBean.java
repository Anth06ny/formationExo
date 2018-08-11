package anthony.com.smsmmsbomber.model.wsbeans.smssuccessfail;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import anthony.com.smsmmsbomber.model.AnswerBean;

public class SmsSucessFailSendBean {
    private String IMEI;
    @SerializedName("scheduleds_to_send")
    List<AnswerBean> phoneList;

    public SmsSucessFailSendBean() {
    }

    public SmsSucessFailSendBean(String IMEI, List<AnswerBean> phoneList) {
        this.IMEI = IMEI;
        this.phoneList = phoneList;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public List<AnswerBean> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(ArrayList<AnswerBean> phoneList) {
        this.phoneList = phoneList;
    }
}
