package com.example.anthony.greendao.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.OrderBy;
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
public class Classe {
    @Id(autoincrement = true)
    private Long id;

    private String name;

    //Jointure Inverse Avec l'élève
    @ToMany(referencedJoinProperty = "classeId")
    @OrderBy("nom ASC")
    private List<Eleve> eleves;

    @ToMany
    @JoinEntity(
            //Table intermediaire
            entity = ClasseEnseignant.class,
            //Id representant cette table dans la table intermediaire
            sourceProperty = "classeId",
            //Id representant la table voulu dans la table intermediraire
            targetProperty = "enseignantId"
    )
    private List<Enseignant> enseignantList;
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
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 347321945)
    public synchronized void resetEnseignantList() {
        enseignantList = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 914912505)
    public List<Enseignant> getEnseignantList() {
        if (enseignantList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EnseignantDao targetDao = daoSession.getEnseignantDao();
            List<Enseignant> enseignantListNew = targetDao._queryClasse_EnseignantList(id);
            synchronized (this) {
                if (enseignantList == null) {
                    enseignantList = enseignantListNew;
                }
            }
        }
        return enseignantList;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 2129828932)
    public synchronized void resetEleves() {
        eleves = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 747741136)
    public List<Eleve> getEleves() {
        if (eleves == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EleveDao targetDao = daoSession.getEleveDao();
            List<Eleve> elevesNew = targetDao._queryClasse_Eleves(id);
            synchronized (this) {
                if (eleves == null) {
                    eleves = elevesNew;
                }
            }
        }
        return eleves;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1433894868)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClasseDao() : null;
    }

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1659662464)
    private transient ClasseDao myDao;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Generated(hash = 1415365288)
    public Classe(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 11538485)
    public Classe() {
    }
}
