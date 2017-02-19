package com.formation.webservice;

import com.formation.webservice.bean.CityBean;
import com.formation.webservice.bean.ResultBean;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Anthony on 15/11/2014.
 */
public class CityWS {

    private final static String API_LOGIN = "login=webserviceexemple";
    private final static String API_KEY = "apikey=sof940dd26cf107eabf8bf6827f87c3ca8e8d82546";

    private final static String CITY_REQ = "http://www.citysearch-api.com/fr/city?&" + API_LOGIN + "&" + API_KEY;
    private final static String CP_PARAM = "cp";

    public interface CPService {

        @GET("/cp={cp}")
        ResultBean getCp(@Path("cp") String cp);

        @GET("/")
        ResultBean getCpV2(@Query("cp") String cp);
    }

    public static List<CityBean> getCityWithRetroFit(String postalCode) throws Exception {
        if (postalCode == null) {
            throw new Exception("Le code postal n'a pas été remplie");
        }

        CPService service = new Retrofit.Builder().baseUrl(CITY_REQ).build().create(CPService.class);
        ResultBean result = service.getCpV2(postalCode);
        if (result == null) {
            throw new Exception("result est nulle");
        }
        else if (result.getErrors() != null) {
            throw new Exception(result.getErrors().getMessage());
        }
        else if (result.getCityBean() == null) {
            throw new Exception("result.getCityBean() est nulle");
        }
        else {
            return Arrays.asList(result.getCityBean());
        }
    }

    public static List<CityBean> getCity(String postalCode) throws Exception {
        if (postalCode == null) {
            throw new Exception("Le code postal n'a pas été remplie");
        }
        String urlString = CITY_REQ + "&" + CP_PARAM + "=" + postalCode;

        InputStreamReader reader = null;

        try {

            String jsonResponse = OkHttpUtils.sendGetOkHttpRequest(urlString);
            Gson gson = new Gson();
            ResultBean result = gson.fromJson(jsonResponse, ResultBean.class);
            if (result == null) {
                throw new Exception("result est nulle");
            }
            else if (result.getErrors() != null) {
                throw new Exception(result.getErrors().getMessage());
            }
            else if (result.getCityBean() == null) {
                throw new Exception("result.getCityBean() est nulle");
            }
            else {
                return Arrays.asList(result.getCityBean());
            }
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (IOException e) {
            }
        }
    }
}
