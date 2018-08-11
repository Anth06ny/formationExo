package anthony.com.smsmmsbomber.model.wsbeans.smssent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;

public class SmsSentSendBean {
    private String IMEI;
    @SerializedName("scheduleds_to_send")
    ArrayList<PhoneBean> phoneList;

    public SmsSentSendBean() {
    }

    public SmsSentSendBean(String IMEI, ArrayList<PhoneBean> phoneList) {
        this.IMEI = IMEI;
        this.phoneList = phoneList;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public ArrayList<PhoneBean> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(ArrayList<PhoneBean> phoneList) {
        this.phoneList = phoneList;
    }
}
