package il.ac.huji.tipcalculator;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class TipCalculatorActivity extends Activity {

	final static private String BILL_AMOUNT = "BILL_AMOUNT";
	final static private String TIP_AMOUNT = "TIP_AMOUNT";
	final static private String TIP = "TIP";
	
	private double billAmount;
	private double tipAmount;
	private double tip;
	
	EditText billAmountET;
	TextView tipTV;
	CheckBox checkBox;
	SeekBar tipAmountSB;
	TextView tipAmountTV;

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        
        if(savedInstanceState == null){
        	billAmount = 0.0;
        	tipAmount = .12;
        	tip = 0.0;
        }
        else{
        	billAmountET = (EditText)findViewById(R.id.edtBillAmount);
        	tipTV = (TextView)findViewById(R.id.txtTipResult);
        }
        
        tipAmountSB = (SeekBar) findViewById(R.id.tipAmountSeekBar);
        tipAmountTV = (TextView) findViewById(R.id.sbProgressTextView);
        tipAmountSB.setProgress(Integer.parseInt(tipAmountTV.getText().toString()));
        tipAmountSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        	
        	@Override
        	public void onProgressChanged(SeekBar seekBar,int progresValue, boolean fromUser) {
        	}

        	@Override
        	public void onStartTrackingTouch(SeekBar seekBar) {
        	}

        	@Override
        	public void onStopTrackingTouch(SeekBar seekBar) {
        		int progress = seekBar.getProgress();
        		tipAmountTV.setText(String.valueOf(progress));
        		tipAmount = (double) (progress)/100.0;
        	}
        });
    
        
        final Button button = (Button) findViewById(R.id.btnCalculate);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	billAmountET = (EditText)findViewById(R.id.edtBillAmount);
            	tipTV = (TextView)findViewById(R.id.txtTipResult);
                checkBox = (CheckBox) findViewById(R.id.chkRound);

    			try{
    				billAmount = Double.parseDouble(billAmountET.getText().toString());			
    			}
    			catch(NumberFormatException e){
    				billAmount = 0.0;
    			}
    			if(checkBox.isChecked()){
    				tipTV.setText("$"+String.valueOf((int)Math.ceil(billAmount*tipAmount)));
    			}
    			else{
    				tipTV.setText("$"+String.format("%.2f", billAmount*tipAmount));
    			}
            }
        });
    }

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putDouble(BILL_AMOUNT, billAmount);
		outState.putDouble(TIP_AMOUNT, tipAmount);
		outState.putDouble(TIP, tip);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tip_calculator, menu);
        return true;
    }
}


