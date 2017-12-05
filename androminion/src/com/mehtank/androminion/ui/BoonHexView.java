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
import android.graphics.Typeface;
import com.vdom.core.Boons;
import com.vdom.core.Hexes;
import java.util.HashSet;
import java.util.HashMap;

public class BoonHexView extends FrameLayout implements OnLongClickListener{

	private TextView name;
	private View cardBox;
	private TextView checked;
	private TextView countLeft;
	private boolean isBoon;
	private String desc;
	private HashMap<String, String> nameMap = new HashMap<String, String>();
	private HashMap<String, String> descMap = new HashMap<String, String>();

	public BoonHexView(Context context, boolean isBoon) {
		super(context);
		this.isBoon = isBoon;
		LayoutInflater.from(context).inflate(R.layout.view_card_classic, this, true);
		name = (TextView) findViewById(R.id.name);
		cardBox = findViewById(R.id.cardBox);
		checked = (TextView) findViewById(R.id.checked);
		countLeft = (TextView) findViewById(R.id.countLeft);

		TypedArray cardStyle = getContext().obtainStyledAttributes(
				isBoon ? R.style.CardView_Boons : R.style.CardView_Hexes,
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

		name.setText(isBoon ? "Boons" : "Hexes", TextView.BufferType.SPANNABLE);
		cardBox.setVisibility(VISIBLE);
		checked.setVisibility(INVISIBLE);
		countLeft.setVisibility(VISIBLE);
		countLeft.setTextColor(countColor);

		if (isBoon) {
			for (Boons b : Boons.allBoons) {
				try {
					Resources r = context.getResources();
					int id = r.getIdentifier(b.name + "_name", "string", context.getPackageName());
					nameMap.put(b.name, r.getString(id));
					id = r.getIdentifier(b.name + "_desc", "string", context.getPackageName());
					descMap.put(b.name, r.getString(id));
					b.description = descMap.get(b.name);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			for (Boons b : Boons.setAsideBoons) {
				try {
					Resources r = context.getResources();
					int id = r.getIdentifier(b.name + "_name", "string", context.getPackageName());
					nameMap.put(b.name, r.getString(id));
					id = r.getIdentifier(b.name + "_desc", "string", context.getPackageName());
					descMap.put(b.name, r.getString(id));
					b.description = descMap.get(b.name);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			for (Hexes b : Hexes.allHexes) {
				try {
					Resources r = context.getResources();
					int id = r.getIdentifier(b.name + "_name", "string", context.getPackageName());
					nameMap.put(b.name, r.getString(id));
					id = r.getIdentifier(b.name + "_desc", "string", context.getPackageName());
					descMap.put(b.name, r.getString(id));
					b.description = descMap.get(b.name);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setCountLeft(int s) {
		countLeft.setText(" " + s + " ");
		countLeft.setVisibility(VISIBLE);
	}

	public void setRemaining(String[] boons, String[] hexes) {
		if (isBoon) {
			setCountLeft(boons.length);
			desc = "";
			HashSet<String> boonsName = new HashSet<String>();
			for (String s : boons)
				boonsName.add(s);
			for (Boons b : Boons.allBoons) {
				if (boonsName.contains(b.name)) {
					desc += String.format("%s: %s\n", nameMap.get(b.name), descMap.get(b.name));
				}
			}
		} else {
			setCountLeft(hexes.length);
			desc = "";
			HashSet<String> hexesName = new HashSet<String>();
			for (String s : hexes)
				hexesName.add(s);
			for (Hexes b : Hexes.allHexes) {
				if (hexesName.contains(b.name)) {
					desc += String.format("%s: %s\n", nameMap.get(b.name), descMap.get(b.name));
				}
			}
		}
	}

	public boolean onLongClick(View view) {
		HapticFeedback.vibrate(getContext(),AlertType.LONGCLICK);
		TextView textView = new TextView(view.getContext());
		textView.setPadding(5, 0, 5, 5);
//		textView.setTypeface(Typeface.MONOSPACE);
		textView.setText(desc);
		new AlertDialog.Builder(view.getContext())
			.setTitle(isBoon ? "Boons" : "Hexes")
			.setView(textView)
			.setPositiveButton(android.R.string.ok, null)
			.show();
		return true;
	}
}
