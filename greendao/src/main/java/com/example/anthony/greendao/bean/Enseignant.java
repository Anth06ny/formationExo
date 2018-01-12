package com.example.anthony.greendao.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

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
public class Enseignant {

    @Id(autoincrement = true)
    private Long id;
    private String nom;
    private String prenom;

    @ToMany
    @JoinEntity(
            //Table intermediaire
            entity = ClasseEnseignant.class,
            //Id representant cette table dans la table intermediaire
            sourceProperty = "enseignantId",
            //Id representant la table voulu dans la table intermediraire
            targetProperty = "classeId"
    )
    private List<Classe> classeList;
    /* ---------------------------------
    // Generes
    // -------------------------------- */

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1865954379)
    private transient EnseignantDao myDao;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    @Generated(hash = 1990634895)
    public Enseignant(Long id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    @Generated(hash = 992391933)
    public Enseignant() {
    }

    @Override
    public String toString() {
        return nom + " " + prenom;
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
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 739549146)
    public synchronized void resetClasseList() {
        classeList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1276104001)
    public List<Classe> getClasseList() {
        if (classeList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ClasseDao targetDao = daoSession.getClasseDao();
            List<Classe> classeListNew = targetDao._queryEnseignant_ClasseList(id);
            synchronized (this) {
                if (classeList == null) {
                    classeList = classeListNew;
                }
            }
        }
        return classeList;
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
    @Generated(hash = 1167124596)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getEnseignantDao() : null;
    }
}
