package il.ac.huji.todolist;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import il.ac.huji.todolist.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class TodoListManagerActivity extends Activity{

	private ListView tasksList;
	private CustomAdapter tasksAdapter;
	private ArrayList<ListObject> list = new ArrayList<ListObject>();
	final Context dialogContext = this;
//	int currentDialogPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);
		tasksList = (ListView)findViewById(R.id.lstTodoItems);
		tasksAdapter = new CustomAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,list);
		tasksList.setAdapter(tasksAdapter);
		registerForContextMenu (tasksList);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo_list_manager, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case R.id.menuItemAdd:
			Intent startAddItemActvt = new Intent(this, AddNewTodoItemActivity.class);
			startActivityForResult(startAddItemActvt,0);
		}
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	    menu.setHeaderTitle(list.get(info.position).getTask());
	    String task = list.get(info.position).getTask();
	    CharSequence taskS = (task.subSequence(0, 5));
	    if(taskS.equals("Call ")){
	    	inflater.inflate(R.menu.dialog_call_menu, menu);
	    	menu.findItem(R.id.menuItemCall).setTitle(task);
	    }
	    else{
	    	inflater.inflate(R.menu.dialog_menu, menu);
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menuItemCall:
	        	Intent intent = new Intent(Intent.ACTION_DIAL);
	        	String number = item.getTitle().subSequence(5, item.getTitle().length()).toString();
	        	intent.setData(Uri.parse("tel:"+number));
	        	startActivity(intent); 
	        	return true;
	        case R.id.menuItemDelete:
	        	list.remove(info.position);
				tasksAdapter.notifyDataSetChanged();
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	@Override
	public void onActivityResult(int reqCode, int resCode, Intent data)
	{
	    Bundle extras = data.getExtras();
	    if (extras != null) {
	        String newTask = extras.getString("title");
	        Date newDate = new Date(extras.getLong("dueDate"));
	        if(!newTask.isEmpty() && newTask!=null){
				list.add(new ListObject(newTask,newDate));
				tasksAdapter.notifyDataSetChanged();
			}
	    }	
	  } 
	
	class ListObject{
		String _task;
		Date _date;
		
		public ListObject(String task, Date date){
			_task = task;
			_date = date;
		}
		
		public String getTask(){
			return _task;
		}
		
		public Date getDate(){
			return _date;
		}
		
		@SuppressLint("SimpleDateFormat")
		public String getDateString(){
			SimpleDateFormat df = new SimpleDateFormat("d/M/yyyy");
	        return df.format(_date);
		}
	}
	
	class CustomAdapter extends ArrayAdapter<ListObject>{

		public CustomAdapter(Context context, int resource, List<ListObject> items) {
			super(context, resource, items);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View task = convertView;
			if(task == null){
				LayoutInflater inflater = getLayoutInflater();
				task = inflater.inflate(R.layout.simple_row, null);
			}
			((TextView)task.findViewById(R.id.txtTodoTitle)).setText(list.get(position).getTask());
			((TextView)task.findViewById(R.id.txtTodoDueDate)).setText(list.get(position).getDateString());

				if( dateHasPassed(list.get(position).getDate()) ){
					((TextView)task.findViewById(R.id.txtTodoTitle)).setTextColor(Color.RED);
					((TextView)task.findViewById(R.id.txtTodoDueDate)).setTextColor(Color.RED);
				}
				else{
					((TextView)task.findViewById(R.id.txtTodoTitle)).setTextColor(Color.BLACK);
					((TextView)task.findViewById(R.id.txtTodoDueDate)).setTextColor(Color.BLACK);
				}
			
			return task;
		}
		
		private boolean dateHasPassed(Date date) {
			Date today = new Date();
			if(date.before(today) || date.equals(today)){
				return true;
			}
			return false;
		}
	}
}
