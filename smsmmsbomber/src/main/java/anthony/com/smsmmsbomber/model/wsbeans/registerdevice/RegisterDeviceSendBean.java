package anthony.com.smsmmsbomber.model.wsbeans.registerdevice;

import anthony.com.smsmmsbomber.MyApplication;

public class RegisterDeviceSendBean {

    private String ip_remote;
    private String host_uid;
    private String box_version;  //v0.0.0.2.xxx ou xxx correspond au numerond de la version mobile
    private String SIM_status = "ready";
    private int SIM_ready = 1;
    private RegisterDeviceInformationsBean informations;

    public RegisterDeviceSendBean() {
    }

    public RegisterDeviceSendBean(String ip_remote, String host_uid, String imei) {
        this.ip_remote = ip_remote;
        this.host_uid = host_uid;
        informations = new RegisterDeviceInformationsBean(imei);
        box_version = MyApplication.getVersionAppli();
    }

    public String getIp_remote() {
        return ip_remote;
    }

    public void setIp_remote(String ip_remote) {
        this.ip_remote = ip_remote;
    }

    public String getHost_uid() {
        return host_uid;
    }

    public void setHost_uid(String host_uid) {
        this.host_uid = host_uid;
    }

    public String getSIM_status() {
        return SIM_status;
    }

    public void setSIM_status(String SIM_status) {
        this.SIM_status = SIM_status;
    }

    public int getSIM_ready() {
        return SIM_ready;
    }

    public void setSIM_ready(int SIM_ready) {
        this.SIM_ready = SIM_ready;
    }

    public RegisterDeviceInformationsBean getInformations() {
        return informations;
    }

    public void setInformations(RegisterDeviceInformationsBean informations) {
        this.informations = informations;
    }
}
