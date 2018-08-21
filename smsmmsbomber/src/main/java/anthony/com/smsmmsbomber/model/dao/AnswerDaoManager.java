package anthony.com.smsmmsbomber.model.dao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import anthony.com.smsmmsbomber.MyApplication;
import anthony.com.smsmmsbomber.model.AnswerBean;
import anthony.com.smsmmsbomber.model.AnswerBeanDao;

public class AnswerDaoManager {

    public static void save(AnswerBean dao) {
        getDao().insertOrReplace(dao);
    }

    /**
     * les reponses reçus
     *
     * @return
     */
    public static List<AnswerBean> getAnswer() {
        return getDao().loadAll();
    }

    public static List<AnswerBean> getSmsReceived() {
        QueryBuilder<AnswerBean> qb = getDao().queryBuilder();
        return qb.where(AnswerBeanDao.Properties.Text.isNotNull()).list();
    }

    public static List<AnswerBean> getFailedDelivery() {
        return getDao().queryBuilder().where(AnswerBeanDao.Properties.Send.isNotNull()).list();
    }

    public static void deleteList(List<AnswerBean> list) {
        getDao().deleteInTx(list);
        MyApplication.getDaoSession().clear();
    }

    public static AnswerBeanDao getDao() {
        return MyApplication.getDaoSession().getAnswerBeanDao();
    }
}
