package anthony.com.smsmmsbomber.model.dao;

import org.greenrobot.greendao.query.DeleteQuery;

import java.util.List;

import anthony.com.smsmmsbomber.MyApplication;
import anthony.com.smsmmsbomber.model.CampagneBean;
import anthony.com.smsmmsbomber.model.TelephoneBean;
import anthony.com.smsmmsbomber.model.TelephoneBeanDao;

public class TelephoneDaoManager {

    public static void save(TelephoneBean telephoneBean) {
        getDao().insertOrReplace(telephoneBean);
    }

    public static void save(CampagneBean campagneBean) {
        //On supprime l'ancienne campagne

        for (TelephoneBean telephoneBean : campagneBean.getTelephoneBeans()) {
            telephoneBean.setCampagneId(campagneBean.getCampagneId());
        }

        getDao().insertInTx(campagneBean.getTelephoneBeans());
    }

    public static TelephoneBean getTelephone(long id) {
        return getDao().load(id);
    }

    public static List<TelephoneBean> getTelephoneFromCampagneId(long campagneId) {
        return getDao().queryBuilder().where(TelephoneBeanDao.Properties.CampagneId.eq(campagneId)).list();
    }

    /**
     * les reponses reçus
     *
     * @return
     */
    public static List<TelephoneBean> getTelephoneWithAnswer() {
        return getDao().queryBuilder().where(TelephoneBeanDao.Properties.Answer.isNotNull()).list();
    }

    public static void deleteList(List<TelephoneBean> telephoneBeans) {
        getDao().deleteInTx(telephoneBeans);
        MyApplication.getDaoSession().clear();
    }

    /**
     * On supprime tous les numéros INFERIEUR à la campagne et qui n'ont pas le champs Answer rempli. (celui ci servant au message que l'on recoit et supprimer
     * s'il sont bien envoyé par le serveur)
     *
     * @param campagneId
     */
    public static void delete(long campagneId) {
        final DeleteQuery<TelephoneBean> tableDeleteQuery = getDao().queryBuilder().where(TelephoneBeanDao.Properties.CampagneId.lt(campagneId), TelephoneBeanDao
                .Properties.Answer.isNull()).buildDelete();
        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
        MyApplication.getDaoSession().clear();
    }

    public static TelephoneBeanDao getDao() {
        return MyApplication.getDaoSession().getTelephoneBeanDao();
    }
}
