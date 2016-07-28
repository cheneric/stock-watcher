package cheneric.stockwatcher.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cheneric.stockwatcher.R;
import timber.log.Timber;

public class StockQuoteListActivity extends AppCompatActivity implements StockQuoteListFragment.ItemSelectedListener {
	private static final int QUOTE_DETAIL_REQUEST_CODE = 1;
	private StockQuoteListFragment stockQuoteListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stock_quote_list_activity);
		stockQuoteListFragment = (StockQuoteListFragment)getSupportFragmentManager().findFragmentById(R.id.list_fragment);
	}

	@Override
	public void onItemSelected(int itemIndex) {
		final Intent intent = new Intent(this, StockQuoteDetailActivity.class);
		intent.putExtra(StockQuoteDetailActivity.START_INDEX_EXTRA_KEY, itemIndex);
		startActivityForResult(intent, QUOTE_DETAIL_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
		if (resultIntent != null) {
			if (requestCode == QUOTE_DETAIL_REQUEST_CODE) {
				final int itemIndex = resultIntent.getIntExtra(StockQuoteDetailActivity.END_INDEX_EXTRA_KEY, 0);
				Timber.d("received stock quote detail end index: %s", itemIndex);
				stockQuoteListFragment.scrollToPosition(itemIndex);
			}
		}
	}
}
