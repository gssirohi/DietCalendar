package com.techticz.networking.livedata;

/**
 * Created by YATRAONLINE\gyanendra.sirohi on 18/12/17.
 */

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;


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

 */
public abstract class NetworkResource<ResultType> {
    private final AppExecutors appExecutors;

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();

    @MainThread
    protected NetworkResource(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
        result.setValue(Resource.loading(null, null));
        fetchFromNetwork();
    }

    private void fetchFromNetwork() {
        Timber.d("Fetching from network, creating call..");
        LiveData<ApiResponse<ResultType>> apiResponse = createCall();
        result.addSource(apiResponse, response -> {
            Timber.d("ResponseContainer received , removing both data source");
            result.removeSource(apiResponse);

            if (response.isSuccessful() && response.body != null) {
                Timber.d("ResponseContainer is successful!");
                appExecutors.diskIO().execute(() -> {
                            saveCallResult(processResponse(response));
                        });
                result.addSource(apiResponse,
                        newData -> {
                            Timber.d("Data changed or live data from DB, setting success data");
                            result.setValue(Resource.success(processResponse(newData), DataSource.REMOTE));
                        });
            } else {
                Timber.d("ResponseContainer is NOT successful!");
                onFetchFailed();
                Timber.d("Adding db source for error");
                result.addSource(apiResponse,
                        errorResponse -> {
                            Timber.d("setting error response");
                            result.setValue(Resource.error(errorResponse.errorMessage, errorResponse.body, DataSource.REMOTE));
                        });
            }
        });
    }

    protected void onFetchFailed() {
        Timber.d("onFetch Failed");
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected ResultType processResponse(ApiResponse<ResultType> response) {
        return response.body;
    }

    @NonNull
    @MainThread
    protected abstract LiveData<ApiResponse<ResultType>> createCall();

    @WorkerThread
    protected abstract void saveCallResult(ResultType data);
}

