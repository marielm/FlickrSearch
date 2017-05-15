package com.marielm.flickersearch.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import com.marielm.flickersearch.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchDialog extends AppCompatActivity {

    @BindView(R.id.tag_input) TextInputLayout tagInput;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_dialog);
        ButterKnife.bind(this);

    }
}
