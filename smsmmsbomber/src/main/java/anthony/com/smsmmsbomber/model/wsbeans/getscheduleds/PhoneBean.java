package anthony.com.smsmmsbomber.model.wsbeans.getscheduleds;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PhoneBean implements Serializable {
    public static final long serialVersionUID = 123456;

    private long id;
    private String number;
    @SerializedName("id_compagne")
    private long campagneId;
    private String content;
    @SerializedName("URL")
    private String urlFichier;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getCampagneId() {
        return campagneId;
    }

    public void setCampagneId(long campagneId) {
        this.campagneId = campagneId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrlFichier() {
        return urlFichier;
    }

    public void setUrlFichier(String urlFichier) {
        this.urlFichier = urlFichier;
    }
}
