package com.effective.apackage.packageeffective;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.effective.apackage.packageeffective.domain.ItemValue;
import com.effective.apackage.packageeffective.domain.PackageValue;
import com.effective.apackage.packageeffective.domain.SortType;
import com.effective.apackage.packageeffective.utilities.ExpandAdapter;
//import com.effective.apackage.packageeffective.utilities.NetworkUtils;
import com.effective.apackage.packageeffective.utilities.packageJsonUtil;

import org.json.JSONException;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PackageListActivity extends AppCompatActivity {

    private TextView mPackageTextView;
    private ExpandableListView expandableListView;
    private ImageView mTopPanel;
    private ImageButton mEffecienctSortButton;
    private ImageButton mPriceSortButton;
    private EditText searchEditTextView;
    private List<PackageValue> packageValueList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_list);

        //mPackageTextView = (TextView) findViewById(R.id.tv_package_data);

        expandableListView = (ExpandableListView) findViewById(R.id.mylist);
        final Intent intentThatStartedThisActivity = getIntent();

        mTopPanel = findViewById(R.id.topPanel);
        mTopPanel.setImageResource(R.drawable.brown_dust_top);
        mEffecienctSortButton = findViewById(R.id.efficiencySortButton);
        mPriceSortButton = findViewById(R.id.priceSortButton);
        if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String gameName = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            if (gameName.equals("에픽세븐")) {
                mTopPanel.setImageResource(R.drawable.epic_seven_top);
            } else if (gameName.equals("브라운더스트")) {
                mTopPanel.setImageResource(R.drawable.brown_dust_top);
            } else if (gameName.equals("서머너즈워")) {
                mTopPanel.setImageResource(R.drawable.summoners_war_top);
            } else if (gameName.equals("블레이드앤소울")) {
                mTopPanel.setImageResource(R.drawable.blade_and_soul_top);
            }

            // mEffecienctSortButton.setBackgroundColor(Color.DKGRAY);
            try {
                loadPackageData(gameName);
                packageValueList = packageJsonUtil.sort(SortType.VALUE_DOWN);
                packageJsonUtil.initTopRateValues();
                ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), R.layout.package_alone, R.layout.components, packageValueList);
                expandableListView.setAdapter(adapter);
            } catch (JSONException e) {
                Log.d("PackageListActivity", e.getMessage());
            }
        }


        //TODO 함수로 따로 모듈화
        mPriceSortButton.setOnClickListener(new Button.OnClickListener() {
            int count = 0;
            String gameName = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);

            @Override
            public void onClick(View v) {
                count++;
                if (count % 2 == 1) {
                    mPriceSortButton.setImageResource(R.drawable.price_down);
                    mEffecienctSortButton.setImageResource(R.drawable.efficiency_inactivated);
                    try {
                        packageValueList = packageJsonUtil.sort(SortType.PRICE_DOWN);
                        ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), R.layout.package_alone, R.layout.components, packageValueList);
                        expandableListView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mPriceSortButton.setImageResource(R.drawable.price_up);
                    mEffecienctSortButton.setImageResource(R.drawable.efficiency_inactivated);
                    try {
                        packageValueList = packageJsonUtil.sort(SortType.PRICE_UP);
                        ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), R.layout.package_alone, R.layout.components, packageValueList);
                        expandableListView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        mEffecienctSortButton.setOnClickListener(new Button.OnClickListener() {
            String gameName = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
            int count = 0;

            @Override
            public void onClick(View v) {
                count++;
                if (count % 2 == 1) {
                    try {
                        mPriceSortButton.setImageResource(R.drawable.price_inactivate);
                        mEffecienctSortButton.setImageResource(R.drawable.efficiency_down);
                        packageValueList = packageJsonUtil.sort(SortType.VALUE_DOWN);
                        ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), R.layout.package_alone, R.layout.components, packageValueList);
                        expandableListView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        mPriceSortButton.setImageResource(R.drawable.price_inactivate);
                        mEffecienctSortButton.setImageResource(R.drawable.efficiency_up);
                        packageValueList = packageJsonUtil.sort(SortType.VALUE_UP);
                        ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), R.layout.package_alone, R.layout.components, packageValueList);
                        expandableListView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        searchEditTextView = (EditText) findViewById(R.id.searchText);

        searchEditTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            @TargetApi(24)
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Predicate<ItemValue> itemValuePredicate = i -> i.getItemName().contains(searchEditTextView.getText().toString());

                        List<PackageValue> filteredList =
                                packageValueList.stream().filter(t -> t.getPackageName().contains(searchEditTextView.getText().toString())
                                        || t.getItemValues().stream().anyMatch(itemValuePredicate)).collect(Collectors.toList());


                        Log.d("search", filteredList.toString());
                        ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), R.layout.package_alone, R.layout.components, filteredList);
                        expandableListView.setAdapter(adapter);
                        break;
                    default:

                        return false;
                }
                return true;
            }
        });

    }

    public void loadPackageData(String gameName) {
        try {
            packageValueList = packageJsonUtil.
                    getPackageStringsFromJson(PackageListActivity.this, gameName);
            ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(), R.layout.package_alone, R.layout.components, packageValueList);
            expandableListView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
