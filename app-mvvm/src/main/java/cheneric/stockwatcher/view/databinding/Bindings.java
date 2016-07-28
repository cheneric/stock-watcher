package cheneric.stockwatcher.view.databinding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import cheneric.stockwatcher.R;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class Bindings {
	static final int NON_NEGATIVE_DISCRIMINATOR_COLOR = R.color.green;
	static final int NEGATIVE_DISCRIMINATOR_COLOR = R.color.red;

	@BindingAdapter("bind:backgroundTintDiscriminator")
	public static void setBackgroundTintByDiscriminator(@NonNull View view, Boolean discriminator) {
		ViewCompat.setBackgroundTintList(
			view,
			getDiscriminatorColorStateList(view, discriminator));
	}

	@BindingAdapter("bind:textColorDiscriminator")
	public static void setTextColorByDiscriminator(@NonNull TextView textView, Boolean discriminator) {
		textView.setTextColor(
			getDiscriminatorColorStateList(textView, discriminator));
	}

	@BindingAdapter({"android:text", "bind:textSpanColorDiscriminator"})
	public static void setHtmlTextColorByDiscriminator(@NonNull TextView textView, String formattedHtml, Boolean discriminator) {
		textView.setText(
			Html.fromHtml(
				String.format(
					formattedHtml,
					getDiscriminatorColor(textView, discriminator) & ~0xFF000000)
			)
		);
	}

	static int getDiscriminatorColor(@NonNull View view, Boolean discriminator) {
		return getDiscriminatorColor(view.getContext(), discriminator);
	}

	static int getDiscriminatorColor(@NonNull Context context, Boolean discriminator) {
		return ContextCompat.getColor(
			context,
			Boolean.FALSE.equals(discriminator) ?
				NEGATIVE_DISCRIMINATOR_COLOR :
				NON_NEGATIVE_DISCRIMINATOR_COLOR);
	}

	static ColorStateList getDiscriminatorColorStateList(@NonNull View view, Boolean discriminator) {
		return getDiscriminatorColorStateList(view.getContext(), discriminator);
	}

	static ColorStateList getDiscriminatorColorStateList(@NonNull Context context, Boolean discriminator) {
		return ColorStateList.valueOf(getDiscriminatorColor(context, discriminator));
	}
}
