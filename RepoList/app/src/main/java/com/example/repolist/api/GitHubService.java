package com.example.repolist.api;

import com.example.repolist.models.RepoResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GitHubService {

    @GET("/repositories")
    public Call<RepoResult> retrieveRepositories();

    @GET("/search/repositories?&sort=stars&order=desc&per_page=10")
    public Call<RepoResult> searchRepositories(@Query("q") String language);

}
