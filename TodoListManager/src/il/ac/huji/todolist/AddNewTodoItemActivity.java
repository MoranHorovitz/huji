package il.ac.huji.todolist;

import java.util.Calendar;

import il.ac.huji.todolist.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class AddNewTodoItemActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.add_item_dialog);
		
		final Button cancel_button = (Button) findViewById(R.id.btnCancel);
		cancel_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
    
		final Button ok_button = (Button) findViewById(R.id.btnOK);
		ok_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				EditText newItem = (EditText) findViewById(R.id.edtNewItem);
				DatePicker dp = (DatePicker) findViewById(R.id.datePicker);
				if(newItem.getText()==null || newItem.getText().toString().isEmpty()){
					Toast toast = Toast.makeText(getApplicationContext(), "please enter task title" , Toast.LENGTH_SHORT);
					toast.show();
					return;
				}

				Calendar calendar = Calendar.getInstance();
			    calendar.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
			    long dueDate = calendar.getTime().getTime();
						
			    Intent intent = new Intent(AddNewTodoItemActivity.this, TodoListManagerActivity.class);
				intent.putExtra("title", newItem.getText().toString());
				intent.putExtra("dueDate", dueDate);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
}
