package com.techticz.networking.livedata;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;


import com.techticz.networking.model.ApiResponse;
import com.techticz.networking.model.AppExecutors;
import com.techticz.networking.model.DataSource;
import com.techticz.networking.model.Resource;

import timber.log.Timber;

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 * <p>
 * You can read more about it in the <a href="https://developer.android.com/arch">Architecture
 * Guide</a>.
 *
 * @param <ResultType>
 * @param <RequestType>
 */
public abstract class NetworkBoundResource<ResultType, RequestType> {
    private final AppExecutors appExecutors;

    private final MediatorLiveData<Resource<ResultType>> resultDBType = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<RequestType>> resultNetworkType = new MediatorLiveData<>();

    @MainThread
    protected NetworkBoundResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        resultDBType.setValue(Resource.loading(null, null));
        resultNetworkType.setValue(Resource.loading(null, null));
        LiveData<ResultType> dbSource = loadFromDb();

            Timber.d("Adding db data source");
            resultDBType.addSource(dbSource, data -> {
                Timber.d("Data Changed, Removing db data source");
                resultDBType.removeSource(dbSource);
                DataSource dataSource = dataSource(data);
                if (dataSource == DataSource.BOTH || dataSource == DataSource.REMOTE) {
                    fetchFromNetwork(dbSource, dataSource);
                } else {
                    Timber.d("Again adding db data source");
                    resultDBType.addSource(dbSource, newData -> {
                                Timber.d("Data changed again, setting success value");
                                resultDBType.setValue(Resource.success(newData, dataSource));
                            }
                    );
                }
            });

    }

    private void fetchFromNetwork(final LiveData<ResultType> dbSource, DataSource dataSource) {
        Timber.d("Fetching from network, creating call..");
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        if (dataSource == DataSource.BOTH) {
            Timber.d("Again adding db data source after call");
            resultDBType.addSource(dbSource, newData -> {
                        Timber.d("Data changed , setting loading data");
                resultDBType.setValue(Resource.loading(newData, dataSource));
            }
            );
        }
        Timber.d("Adding API data source after call");
        resultDBType.addSource(apiResponse, response -> {
            Timber.d("ResponseContainer received , removing both data source");
            resultDBType.removeSource(apiResponse);
            resultDBType.removeSource(dbSource);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                Timber.d("ResponseContainer is successful!");
                appExecutors.diskIO().execute(() -> {
                    saveCallResult(processResponse(response));
                    appExecutors.mainThread().execute(() -> {
                        // we specially request a new live data,
                        // otherwise we will get immediately last cached value,
                        // which may not be updated with latest results received from network.
                        Timber.d("Adding db source after response to request new live data from DB");
                        resultDBType.addSource(loadFromDb(),
                                newData -> {
                                    Timber.d("Data changed or live data from DB, setting success data");
                            resultDBType.setValue(Resource.success(newData, dataSource));
                        });
                            }
                    );
                });
            } else {
                Timber.d("ResponseContainer is NOT successful!");
                onFetchFailed();
                Timber.d("Adding db source for error");
                resultDBType.addSource(dbSource,
                        newData -> {
                            Timber.d("DataChanged ,setting error response");
                    resultDBType.setValue(Resource.error(response.errorMessage, newData, dataSource));
                        });
            }
        });
    }

    protected void onFetchFailed() {
        Timber.d("onFetch Failed");
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return resultDBType;
    }
    public LiveData<Resource<RequestType>> asRequestLiveData(){
        return resultNetworkType;
    }
    @WorkerThread
    protected RequestType processResponse(ApiResponse<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @MainThread
    protected abstract DataSource dataSource(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();

}
