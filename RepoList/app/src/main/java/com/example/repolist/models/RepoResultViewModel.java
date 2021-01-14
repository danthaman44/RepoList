package com.example.repolist.view;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.repolist.api.RepositoryRetriever;
import com.example.repolist.models.Item;
import com.example.repolist.models.RepoResult;

public class RepoResultViewModel extends ViewModel {
    private final RepositoryRetriever mRepositoryRetriever;
    public final MutableLiveData<String> searchInput = new MutableLiveData<>();
    public final MutableLiveData<Boolean> errorData = new MutableLiveData<>();
    public final MutableLiveData<Boolean> loadingData = new MutableLiveData<>();
    public final MutableLiveData<RepoResult> repoData = new MutableLiveData<>();

    public RepoResultViewModel(Context context) {
        mRepositoryRetriever = new RepositoryRetriever(context);
    }

    private RepositoryRetriever.CallBack getCallback() {
        return new RepositoryRetriever.CallBack() {
            @Override
            public void onResponse(RepoResult response, boolean isSuccessful) {
                loadingData.setValue(false);
                errorData.setValue(!isSuccessful);
                if (isSuccessful) {
                    repoData.setValue(response);
                    mRepositoryRetriever.saveItems(response.items);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                loadingData.setValue(false);
                errorData.setValue(true);
            }
        };
    }

    public void fetchRepositories(String searchTerm) {
        mRepositoryRetriever.getRepositories(searchTerm, getCallback());
    }

    public void clearAllRepositories() {
        mRepositoryRetriever.clearAllItems();
    }

    public void clearRepository(Item repo) {
        mRepositoryRetriever.clearItem(repo);
    }
}
