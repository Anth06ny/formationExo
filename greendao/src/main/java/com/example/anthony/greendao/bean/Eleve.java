package com.example.anthony.greendao.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;

@Entity(

        // Flag to make an entity "active": Active entities have update,
        // delete, and refresh methods.
        active = true,

        // Specifies the name of the table in the database.
        // By default, the name is based on the entities class name.
        //nameInDb = "ELEVE",

        // Define indexes spanning multiple columns here.
        //        indexes = {
        //                @Index(value = "name DESC", unique = true)
        //        },

        // Flag if the DAO should create the database table (default is true).
        // Set this to false, if you have multiple entities mapping to one table,
        // or the table creation is done outside of greenDAO.
        //createInDb = false,

        // Whether an all properties constructor should be generated.
        // A no-args constructor is always required.
        generateConstructors = true,

        // Whether getters and setters for properties should be generated if missing.
        generateGettersSetters = true
)
public class Eleve {

    @Id(autoincrement = true)
    private Long id;
    private String nom;
    private String prenom;

    //Jointure avec la Classe : 1 élève à 1 classe
    @ToOne(joinProperty = "classeId")
    private Classe classe;
    private long classeId;
    /* ---------------------------------
    // Generes
    // -------------------------------- */

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
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 795697025)
    public void setClasse(@NotNull Classe classe) {
        if (classe == null) {
            throw new DaoException(
                    "To-one property 'classeId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.classe = classe;
            classeId = classe.getId();
            classe__resolvedKey = classeId;
        }
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 841537653)
    public Classe getClasse() {
        long __key = this.classeId;
        if (classe__resolvedKey == null || !classe__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClasseDao targetDao = daoSession.getClasseDao();
            Classe classeNew = targetDao.load(__key);
            synchronized (this) {
                classe = classeNew;
                classe__resolvedKey = __key;
            }
        }
        return classe;
    }

    @Generated(hash = 632681660)
    private transient Long classe__resolvedKey;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1736315468)
    private transient EleveDao myDao;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public long getClasseId() {
        return this.classeId;
    }

    public void setClasseId(long classeId) {
        this.classeId = classeId;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 311604629)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEleveDao() : null;
    }

    @Generated(hash = 1853661841)
    public Eleve(Long id, String nom, String prenom, long classeId) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.classeId = classeId;
    }

    @Generated(hash = 1796364228)
    public Eleve() {
    }
}
