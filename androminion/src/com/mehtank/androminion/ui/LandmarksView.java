package com.mehtank.androminion.ui;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import com.mehtank.androminion.util.HapticFeedback;
import com.mehtank.androminion.util.HapticFeedback.AlertType;
import com.mehtank.androminion.R;
import com.vdom.core.Landmarks;

public class LandmarksView extends FrameLayout implements OnLongClickListener{

	private TextView name;
	private View cardBox;
	public Landmarks landmark;
	private TextView victoryTokens;
	private TextView checked;
	private TextView countLeft;

	public LandmarksView(Context context, Landmarks landmark) {
		super(context);
		this.landmark = landmark;
		try {
			Resources r = context.getResources();
			int id = r.getIdentifier(landmark.name + "_name", "string", context.getPackageName());
			landmark.displayName = r.getString(id);
			id = r.getIdentifier(landmark.name + "_desc", "string", context.getPackageName());
			landmark.description = r.getString(id);
		} catch(Exception e) {
			e.printStackTrace();
		}
		LayoutInflater.from(context).inflate(R.layout.view_card_classic, this, true);
		name = (TextView) findViewById(R.id.name);
		cardBox = findViewById(R.id.cardBox);
		victoryTokens = (TextView) findViewById(R.id.supplyVictoryTokens);
		checked = (TextView) findViewById(R.id.checked);
		countLeft = (TextView) findViewById(R.id.countLeft);

		TypedArray cardStyle = getContext().obtainStyledAttributes(R.style.CardView_Landmarks,
				new int[] {
					R.attr.cardBackgroundColor,
					R.attr.cardNameBackgroundColor,
					R.attr.cardTextColor,
					R.attr.cardCountColor });
		int bgColor = cardStyle.getColor(0, R.color.cardDefaultBackgroundColor);
		int textColor = cardStyle.getColor(2, R.color.cardDefaultTextColor);
        int nameBgColor = cardStyle.getColor(1, R.color.cardDefaultTextBackgroundColor);
		int countColor = cardStyle.getColor(3, R.color.cardDefaultTextColor);
		cardBox.setBackgroundColor(bgColor);
		name.setTextColor(textColor);
        name.setBackgroundColor(nameBgColor);
		setBackgroundResource(R.drawable.cardborder);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.TOP + Gravity.CENTER_HORIZONTAL);
		name.setLayoutParams(p);

		name.setText(landmark.displayName, TextView.BufferType.SPANNABLE);
		cardBox.setVisibility(VISIBLE);
		checked.setVisibility(INVISIBLE);
		countLeft.setVisibility(INVISIBLE);
	}

	public void setVictoryTokens(int s) {
		if (s != 0) {
			victoryTokens.setText(" "+s+" ");
			victoryTokens.setVisibility(VISIBLE);
		} else
			victoryTokens.setVisibility(GONE);
	}

	public boolean onLongClick(View view) {
		HapticFeedback.vibrate(getContext(),AlertType.LONGCLICK);
		TextView textView = new TextView(view.getContext());
		textView.setPadding(15, 0, 15, 5);
		String text = "Landmark ("+landmark.expansion+")\n"+landmark.description;
		textView.setText(text);
		new AlertDialog.Builder(view.getContext())
			.setTitle(landmark.displayName)
			.setView(textView)
			.setPositiveButton(android.R.string.ok, null)
			.show();
		return true;
	}
}
