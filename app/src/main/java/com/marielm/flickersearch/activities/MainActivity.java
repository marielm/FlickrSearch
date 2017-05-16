package com.marielm.flickersearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.marielm.flickersearch.FlickrSearchApplication;
import com.marielm.flickersearch.R;
import com.marielm.flickersearch.network.PhotoSearchService;
import com.marielm.flickersearch.network.SearchResult;
import com.marielm.flickersearch.network.TagsResponse;
import com.marielm.flickersearch.util.ImageUrlUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.marielm.flickersearch.activities.SearchDialog.KEY_FILTER;
import static com.marielm.flickersearch.activities.SearchDialog.KEY_TAG;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SEARCH_TAG = 1;
    private static final int REQUEST_SEARCH_FILTER = 2;

    @BindView(R.id.progress_bar) View progressBar;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.empty_view) View emptyView;
    @BindView(R.id.empty_view_text) TextView emptyMessage;

    @Inject PhotoSearchService service;

    private SearchAdapter adapter;
    private boolean showFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        FlickrSearchApplication.getGraph().inject(this);

        adapter = new SearchAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            showEmpty("No results, try searching!");
        } else {
            showResults();
        }

    }

    @OnClick(R.id.search_fab)
    public void onSearchClick(View view) {
        startActivityForResult(SearchDialog.create(MainActivity.this, KEY_TAG), REQUEST_SEARCH_TAG);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_SEARCH_TAG && data.hasExtra(KEY_TAG)) {
            String tag = data.getStringExtra(KEY_TAG);
            getSearchResults(tag);
        } else if (requestCode == REQUEST_SEARCH_FILTER && data.hasExtra(KEY_FILTER)) {
            String filterText = data.getStringExtra(KEY_FILTER);
            handleFilterResults(filterText);
        }
    }

    private void handleFilterResults(String filterText) {
        adapter.filterResults(filterText);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.filter).setVisible(showFilter);

        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                startActivityForResult(SearchDialog.create(MainActivity.this, KEY_FILTER), REQUEST_SEARCH_FILTER);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSearchResults(String tag) {
        showProgress();
        service.getTaggedPhotos(tag).enqueue(new Callback<TagsResponse>() {
            @Override
            public void onResponse(Call<TagsResponse> call, Response<TagsResponse> response) {
                if (response.isSuccessful()) {

                    showFilter = true;
                    invalidateOptionsMenu();

                    adapter.setData(response.body().photos.photo);
                    showResults();
                } else {
                    showFilter = false;
                    invalidateOptionsMenu();

                    showEmpty("Error, give it another try!");
                }
            }

            @Override public void onFailure(Call<TagsResponse> call, Throwable t) {
                showEmpty("Error, give it another try!");
                showFilter = false;
                invalidateOptionsMenu();
            }
        });
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void showEmpty(String message) {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        emptyMessage.setText(message);
        recyclerView.setVisibility(View.GONE);
    }

    private void showResults() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<SearchResult> data;
        private List<SearchResult> total;

        public void setData(List<SearchResult> data) {
            this.data = data;
            this.total = data;
        }

        public void filterResults(final String text) {

            Iterable<SearchResult> filtered = Iterables.filter(total, new Predicate<SearchResult>() {
                @Override public boolean apply(SearchResult item) {
                    return item.title.contains(text);
                }
            });

            ArrayList<SearchResult> results = Lists.newArrayList(filtered);

            if (results.size() > 0) {
                data = results;
                notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "No results on filter", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_result, parent, false));
        }

        @Override public void onBindViewHolder(SearchViewHolder holder, int position) {
            SearchResult item = data.get(position);

            Glide.with(MainActivity.this)
                    .load(ImageUrlUtil.getUrl(item.farm, item.server, item.id, item.secret))
                    .crossFade()
                    .into(holder.photo);
        }

        @Override public int getItemCount() {
            if (data != null) return data.size();

            return 0;
        }
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image) ImageView photo;

        public SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
