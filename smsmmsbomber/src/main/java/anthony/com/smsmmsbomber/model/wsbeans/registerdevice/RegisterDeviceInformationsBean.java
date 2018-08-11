package anthony.com.smsmmsbomber.model.wsbeans.registerdevice;

public class RegisterDeviceInformationsBean {
    private String IMEI;

    public RegisterDeviceInformationsBean() {
    }

    public RegisterDeviceInformationsBean(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }
}
