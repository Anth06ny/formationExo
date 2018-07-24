package anthony.com.smsmmsbomber.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.formation.utils.exceptions.TechnicalException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class CampagneBean {

    int campagneId;
    ArrayList<TelephoneBean> telephoneBeans;
    String urlFile;
    String message;

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

    public static boolean isCampagneReady(CampagneBean campagneBean) throws TechnicalException {
        //Si on a des numéros
        if (campagneBean != null && campagneBean.getTelephoneBeans() != null && !campagneBean.getTelephoneBeans().isEmpty()) {

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

        Log.w("TAG_CAMPAGNE", "CampagneBean=" + campagneBean);

        return false;
    }

    @Override
    public String toString() {
        return "CampagneBean{" +
                "campagneId=" + campagneId +
                ", telephoneBeans=" + telephoneBeans +
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
