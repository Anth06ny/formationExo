package anthony.com.smsmmsbomber.model.wsbeans.getscheduleds;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetScheduledAnswerBean {

    @SerializedName("scheduleds_to_send")
    ArrayList<PhoneBean> phoneList;

    public String status;
    public int code;

    public ArrayList<PhoneBean> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(ArrayList<PhoneBean> phoneList) {
        this.phoneList = phoneList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
