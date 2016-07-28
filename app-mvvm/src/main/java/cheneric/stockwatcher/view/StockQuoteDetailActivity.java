package cheneric.stockwatcher.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cheneric.stockwatcher.R;
import cheneric.stockwatcher.StockWatcherApplication;
import cheneric.stockwatcher.inject.ComponentManager;
import cheneric.stockwatcher.inject.StockQuoteDetailComponent;
import timber.log.Timber;

public class StockQuoteDetailActivity extends AppCompatActivity {
	static final String START_INDEX_EXTRA_KEY = "START_INDEX";
	static final String END_INDEX_EXTRA_KEY = "END_INDEX";

	private ComponentManager componentManager;

	@Inject
	StockQuoteDetailPagerAdapterFactory stockQuoteDetailPagerAdapterFactory;

	@BindView(R.id.view_pager)
	ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stock_quote_detail_activity);

		// inject
		componentManager = ((StockWatcherApplication)getApplicationContext()).getComponentManager();
		componentManager.getOrCreateStockDetailComponent()
			.inject(this);

		// bind
		ButterKnife.bind(this);

		// intent extras
		final int startIndex = getIntent().getIntExtra(START_INDEX_EXTRA_KEY, -1);
		Timber.d("stock quote detail activity start index: %s", startIndex);
		viewPager.setAdapter(stockQuoteDetailPagerAdapterFactory.create(getSupportFragmentManager()));
		viewPager.setCurrentItem(startIndex);
	}

	@Override
	public void onBackPressed() {
		final int itemIndex = viewPager.getCurrentItem();
		Timber.d("stock quote detail activity end index: %s", itemIndex);
		final Intent intent = new Intent();
		intent.putExtra(END_INDEX_EXTRA_KEY, itemIndex);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		componentManager.destroyComponent(StockQuoteDetailComponent.class);
	}
}
