package cheneric.stockwatcher.viewmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;

import javax.inject.Inject;

import cheneric.stockwatcher.R;
import cheneric.stockwatcher.StockWatcherApplication;
import cheneric.stockwatcher.inject.scope.ViewScope;
import cheneric.stockwatcher.model.StockSymbolList;
import cheneric.stockwatcher.view.StockQuoteListFragment;
import lombok.NoArgsConstructor;
import timber.log.Timber;

public class StockQuoteListViewModel {
	final StockQuoteListFragment stockQuoteListFragment;
	AlertDialog alertDialog;

	@Inject
	StockSymbolList stockSymbolList;

	public StockQuoteListViewModel(StockQuoteListFragment stockQuoteListFragment) {
		this.stockQuoteListFragment = stockQuoteListFragment;
		((StockWatcherApplication)stockQuoteListFragment.getContext()
			.getApplicationContext())
			.getComponentManager()
			.getOrCreateStockListComponent()
			.inject(this);
	}

	public void onAddClick(View view) {
		Timber.d("add stock symbol");
		AlertDialog alertDialog = this.alertDialog;
		if (alertDialog == null) {
			this.alertDialog = alertDialog = buildAlertDialog(view.getContext());
		}
		alertDialog.show();
	}

	AlertDialog buildAlertDialog(Context context) {
		Timber.v("building Add alert dialog");
		final Resources resources = context.getResources();
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final View alertDialogBody = LayoutInflater.from(context).inflate(R.layout.stock_quote_list_add_dialog, null);
		final EditText symbolEditText = (EditText)alertDialogBody.findViewById(R.id.stock_symbol);
		builder.setView(alertDialogBody);
		final AlertDialog alertDialog = builder.create();
		alertDialog.setTitle(resources.getText(R.string.quote_list_add_title));
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, resources.getText(R.string.quote_list_add_button_negative), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int buttonId) {
				alertDialog.cancel();
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, resources.getText(R.string.quote_list_add_button_positive), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int buttonId) {
				final String stockSymbol = symbolEditText.getText().toString().trim();
				if (stockSymbol.length() > 0) {
					stockSymbolList.add(stockSymbol);
					stockQuoteListFragment.scrollToPosition(stockSymbolList.size() - 1);
				}
				symbolEditText.setText("");
			}
		});
		return alertDialog;
	}
}
