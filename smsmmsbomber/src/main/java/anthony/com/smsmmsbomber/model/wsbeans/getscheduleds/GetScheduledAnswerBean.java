package anthony.com.smsmmsbomber.model.wsbeans.getscheduleds;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.model.wsbeans.AnswerStatusBean;

public class GetScheduledAnswerBean {

    @SerializedName("scheduleds_to_send")
    ArrayList<PhoneBean> phoneList;
    private AnswerStatusBean status;

    public ArrayList<PhoneBean> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(ArrayList<PhoneBean> phoneList) {
        this.phoneList = phoneList;
    }

    public AnswerStatusBean getStatus() {
        return status;
    }

    public void setStatus(AnswerStatusBean status) {
        this.status = status;
    }
}
