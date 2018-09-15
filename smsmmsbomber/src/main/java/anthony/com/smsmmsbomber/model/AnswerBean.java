package anthony.com.smsmmsbomber.model;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import anthony.com.smsmmsbomber.model.wsbeans.getscheduleds.PhoneBean;

@Entity(

        active = true,
        generateConstructors = true,
        generateGettersSetters = true
)
public class AnswerBean {

    @Id(autoincrement = true)
    private Long id;
    private String outbox;
    private String number;

    private Boolean send;  //accusé d'envoie
    private String text;       //message recu
    private Boolean received; //Accusé reception

    @Transient
    private boolean mms;

    @Override
    public String toString() {
        return "AnswerBean{" +
                "id=" + id +
                ", outbox='" + outbox + '\'' +
                ", number='" + number + '\'' +
                ", send=" + send +
                ", mms=" + mms +
                ", text='" + text + '\'' +
                '}';
    }

    public AnswerBean(PhoneBean phoneBean) {
        if (phoneBean != null) {
            number = phoneBean.getNumber();
            outbox = "AN-OUT-" + phoneBean.getId();
            mms = StringUtils.isNotBlank(phoneBean.getUrlFichier());
        }
    }

    public void setMms(boolean mms) {
        this.mms = mms;
    }

    public boolean isMms() {
        return mms;
    }

    /* ---------------------------------
    // Generate
    // -------------------------------- */
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 877692854)
    private transient AnswerBeanDao myDao;

    @Generated(hash = 1347571961)
    public AnswerBean(Long id, String outbox, String number, Boolean send, String text,
            Boolean received) {
        this.id = id;
        this.outbox = outbox;
        this.number = number;
        this.send = send;
        this.text = text;
        this.received = received;
    }

    @Generated(hash = 1597358991)
    public AnswerBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getSend() {
        return this.send;
    }

    public void setSend(Boolean send) {
        this.send = send;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOutbox() {
        return this.outbox;
    }

    public void setOutbox(String outbox) {
        this.outbox = outbox;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }

    public Boolean getReceived() {
        return received;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1374602174)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAnswerBeanDao() : null;
    }
}
