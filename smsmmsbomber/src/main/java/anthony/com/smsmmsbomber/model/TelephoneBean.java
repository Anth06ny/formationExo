package anthony.com.smsmmsbomber.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by Utilisateur on 08/06/2018.
 */
@Entity(

        active = true,
        generateConstructors = true,
        generateGettersSetters = true
)
public class TelephoneBean implements Serializable {
    public static final long serialVersionUID = 123456;

    @Id
    private Long id;
    private String numero;
    private long campagneId;

    //A envoyer au serveur
    private Boolean send;
    private Boolean received;
    private String Answer;
    private boolean sendToServer;


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 625075912)
    private transient TelephoneBeanDao myDao;

    public TelephoneBean(String numero) {
        this.numero = numero;
    }

    public TelephoneBean() {
    }


    /* ---------------------------------
    // Generate
    // -------------------------------- */

    @Generated(hash = 758663091)
    public TelephoneBean(Long id, String numero, Boolean send, Boolean received,
            String Answer, long campagneId) {
        this.id = id;
        this.numero = numero;
        this.send = send;
        this.received = received;
        this.Answer = Answer;
        this.campagneId = campagneId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return this.numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Boolean getSend() {
        return this.send;
    }

    public void setSend(Boolean send) {
        this.send = send;
    }

    public Boolean getReceived() {
        return this.received;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }

    public String getAnswer() {
        return this.Answer;
    }

    public void setAnswer(String Answer) {
        this.Answer = Answer;
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

    public long getCampagneId() {
        return this.campagneId;
    }

    public void setCampagneId(long campagneId) {
        this.campagneId = campagneId;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 149482527)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTelephoneBeanDao() : null;
    }
}
