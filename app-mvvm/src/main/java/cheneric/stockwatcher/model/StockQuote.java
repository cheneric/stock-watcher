package cheneric.stockwatcher.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/*
 * {
 *   "Change": "+4.87",
 *   "LastTradePriceOnly": "759.28",
 *   "Name": "Alphabet Inc.",
 *   "Symbol": "GOOGL"
 * }
 */
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of="symbol")
@ToString
public class StockQuote {

	@SerializedName("ChangeinPercent")
	private String changePercent;

	@SerializedName("Name")
	private String name;

	@SerializedName("LastTradePriceOnly")
	private String price;

	@NonNull
	@SerializedName("Symbol")
	private String symbol;

	private long timestamp = System.currentTimeMillis();

	public Boolean isPriceGain() {
		return changePercent == null ? null : changePercent.startsWith("+");
	}
}
