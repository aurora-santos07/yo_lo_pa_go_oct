package net.yolopago.pago.recyclerview.datasource;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;


public class TxListDataSourceFactory extends DataSource.Factory {
    private static final String TAG = "TxListDataSourceFactory";
    
    private MutableLiveData<TxListDataSource> mutableLiveData;
    private TxListDataSource txListDataSource;
    Context context;
    public TxListDataSourceFactory(Context context) {

        this.context=context;
        mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public DataSource create() {

        txListDataSource = new TxListDataSource(context);
        mutableLiveData.postValue(txListDataSource);
        return txListDataSource;
    }

    public MutableLiveData<TxListDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
