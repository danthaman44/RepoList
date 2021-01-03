package com.example.repolist.models;

import java.util.ArrayList;
import java.util.List;

public class RepoResult {
    public List<Item> items;

    public RepoResult() {
        items = new ArrayList<>();
    }

    public RepoResult(List<Item> repoItems) {
        items = repoItems;
    }
}
