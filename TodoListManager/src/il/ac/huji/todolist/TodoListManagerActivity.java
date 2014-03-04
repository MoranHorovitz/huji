package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.todolist.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class TodoListManagerActivity extends Activity implements OnItemLongClickListener{

	private ListView tasksList;
	private CustomAdapter tasksAdapter;
	private ArrayList<String> list = new ArrayList<String>();
	final Context dialogContext = this;
	int currentDialogPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		tasksList = (ListView)findViewById(R.id.lstTodoItems);
		tasksAdapter = new CustomAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,list);
		tasksList.setAdapter(tasksAdapter);
		tasksList.setOnItemLongClickListener(this);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list_manager, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.add:
			String newTask = ((EditText)findViewById(R.id.edtNewItem)).getText().toString();
			if(!newTask.isEmpty() && newTask!=null){
				list.add(newTask);
				((EditText)findViewById(R.id.edtNewItem)).setText("");
				tasksAdapter.notifyDataSetChanged();
			}
		}
		return false;
	}
	
	class CustomAdapter extends ArrayAdapter<String>{

		public CustomAdapter(Context context, int resource, List<String> items) {
			super(context, resource, items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View task = convertView;
			if(task == null){
				LayoutInflater inflater = getLayoutInflater();
				task = inflater.inflate(R.layout.simple_row, null);
			}
			((TextView)task.findViewById(R.id.taskTextView)).setText(list.get(position));
			if(position%2 == 0){
				((TextView)task.findViewById(R.id.taskTextView)).setTextColor(Color.RED);
			}
			else{
				((TextView)task.findViewById(R.id.taskTextView)).setTextColor(Color.BLUE);
			}
			return task;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, 
			int position, long id) {
		currentDialogPosition = position;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(dialogContext);
		alertDialogBuilder.setTitle(list.get(position));
		alertDialogBuilder
		.setNegativeButton("cancel",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			}
		})
		.setPositiveButton("delete",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				list.remove(currentDialogPosition);
				tasksAdapter.notifyDataSetChanged();
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();	
		return false;
	}


}
