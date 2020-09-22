package com.specx.scan.ui.result.base;

import android.os.Bundle;

import static com.data.app.db.table.ResultTable.BATCH_ID;

public class ResultActivity extends BaseResult {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fetchResultFromDb(getIntent().getStringExtra(BATCH_ID));
    }
}