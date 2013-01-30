package org.example.qberticus.quickactions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.Toast;

/**
 * Simple demo Activity
 * 
 * @author qberticus
 * 
 */
public class LikeQuickActionsDemo extends Activity {
	private Button likemenuButton;
	private Button likequickactionButton;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.demo_layout);

		this.likemenuButton = (Button) this.findViewById(R.id.likemenu);
		this.likemenuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DemoPopupWindow dw = new DemoPopupWindow(v);
				dw.showLikePopDownMenu();
			}
		});

		this.likequickactionButton = (Button) this.findViewById(R.id.likequickaction);
		this.likequickactionButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DemoPopupWindow dw = new DemoPopupWindow(v);
				dw.showLikeQuickAction(0, 30);
			}
		});

	}

	/**
	 * Extends {@link BetterPopupWindow}
	 * <p>
	 * Overrides onCreate to create the view and register the button listeners
	 * 
	 * @author qbert
	 * 
	 */
	private static class DemoPopupWindow extends BetterPopupWindow implements OnClickListener {
		public DemoPopupWindow(View anchor) {
			super(anchor);
		}

		@Override
		protected void onCreate() {
			// inflate layout
			LayoutInflater inflater =
					(LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_grid_layout, null);

			// setup button events
			for(int i = 0, icount = root.getChildCount() ; i < icount ; i++) {
				View v = root.getChildAt(i);

				if(v instanceof TableRow) {
					TableRow row = (TableRow) v;

					for(int j = 0, jcount = row.getChildCount() ; j < jcount ; j++) {
						View item = row.getChildAt(j);
						if(item instanceof Button) {
							Button b = (Button) item;
							b.setOnClickListener(this);
						}
					}
				}
			}

			// set the inflated view as what we want to display
			this.setContentView(root);
		}

		@Override
		public void onClick(View v) {
			// we'll just display a simple toast on a button click
			Button b = (Button) v;
			Toast.makeText(this.anchor.getContext(), b.getText(), Toast.LENGTH_SHORT).show();
			this.dismiss();
		}
	}
}