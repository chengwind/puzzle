package com.example.puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

public class MainActivity extends Activity {

	List<Button> buttons = null;

	private int ROWS = 4, MAX_BUTTONS = 16;

	private List<String> RANDS = new ArrayList<String>();

	private List<String> INIT_LIST = new ArrayList<String>() {
		{
			add("1");
			add("2");
			add("3");
			add("4");
			add("5");
			add("6");
			add("7");
			add("8");
			add("9");
			add("10");
			add("11");
			add("12");
			add("13");
			add("14");
			add("15");
		}
	};

	private long startTime, endTime;

	private Button btnReset, btnStart;
	private EditText txtStep, txtSecond;

	private String PUZZLE_KEY = "";

	private int steps;

	private Timer mTimer = null;
	private TimerTask mTimerTask = null;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
		LinearLayout linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);
		LinearLayout linearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);
		btnReset = (Button) findViewById(R.id.btnReset);
		btnStart = (Button) findViewById(R.id.btnStart);
		txtStep = (EditText) findViewById(R.id.txtStep);
		txtSecond = (EditText) findViewById(R.id.txtSecond);

		btnReset.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resetButtons();
				startTime = 0;
				endTime = 0;
				steps = 0;
				resetStatus();
				stopTimer();
			}
		});
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startTime = System.currentTimeMillis();
				endTime = startTime;
				steps = 0;
				startTimer();
			}
		});

		resetButtons();

		for (int i = 0; i < MAX_BUTTONS; i++) {
			Button button = buttons.get(i);
			if (i < 4) {
				linearLayout1.addView(button);
			} else if (i < 8) {
				linearLayout2.addView(button);
			} else if (i < 12) {
				linearLayout3.addView(button);
			} else {
				linearLayout4.addView(button);
			}
		}

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:
					endTime = System.currentTimeMillis();
					resetStatus();
					break;
				default:
					break;
				}
			}
		};

	}

	private void startTimer() {
		stopTimer();
		if (mTimer == null) {
			mTimer = new Timer();
		}

		if (mTimerTask == null) {
			mTimerTask = new TimerTask() {
				@Override
				public void run() {
					Message message = Message.obtain(handler, 0);
					handler.sendMessage(message);
					// try {
					// Thread.sleep(1000);
					// } catch (InterruptedException e) {
					// }

				}
			};
		}

		mTimer.schedule(mTimerTask, 1000, 1000);
	}

	private void stopTimer() {

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}

	}

	private void resetRands() {
		RANDS.clear();
		if (INIT_LIST.size() != 15) {
			INIT_LIST = new ArrayList<String>() {
				{
					add("1");
					add("2");
					add("3");
					add("4");
					add("5");
					add("6");
					add("7");
					add("8");
					add("9");
					add("10");
					add("11");
					add("12");
					add("13");
					add("14");
					add("15");
				}
			};
		}
		Random random = new Random();
		int one = -1;
		for (int i = 0; i < MAX_BUTTONS - 1; i++) {
			int current = MAX_BUTTONS - i - 1;
			if (current > 0) {
				one = random.nextInt(current);
				RANDS.add(INIT_LIST.get(one));
				INIT_LIST.remove(one);
			} else {
				RANDS.add(INIT_LIST.get(0));
			}
		}
	}

	private void resetStatus() {
		long s = (endTime - startTime) / 1000;
		txtStep.setText("Steps : " + steps);
		txtSecond.setText("Seconds : " + s);
	}

	private void resetButtons() {
		resetRands();
		if (buttons != null && buttons.size() == MAX_BUTTONS) {
			for (int i = 0; i < MAX_BUTTONS; i++) {
				Button button = buttons.get(i);
				if (i == MAX_BUTTONS - 1) {
					button.setText(PUZZLE_KEY);
				} else {
					button.setText(RANDS.get(i));
				}
			}
		} else {
			buttons = new ArrayList<Button>();
			for (int i = 0; i < MAX_BUTTONS; i++) {
				Button button = new Button(this);
				if (i == MAX_BUTTONS - 1) {
					button.setText(PUZZLE_KEY);
				} else {
					button.setText(RANDS.get(i));
				}
				button.setLayoutParams(new LinearLayout.LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 1f));
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Button button = (Button) v;
						if (startTime == 0) {
							startTime = System.currentTimeMillis();
						}
						endTime = System.currentTimeMillis();
						boolean moved = moveIt(button);
						if (moved) {
							steps++;
							Message message = Message.obtain(handler, 0);
							handler.sendMessage(message);
							if (checkSuccess()) {
								Toast.makeText(MainActivity.this, "Success!!!",
										Toast.LENGTH_SHORT);
							}
						}

					}
				});
				buttons.add(button);
			}
		}
	}

	private boolean checkSuccess() {
		for (int i = 0; i < MAX_BUTTONS; i++) {
			Button button = buttons.get(i);
			if (i == MAX_BUTTONS - 1) {
				if (!PUZZLE_KEY.equals(button.getText().toString())) {
					return false;
				}
			} else {
				if (!String.valueOf(i + 1).equals(button.getText().toString())) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean moveIt(Button button) {
		int currentIdx = buttons.indexOf(button);
		if (PUZZLE_KEY.equals(button.getText().toString())) {
			return false;
		}
		if ((currentIdx + 1) % ROWS > 0) {
			int possibleIdx = currentIdx + 1;
			Button pb = buttons.get(possibleIdx);
			if (PUZZLE_KEY.equals(pb.getText())) {
				String tmp = pb.getText().toString();
				pb.setText(button.getText());
				button.setText(tmp);
				return true;
			}
		}

		if (currentIdx % ROWS > 0 && currentIdx >= 0) {
			int possibleIdx = currentIdx - 1;
			Button pb = buttons.get(possibleIdx);
			if (PUZZLE_KEY.equals(pb.getText())) {
				String tmp = pb.getText().toString();
				pb.setText(button.getText());
				button.setText(tmp);
				return true;
			}
		}

		if (currentIdx < MAX_BUTTONS - 4) {
			int possibleIdx = currentIdx + 4;
			Button pb = buttons.get(possibleIdx);
			if (PUZZLE_KEY.equals(pb.getText())) {
				String tmp = pb.getText().toString();
				pb.setText(button.getText());
				button.setText(tmp);
				return true;
			}
		}

		if (currentIdx >= 4) {
			int possibleIdx = currentIdx - 4;
			Button pb = buttons.get(possibleIdx);
			if (PUZZLE_KEY.equals(pb.getText())) {
				String tmp = pb.getText().toString();
				pb.setText(button.getText());
				button.setText(tmp);
				return true;
			}
		}

		return false;
	}
}
