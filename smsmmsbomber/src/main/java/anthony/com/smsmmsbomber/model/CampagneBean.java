package anthony.com.smsmmsbomber.model;

import android.graphics.Bitmap;

import com.formation.utils.exceptions.TechnicalException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;
import anthony.com.smsmmsbomber.utils.LogUtils;

public class CampagneBean {

    int campagneId;
    String urlFile;
    String message;
    boolean accusedReceipt; //est ce qu'on activel'accusé reception
    boolean accusedSend;
    //is video
    boolean video;
    String urlEnd;
    String urlReceipt;
    ArrayList<PhoneBean> phoneBeans;

    //HORS WEB SERVICE
    //image
    Bitmap bitmap;
    //video
    byte[] videoFile;

    public CampagneBean() {
    }

    public CampagneBean(ArrayList<PhoneBean> phoneBeans, String urlFile) {
        this.phoneBeans = phoneBeans;
        this.urlFile = urlFile;
    }

    public static boolean isCampagneReady(CampagneBean campagneBean) throws TechnicalException {
        //Si on a des numéros
        if (campagneBean != null && campagneBean.getPhoneBeans() != null && !campagneBean.getPhoneBeans().isEmpty()) {

            //Si on a un message ou une video ou une image
            if (StringUtils.isNotBlank(campagneBean.getUrlFile())) {
                if (campagneBean.getBitmap() == null && campagneBean.getVideoFile() == null) {
                    throw new TechnicalException("Campagne chargée mais problème de chargement du media");
                }
                return true;
            }
            else if (StringUtils.isNotBlank(campagneBean.getMessage())) {
                return true;
            }
        }

        LogUtils.w("TAG_CAMPAGNE", "CampagneBean=" + campagneBean);

        return false;
    }

    @Override
    public String toString() {
        return "CampagneBean{" +
                "campagneId=" + campagneId +
                ", phoneBeans=" + phoneBeans +
                ", urlFile='" + urlFile + '\'' +
                ", message='" + message + '\'' +
                ", bitmap=" + bitmap +
                ", videoFile=" + Arrays.toString(videoFile) +
                ", video=" + video +
                '}';
    }


    /* ---------------------------------
    // get/set
    // -------------------------------- */

    public int getCampagneId() {
        return campagneId;
    }

    public void setCampagneId(int campagneId) {
        this.campagneId = campagneId;
    }

    public ArrayList<PhoneBean> getPhoneBeans() {
        return phoneBeans;
    }

    public void setPhoneBeans(ArrayList<PhoneBean> phoneBeans) {
        this.phoneBeans = phoneBeans;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAccusedReceipt() {
        return accusedReceipt;
    }

    public void setAccusedReceipt(boolean accusedReceipt) {
        this.accusedReceipt = accusedReceipt;
    }

    public boolean isAccusedSend() {
        return accusedSend;
    }

    public void setAccusedSend(boolean accusedSend) {
        this.accusedSend = accusedSend;
    }

    public String getUrlEnd() {
        return urlEnd;
    }

    public void setUrlEnd(String urlEnd) {
        this.urlEnd = urlEnd;
    }

    public String getUrlReceipt() {
        return urlReceipt;
    }

    public void setUrlReceipt(String urlReceipt) {
        this.urlReceipt = urlReceipt;
    }
}
