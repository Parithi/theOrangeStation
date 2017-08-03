package com.parithi.theorangestation.fragments;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parithi.theorangestation.R;
import com.parithi.theorangestation.api.TaxiApi;
import com.parithi.theorangestation.database.Contract;
import com.parithi.theorangestation.models.Taxi;
import com.parithi.theorangestation.models.TaxiList;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by earul on 19/12/16.
 */

public class TaxiFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, Callback<TaxiList> {

    private final String LOG_TAG = TaxiFragment.class.getSimpleName();
    private static final int TAXI_LOADER = 0;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView noDataFoundLabel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_taxi_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:{
                callApi();
                break;
            }
            case R.id.about:{
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("About");
                alertDialog.setMessage("V3.0. Database last updated on December 2016.\n\nThe Taxis provided here are subject to change without notice. If any of the numbers are not in use, kindly mail me at taxiapp@parithi.com. \n\nDeveloped by Parithi.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                break;
            }
        }
        return true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.taxi_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        noDataFoundLabel = (TextView) view.findViewById(R.id.no_data_found_label);
        getLoaderManager().initLoader(TAXI_LOADER, null, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callApi();
    }

    private void callApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.parithinetwork.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TaxiApi taxiApi = retrofit.create(TaxiApi.class);

        Call<TaxiList> call = taxiApi.loadTaxis("android");
        call.enqueue(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                Contract.Taxi.CONTENT_URI,
                Contract.Taxi.PROJECTION,
                null,
                null,
                Contract.Taxi.NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data!=null && data.getCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            noDataFoundLabel.setVisibility(View.GONE);
            mAdapter = new TaxiAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            noDataFoundLabel.setVisibility(View.VISIBLE);
            Log.e("TaxiFragment","No data");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onResponse(Call<TaxiList> call, Response<TaxiList> response) {
        if(getContext()!=null) {
            getContext().getContentResolver().delete(Contract.Taxi.CONTENT_URI, null, null);
            List<Taxi> taxis = response.body().taxis;
            for (Taxi taxi : taxis) {
                ContentValues taxiValues = new ContentValues();
                taxiValues.put(Contract.Taxi._ID, taxi.getId());
                taxiValues.put(Contract.Taxi.NAME, taxi.getName());
                taxiValues.put(Contract.Taxi.PHONE_NUMBER, taxi.getPhoneNumber());
                getContext().getContentResolver().insert(Contract.Taxi.CONTENT_URI, taxiValues);
            }
        }
    }

    @Override
    public void onFailure(Call<TaxiList> call, Throwable t) {
        Log.d(LOG_TAG,"Unabled to fetch data");
    }

    private class TaxiAdapter extends RecyclerView.Adapter{

        Cursor taxiData;

        public TaxiAdapter(Cursor data) {
            this.taxiData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_list, parent, false);
            RecyclerView.ViewHolder vh = new TaxiHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            taxiData.moveToPosition(position);
            ((TaxiHolder) holder).taxiNameTextView.setText(taxiData.getString(taxiData.getColumnIndex(Contract.Taxi.NAME)));
//            ((TaxiHolder) holder).taxiPhoneNumberTextView.setText(taxiData.getString(taxiData.getColumnIndex(Contract.Taxi.PHONE_NUMBER)));
            ((TaxiHolder) holder).taxiPhoneNumberTextView.setText(taxiData.getString(10203));

            ((TaxiHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + taxiData.getString(taxiData.getColumnIndex(Contract.Taxi.PHONE_NUMBER))));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return taxiData.getCount();
        }
    }

    private static class TaxiHolder extends RecyclerView.ViewHolder {
        public TextView taxiNameTextView;
        public TextView taxiPhoneNumberTextView;
        public View itemView;

        public TaxiHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.taxiNameTextView = (TextView) itemView.findViewById(R.id.primary_text_view);
            this.taxiPhoneNumberTextView = (TextView) itemView.findViewById(R.id.secondary_text_view);
        }
    }
}
