package anthony.com.smsmmsbomber.model.wsbeans.getscheduleds;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import anthony.com.smsmmsbomber.model.wsbeans.GenericAnswerBean;

public class GetScheduledAnswerBean extends GenericAnswerBean {

    @SerializedName("scheduleds_to_send")
    ArrayList<PhoneBean> phoneList;

    long delay;             //Delai d'envoie de chaque message

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public ArrayList<PhoneBean> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(ArrayList<PhoneBean> phoneList) {
        this.phoneList = phoneList;
    }
}
