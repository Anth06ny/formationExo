package anthony.com.smsmmsbomber.model.dao;

import org.greenrobot.greendao.query.DeleteQuery;

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

    public static void getTelephone(String numero) {
        getDao().queryBuilder().where(TelephoneBeanDao.Properties.Numero.eq(numero)).list();
    }

    public static void delete(long campagneId) {
        final DeleteQuery<TelephoneBean> tableDeleteQuery = getDao().queryBuilder().where(TelephoneBeanDao.Properties.CampagneId.lt(campagneId)).buildDelete();
        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
        MyApplication.getDaoSession().clear();
    }

    public static TelephoneBeanDao getDao() {
        return MyApplication.getDaoSession().getTelephoneBeanDao();
    }
}
