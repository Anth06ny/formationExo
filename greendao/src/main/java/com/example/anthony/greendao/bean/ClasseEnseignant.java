package com.example.anthony.greendao.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import greendaobeans.DaoSession;
import greendaobeans.ClasseEnseignantDao;

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
public class ClasseEnseignant {

    @Id(autoincrement = true)
    private Long id;

    private Long enseignantId;
    private Long classeId;
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
/** called by internal mechanisms, do not call yourself. */
@Generated(hash = 1792759407)
public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getClasseEnseignantDao() : null;
}
/** Used for active entity operations. */
@Generated(hash = 991473283)
private transient ClasseEnseignantDao myDao;

/** Used to resolve relations */
@Generated(hash = 2040040024)
private transient DaoSession daoSession;
public Long getClasseId() {
        return this.classeId;
}
public void setClasseId(Long classeId) {
        this.classeId = classeId;
}
public Long getEnseignantId() {
        return this.enseignantId;
}
public void setEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
}
public Long getId() {
        return this.id;
}
public void setId(Long id) {
        this.id = id;
}
@Generated(hash = 162869341)
public ClasseEnseignant(Long id, Long enseignantId, Long classeId) {
        this.id = id;
        this.enseignantId = enseignantId;
        this.classeId = classeId;
}
@Generated(hash = 441099057)
public ClasseEnseignant() {
}

    /* ---------------------------------
    // Generes
    // -------------------------------- */
}
