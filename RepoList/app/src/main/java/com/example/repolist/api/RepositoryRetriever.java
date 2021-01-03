package com.example.repolist.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.room.Room;

import com.example.repolist.models.Item;
import com.example.repolist.models.RepoResult;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RepositoryRetriever {

    public interface CallBack {

        void onResponse(RepoResult result, boolean isSuccessful);

        void onFailure(Throwable t);
    }

    private static String BASE_URL = "https://api.github.com/";
    private static String SEARCH_PREFIX = "language:";
    private static String DB_NAME = "repolist-db";
    private static int THREAD_POOL_SIZE = 1;

    private final GitHubService service;
    private final AppDatabase database;
    private final ExecutorService executorService;
    private final Handler handler;

    public RepositoryRetriever(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(GitHubService.class);
        database = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        handler = new Handler(Looper.getMainLooper());
    }

    private Callback<RepoResult> getCallback(RepositoryRetriever.CallBack callBack) {
        return new Callback<RepoResult>() {
            @Override
            public void onResponse(Call<RepoResult> call, Response<RepoResult> response) {
                if (response.body() != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onResponse(response.body(), response.isSuccessful());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RepoResult> call, Throwable t) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure(t);
                    }
                });
            }
        };
    }

    public void getRepositories(String language, RepositoryRetriever.CallBack callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Item> items = database.userDao().getByLanguage(language);
                if (items != null && !items.isEmpty()) {
                    RepoResult result = new RepoResult(items);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(result, true);
                        }
                    });
                } else {
                    Call<RepoResult> call = service.searchRepositories(SEARCH_PREFIX + language);
                    call.enqueue(getCallback(callback));
                }
            }
        };
        executorService.submit(runnable);
    }

    public void saveItems(List<Item> items) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                database.userDao().insertAll(items);
            }
        };
        executorService.submit(runnable);
    }

    public void clearAllItems() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                database.userDao().deleteAll();
            }
        };
        executorService.submit(runnable);
    }
}
