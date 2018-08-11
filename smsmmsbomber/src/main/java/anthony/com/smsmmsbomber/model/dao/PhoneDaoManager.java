package anthony.com.smsmmsbomber.model.dao;

public class PhoneDaoManager {

//    public static void save(PhoneBean phoneBean) {
//        getDao().insertOrReplace(phoneBean);
//    }
//
//    public static void save(CampagneBean campagneBean) {
//        //On supprime l'ancienne campagne
//
//        for (PhoneBean phoneBean : campagneBean.getPhoneBeans()) {
//            phoneBean.setCampagneId(campagneBean.getCampagneId());
//        }
//
//        getDao().insertInTx(campagneBean.getPhoneBeans());
//    }
//
//    public static PhoneBean getTelephone(long id) {
//        return getDao().load(id);
//    }
//
//    public static List<PhoneBean> getTelephoneFromCampagneId(long campagneId) {
//        return getDao().queryBuilder().where(PhoneBeanDao.Properties.CampagneId.eq(campagneId)).list();
//    }
//
//    public static void deleteList(List<PhoneBean> phoneBeans) {
//        getDao().deleteInTx(phoneBeans);
//        MyApplication.getDaoSession().clear();
//    }
//
//    /**
//     * On supprime tous les numéros INFERIEUR à la campagne
//     *
//     * @param campagneId
//     */
//    public static void delete(long campagneId) {
//        final DeleteQuery<PhoneBean> tableDeleteQuery = getDao().queryBuilder().where(PhoneBeanDao.Properties.CampagneId.lt(campagneId)).buildDelete();
//        tableDeleteQuery.executeDeleteWithoutDetachingEntities();
//        MyApplication.getDaoSession().clear();
//    }
//
//    public static PhoneBeanDao getDao() {
//        return MyApplication.getDaoSession().getPhoneBeanDao();
//    }
}
