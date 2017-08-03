package com.parithi.theorangestation.api;

import com.parithi.theorangestation.models.TaxiList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by earul on 20/12/16.
 */

public interface TaxiApi {

    // taxis.json --> CHENNAI
    // coimbatore.json --> COIMBATORE

    @GET("coimbatore.json")
    Call<TaxiList> loadTaxis(@Query("tagged") String tags);
}
