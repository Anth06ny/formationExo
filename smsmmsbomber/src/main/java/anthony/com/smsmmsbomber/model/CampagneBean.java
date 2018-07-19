package anthony.com.smsmmsbomber.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class CampagneBean {
    ArrayList<TelephoneBean> telephoneBeans;
    String urlFile;

    //image
    Bitmap bitmap;
    //video
    byte[] videoFile;

    //is video
    boolean video;


    public CampagneBean() {
    }

    public CampagneBean(ArrayList<TelephoneBean> telephoneBeans, String urlFile) {
        this.telephoneBeans = telephoneBeans;
        this.urlFile = urlFile;
    }

    public ArrayList<TelephoneBean> getTelephoneBeans() {
        return telephoneBeans;
    }

    public void setTelephoneBeans(ArrayList<TelephoneBean> telephoneBeans) {
        this.telephoneBeans = telephoneBeans;
    }

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public byte[] getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(byte[] videoFile) {
        this.videoFile = videoFile;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }
}
