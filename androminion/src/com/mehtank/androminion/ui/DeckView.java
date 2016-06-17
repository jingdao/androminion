package com.mehtank.androminion.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mehtank.androminion.R;

public class DeckView extends RelativeLayout {
	@SuppressWarnings("unused")
	private static final String TAG = "DeckView";
	
	private TextView name;
	private TextView pirates;
	private TextView victoryTokens;
	private TextView guildsCoinTokens;
	private TextView debtTokens;
	private TextView minusCoinToken;
	private TextView minusCardToken;
	private TextView journeyToken;
	private TextView counts;

	private boolean showCardCounts = true;

	public DeckView(Context context) {
		this(context, null);
	}

	public DeckView(Context context, AttributeSet attrs) {
		super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_deck, this, true);
		name = (TextView) findViewById(R.id.name);
		pirates = (TextView) findViewById(R.id.pirates);
		victoryTokens = (TextView) findViewById(R.id.victoryTokens);
		guildsCoinTokens = (TextView) findViewById(R.id.guildsCoinTokens);
		debtTokens = (TextView) findViewById(R.id.debtTokens);
		minusCoinToken = (TextView) findViewById(R.id.minusCoinToken);
		minusCardToken = (TextView) findViewById(R.id.minusCardToken);
		journeyToken = (TextView) findViewById(R.id.journeyToken);
		counts = (TextView) findViewById(R.id.counts);

        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hide_card_counts", false)) {
            showCardCounts = false;
            counts.setVisibility(INVISIBLE);
        }
	}

	public void set(String nameStr, int turns, int deckSize, int handSize, int numCards, int pt, int vt, int gct, int dt, boolean md, boolean mc, boolean jt, boolean highlight) {
		String txt = nameStr + getContext().getString(R.string.turn_header) + turns;
		name.setText(txt);
		if (highlight) {
//			name.setTextColor(Color.BLACK);
//			name.setBackgroundColor(Color.GRAY);
			name.setTypeface(Typeface.DEFAULT_BOLD);
		} else {
//			name.setTextColor(Color.WHITE);
//			name.setBackgroundColor(Color.TRANSPARENT);
			name.setTypeface(Typeface.DEFAULT);
		}

		pirates.setText(" " + pt + " ");
		if (pt != 0)
			pirates.setVisibility(VISIBLE);
		else
			pirates.setVisibility(INVISIBLE);

        victoryTokens.setText(" " + vt + " ");
        if (vt != 0)
            victoryTokens.setVisibility(VISIBLE);
        else
            victoryTokens.setVisibility(INVISIBLE);
        
        guildsCoinTokens.setText(" " + gct + " ");
        if (gct != 0)
            guildsCoinTokens.setVisibility(VISIBLE);
        else
            guildsCoinTokens.setVisibility(INVISIBLE);

        debtTokens.setText(" " + dt + " ");
        if (dt != 0)
            debtTokens.setVisibility(VISIBLE);
        else
            debtTokens.setVisibility(INVISIBLE);

		if (mc)
			minusCoinToken.setVisibility(VISIBLE);
		else
			minusCoinToken.setVisibility(INVISIBLE);
		if (md)
			minusCardToken.setVisibility(VISIBLE);
		else
			minusCardToken.setVisibility(INVISIBLE);
		if (jt)
			journeyToken.setVisibility(VISIBLE);
		else
			journeyToken.setVisibility(INVISIBLE);

        if(showCardCounts) {
    		String str = "{ \u2261 " + deckSize +
    					 "    \u261e " + handSize +
    					 "    \u03a3 " + numCards + " }";
    		counts.setText(str);
        }
	}
}
