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
import com.vdom.core.Event;
import com.vdom.comms.MyCard;

public class EventView extends FrameLayout implements OnLongClickListener{

	private TextView name;
	private View cardBox;
	private TextView cost, countLeft, embargos;
	private TextView checked;
	public Event event;
	public MyCard mycard;
	public boolean isChecked = false;

	public EventView(Context context, Event event) {
		super(context);
		this.event = event;
		try {
			Resources r = context.getResources();
			int id = r.getIdentifier(event.name + "_name", "string", context.getPackageName());
			event.displayName = r.getString(id);
			id = r.getIdentifier(event.name + "_desc", "string", context.getPackageName());
			event.description = r.getString(id);
		} catch(Exception e) {
			e.printStackTrace();
		}
		LayoutInflater.from(context).inflate(R.layout.view_card_classic, this, true);
		name = (TextView) findViewById(R.id.name);
		cardBox = findViewById(R.id.cardBox);
		cost = (TextView) findViewById(R.id.cost);
		countLeft = (TextView) findViewById(R.id.countLeft);
		embargos = (TextView) findViewById(R.id.embargos);
		checked = (TextView) findViewById(R.id.checked);

		TypedArray cardStyle = getContext().obtainStyledAttributes(R.style.CardView_Event,
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
		countLeft.setTextColor(countColor);
		setBackgroundResource(R.drawable.cardborder);
		FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.TOP + Gravity.CENTER_HORIZONTAL);
		name.setLayoutParams(p);

		name.setText(event.displayName, TextView.BufferType.SPANNABLE);
		cost.setText(" "+event.cost+" ");
		cost.setVisibility(VISIBLE);
		cardBox.setVisibility(VISIBLE);
		countLeft.setVisibility(INVISIBLE);
		embargos.setVisibility(INVISIBLE);
		checked.setVisibility(INVISIBLE);

	}

	public void setChecked(boolean b) {
		isChecked = b;
		if (isChecked)
			checked.setVisibility(VISIBLE);
		else
			checked.setVisibility(INVISIBLE);
	}

	public boolean onLongClick(View view) {
		HapticFeedback.vibrate(getContext(),AlertType.LONGCLICK);
		TextView textView = new TextView(view.getContext());
		textView.setPadding(15, 0, 15, 5);
		String text = "Event ("+event.expansion+")\n"+event.description;
		textView.setText(text);
		new AlertDialog.Builder(view.getContext())
			.setTitle(event.displayName)
			.setView(textView)
			.setPositiveButton(android.R.string.ok, null)
			.show();
		return true;
	}
}
