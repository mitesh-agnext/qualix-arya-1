package com.specx.scan.ui.result.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnCloseListener;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.base.app.ui.base.BaseActivity;
import com.core.app.ui.custom.EmptyRecyclerView;
import com.core.app.util.AlertUtil;
import com.core.app.util.Util;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVWriter;
import com.specx.scan.R;
import com.specx.scan.R2;
import com.specx.scan.data.model.analysis.AnalysisItem;
import com.specx.scan.data.model.result.ResultItem;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import timber.log.Timber;

import static com.opencsv.ICSVParser.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.ICSVWriter.DEFAULT_LINE_END;

public class SearchResultActivity extends BaseActivity implements SearchResultView, OnQueryTextListener, OnCloseListener {

    private SearchResultController controller;
    private List<ResultItem> results = new ArrayList<>();

    @Inject
    SearchResultPresenter presenter;

    @BindView(R2.id.toolbar)
    Toolbar toolbar;

    @BindView(R2.id.emptyView)
    View emptyView;

    @BindView(R2.id.searchView)
    SearchView searchView;

    @BindView(R2.id.recyclerView)
    EmptyRecyclerView recyclerView;

    @BindView(R2.id.fab_share)
    FloatingActionButton fabShare;

    @OnClick(R2.id.fab_share)
    public void shareResults() {
        try {
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter, ',',
                    CSVWriter.NO_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
            DateTimeFormatter milliFormat = DateTimeFormatter.ofPattern("yyMMddHHmmss");
            DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
            String[] headers = {"S/N", "Date", "Sample ID", "Parameter tested", "Result", "Tested by", "Device ID"};
            csvWriter.writeNext(headers);
            for (ResultItem result : results) {
                String index = String.valueOf(results.indexOf(result) + 1);
                LocalDateTime date = LocalDateTime.parse(result.getDatetime(), milliFormat);
                String datetime = datetimeFormat.format(date);
                String sampleId = result.getSample().getId();
                String userId = result.getUserId();
                String serialNumber = result.getSerialNumber();
                List<AnalysisItem> analyses = result.getAnalyses();
                for (AnalysisItem analysis : analyses) {
                    String name = analysis.getName();
                    String value = analysis.getTotalAmount();
                    if (analyses.indexOf(analysis) == 0) {
                        csvWriter.writeNext(new String[]{index, datetime, sampleId, name, value, userId, serialNumber});
                    } else {
                        csvWriter.writeNext(new String[]{"", "", "", name, value, "", ""});
                    }
                }
            }
            csvWriter.close();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TEXT, stringWriter.toString());

            startActivity(Intent.createChooser(intent, "Share"));
        } catch (Exception e) {
            Timber.e(e);
            showMessage(e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            toolbar.setTitle(R.string.title_search);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);

        controller = new SearchResultController(this);
        recyclerView.setAdapter(controller.getAdapter());

        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.setIconified(false);
        searchView.requestFocus();

        presenter.setView(this);
    }

    @Override
    public void showEmptyView() {
        results.clear();
        fabShare.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String msg) {
        AlertUtil.showToast(this, msg);
    }

    @Override
    public void showList(List<ResultItem> results) {
        this.results = results;
        controller.setList(results);
        emptyView.setVisibility(View.GONE);
        fabShare.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        showMessage(getResources().getQuantityString(R.plurals.results_found_msg, results.size(), results.size()));
    }

    @Override
    public void onBatchIdClicked(String batchId) {
        Util.copyToClipboard(this, "Batch ID", batchId);
        AlertUtil.showToast(this, getString(R.string.batch_copied_msg));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Util.hideSoftKeyboard(searchView);
        if (query.length() == 0) {
            showMessage(getString(R.string.search_error_msg));
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query.length() > 0) {
            presenter.searchResult(query);
        }
        return true;
    }

    @Override
    public boolean onClose() {
        if (TextUtils.isEmpty(searchView.getQuery())) {
            finish();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        presenter.destroy();
    }
}