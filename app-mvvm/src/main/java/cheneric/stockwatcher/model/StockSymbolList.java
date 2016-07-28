package cheneric.stockwatcher.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import timber.log.Timber;

public class StockSymbolList extends ArrayList<String> {
	private static final String DATA_FILE = "stock_symbols.txt";

	public void load() throws IOException {
		final BufferedReader reader =
			new BufferedReader(
				new InputStreamReader(
					StockSymbolList.class.getClassLoader()
						.getResourceAsStream(DATA_FILE)));
		try {
			String line;
			while ((line = reader.readLine()) != null) {
				final String symbol = line.trim();
				if (!symbol.isEmpty()) {
					add(symbol);
				}
			}
			Timber.d("loaded %s stock symbols", size());
		}
		finally {
			try {
				reader.close();
			}
			catch (IOException exception) {
				Timber.w(exception, "error closing stock symbol list reader");
			}
		}
	}
}
