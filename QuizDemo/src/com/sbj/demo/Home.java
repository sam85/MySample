package com.sbj.demo;
// Created By Samit
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Home extends Activity {
	
	private final String TAG = Home.class.getSimpleName();
	TextView txtQuestion_FirstPart = null;
	TextView txtOperator = null;
	TextView txtQuestion_SecondPart = null;
	TextView txtAnswer = null;
	TextView txtOption1 = null;
	TextView txtOption2 = null;
	TextView txtOption3 = null;
	TextView txtOption4 = null;
	TextView txtQuestion_equal = null;
	TextView txtMessage = null;

	private int MinNumber = 0;
	private int MaxNumber = 50;
	private String[] Operator = { "ADD", "SUB", "MUL" };
	private HashMap<Integer,Integer> Options = new HashMap<Integer,Integer>();
	private ArrayList<Integer> OptionsPositions = new ArrayList<Integer>();

	String Addition = "ADD";
	String Substract = "SUB";
	String Multiply = "MUL";
	int CorrectAnsPosition = -1;
	Handler handler = new Handler();
	
	LinearLayout linQuestion = null;
	Animation anim_slide_in_up = null;
	Animation anim_slide_out_up = null;
	Animation anim_bounce_scale = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		init();
		SetQuestionAndOptions();
		
	}

	private void init() {
		Typeface font1 = Typeface.createFromAsset(getAssets(), "ASMAN.TTF");
		txtQuestion_FirstPart = (TextView) findViewById(R.id.txtQuestion_FirstPart);
		txtOperator = (TextView) findViewById(R.id.txtOperator);
		txtQuestion_SecondPart = (TextView) findViewById(R.id.txtQuestion_SecondPart);
		txtAnswer = (TextView) findViewById(R.id.txtAnswer);
		txtOption1 = (TextView) findViewById(R.id.txtOption1);
		txtOption2 = (TextView) findViewById(R.id.txtOption2);
		txtOption3 = (TextView) findViewById(R.id.txtOption3);
		txtOption4 = (TextView) findViewById(R.id.txtOption4);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		
		txtQuestion_equal = (TextView) findViewById(R.id.txtQuestion_equal);
		txtQuestion_equal.setText("=");
		

		txtQuestion_FirstPart.setTypeface(font1);
		// txtOperator.setTypeface(font1);
		txtQuestion_SecondPart.setTypeface(font1);
		txtAnswer.setTypeface(font1);
		txtOption1.setTypeface(font1);
		txtOption2.setTypeface(font1);
		txtOption3.setTypeface(font1);
		txtOption4.setTypeface(font1);
		txtMessage.setTypeface(font1);
		// txtQuestion_equal.setTypeface(font1);
		
		linQuestion = (LinearLayout)findViewById(R.id.linearLayout1);
		 anim_slide_in_up = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
		 anim_slide_out_up = AnimationUtils.loadAnimation(this, R.anim.slide_out_up);
		 anim_bounce_scale =  AnimationUtils.loadAnimation(this, R.anim.bounce_scale);

		txtOption1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckAnswer(1);
			}
		});
		txtOption2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckAnswer(2);
			}
		});
		txtOption3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckAnswer(3);
			}
		});
		txtOption4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CheckAnswer(4);
			}
		});
	}

	private void SetQuestionAndOptions() {
		
		txtMessage.setVisibility(View.INVISIBLE);
		txtAnswer.setVisibility(View.INVISIBLE);
		
		int Answer = 0;
		int Opt = GenerateRandom(MinNumber, Operator.length - 1);
		Log.e("Opt ", " : " + Opt);
		String operat = Operator[Opt];

		if (operat.equals(Addition)) {
			MaxNumber = 100;
			int FirstPart = GenerateRandom(5, MaxNumber);
			txtQuestion_FirstPart.setText(String.valueOf(FirstPart));

			int SecondPart = GenerateRandom(MinNumber, FirstPart);
			txtQuestion_SecondPart.setText(String.valueOf(SecondPart));

			txtOperator.setText("+");

			int First = Integer.parseInt(txtQuestion_FirstPart.getText()
					.toString().trim());
			int Last = Integer.parseInt(txtQuestion_SecondPart.getText()
					.toString().trim());

			Answer = First + Last;
		} else if (operat.equals(Substract)) {

			MaxNumber = 100;
			int FirstPart = GenerateRandom(5, MaxNumber);
			txtQuestion_FirstPart.setText(String.valueOf(FirstPart));

			int SecondPart = GenerateRandom(MinNumber, FirstPart);
			txtQuestion_SecondPart.setText(String.valueOf(SecondPart));

			txtOperator.setText("-");

			int First = Integer.parseInt(txtQuestion_FirstPart.getText()
					.toString().trim());
			int Last = Integer.parseInt(txtQuestion_SecondPart.getText()
					.toString().trim());

			Answer = First - Last;
		} else if (operat.equals(Multiply)) {

			MaxNumber = 30;
			int FirstPart = GenerateRandom(5, MaxNumber);
			txtQuestion_FirstPart.setText(String.valueOf(FirstPart));

			int SecondPart = GenerateRandom(MinNumber, FirstPart);
			txtQuestion_SecondPart.setText(String.valueOf(SecondPart));

			txtOperator.setText("x");

			int First = Integer.parseInt(txtQuestion_FirstPart.getText()
					.toString().trim());
			int Last = Integer.parseInt(txtQuestion_SecondPart.getText()
					.toString().trim());

			Answer = First * Last;
		}
		Options.clear();
		OptionsPositions.clear();
		
		linQuestion.startAnimation(anim_slide_in_up);
		
		SetOptions(Answer);
	
	}

	private void SetOptions(int Answer) {
		CorrectAnsPosition = GenerateRandom(0, 3);
		OptionsPositions.add(CorrectAnsPosition);
		Options.put(CorrectAnsPosition, Answer);
	
	
		while (Options.size() != 4) 
		{
			int OptAns = 0;
			if ((Answer - 10) > 0)
				OptAns = GenerateRandom(Answer - 10, Answer + 15);
			else
				OptAns = GenerateRandom(Answer, Answer + 25);

			if (!Options.containsValue(OptAns)) 
			{
				
				int Pos = GenerateRandom(0, 3);
				
					if (!OptionsPositions.contains(Pos)) {
						Options.put(Pos, OptAns);
						OptionsPositions.add(Pos);
					} 
				
			}
		}

		txtOption1.setText(String.valueOf(Options.get(0)));
		txtOption2.setText(String.valueOf(Options.get(1)));
		txtOption3.setText(String.valueOf(Options.get(2)));
		txtOption4.setText(String.valueOf(Options.get(3)));
		
		txtOption1.startAnimation(anim_slide_in_up);
		txtOption2.startAnimation(anim_slide_in_up);
		txtOption3.startAnimation(anim_slide_in_up);
		txtOption4.startAnimation(anim_slide_in_up);

	}

	private void CheckAnswer(int Click) {
		txtAnswer.setVisibility(View.VISIBLE);
		int RightAns = Options.get(CorrectAnsPosition);
		if (Click == 1) {
			txtAnswer.setText(txtOption1.getText().toString().trim());
			if (Integer.parseInt(txtOption1.getText().toString().trim()) == RightAns) {
				txtMessage.setText("Correct!!!");
			} else {
				txtMessage.setText("Wrong!!!");
			}
		} else if (Click == 2) {
			txtAnswer.setText(txtOption2.getText().toString().trim());
			if (Integer.parseInt(txtOption2.getText().toString().trim()) == RightAns) {
				txtMessage.setText("Correct!!!");
			} else {
				txtMessage.setText("Wrong!!!");
			}

		} else if (Click == 3) {
			txtAnswer.setText(txtOption3.getText().toString().trim());
			if (Integer.parseInt(txtOption3.getText().toString().trim()) == RightAns) {
				txtMessage.setText("Correct!!!");
			} else {
				txtMessage.setText("Wrong!!!");
			}
		} else if (Click == 4) {
			txtAnswer.setText(txtOption4.getText().toString().trim());
			if (Integer.parseInt(txtOption4.getText().toString().trim()) == RightAns) {
				txtMessage.setText("Correct!!!");
			} else {
				txtMessage.setText("Wrong!!!");
			}
			
		}
		
		
		txtMessage.setVisibility(View.VISIBLE);
		txtMessage.startAnimation(anim_bounce_scale);
	
		
		Thread thread = new Thread()
		{
		    @Override
		    public void run() {
		        try {		      
		            	sleep(3000);		        
		        		
		            	handler.post(new Runnable() { // This thread runs in the UI
		                    @Override
		                    public void run() {
		                    	SetQuestionAndOptions(); // Update the UI
		                    }
		                });    			
		           
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		};
		thread.start();
	
	}

	private int GenerateRandom(int Min, int Max) {
		Random r = new Random();
		int i1 = r.nextInt(Max - Min + 1) + Min;
		return i1;
	}

}