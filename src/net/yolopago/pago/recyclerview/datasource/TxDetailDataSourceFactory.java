package net.yolopago.pago.recyclerview.datasource;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;


public class TxDetailDataSourceFactory extends DataSource.Factory {
    private MutableLiveData<TxDetailDataSource> mutableLiveData;
    private TxDetailDataSource txDetailDataSource;
   Context context;

    public TxDetailDataSourceFactory(Context context) {
        this.context=context;
        mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {
        txDetailDataSource = new TxDetailDataSource(context);
        mutableLiveData.postValue(txDetailDataSource);
        return txDetailDataSource;
    }

    public MutableLiveData<TxDetailDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
