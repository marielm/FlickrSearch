package com.marielm.flickersearch.network;

import java.util.List;

public class TagsResponse {
    public ResultsContainer photos;

    public class ResultsContainer {
        public int page;
        public int pages;
        public int perpage;
        public int total;
        public List<SearchResult> photo;
    }
}
