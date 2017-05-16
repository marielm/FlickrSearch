package com.marielm.flickersearch.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.marielm.flickersearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchDialog extends AppCompatActivity {
    public static final String KEY_TAG = "key_tag";
    public static final String KEY_FILTER = "key_filter";

    private static final String KEY_SEARCH_TYPE = "key_search_type";

    @BindView(R.id.tag_input) TextInputLayout tagInput;
    @BindView(R.id.apply) View applyTag;

    private String key;

    public static Intent create(Context context, String searchType) {
        return new Intent(context, SearchDialog.class)
                .putExtra(KEY_SEARCH_TYPE, searchType);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_dialog);
        ButterKnife.bind(this);

        key = getIntent().getStringExtra(KEY_SEARCH_TYPE);

        applyTag.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String tag = tagInput.getEditText().getText().toString();

                if (!TextUtils.isEmpty(tag)) {
                    Intent extras = new Intent();
                    extras.putExtra(key, tag);
                    setResult(Activity.RESULT_OK, extras);
                    finish();
                } else {
                    tagInput.setErrorEnabled(true);
                    tagInput.setError("Please enter a search term");
                }
            }

        });

        tagInput.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                tagInput.setErrorEnabled(false);
                tagInput.setError(null);
            }
        });

    }
}
