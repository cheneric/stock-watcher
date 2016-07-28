package cheneric.stockwatcher.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.ToString;


/*
 * {
 *   "query": {
 *     "count": 2,
 *     "created": "2016-07-24T01:37:48Z",
 *     "lang": "en-US",
 *     "results": {
 *       "quote": [
 *         {
 *           "Change": "+4.87",
 *           "LastTradePriceOnly": "759.28",
 *           "Name": "Alphabet Inc.",
 *           "Symbol": "GOOGL"
 *         },
 *         {
 *           "Change": "-0.77",
 *           "LastTradePriceOnly": "98.66",
 *           "Name": "Apple Inc.",
 *           "Symbol": "AAPL"
 *         }
 *       ]
 *     }
 *   }
 * }
 */
@Getter
@ToString
public class StockQuoteResult {
	private Query query;

	@Getter
	@ToString
	public static class Query {
		@SerializedName("results")
		private Result result;

		@Getter
		@ToString
		public static class Result {
			@SerializedName("quote")
			private List<StockQuote> stockQuotes;
		}
	}
}
