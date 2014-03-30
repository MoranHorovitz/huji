package il.ac.huji.todolist;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import il.ac.huji.todolist.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class TodoListManagerActivity extends Activity{

	private ListView tasksListView;
	DBHelper helper;
	SQLiteDatabase db;
	Cursor cursor;
	CustomCursorAdapter ca;
	int smallestFreeCell = 0;
	Context onCreateContext;
	ParseObject taskParseObj;
	Map<Long,String> ids = new HashMap<Long, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo_list_manager);

		tasksListView = (ListView)findViewById(R.id.lstTodoItems);
		helper = new DBHelper( getApplicationContext());
		db = helper.getWritableDatabase();
		onCreateContext = getApplicationContext();

		cursor = db.query("todo", new String[]{"_id","title","due"}, null, null, null, null, null);

		String[] from = new String[]{"title","due"};
		int[] to = new int[]{R.id.txtTodoTitle,R.id.txtTodoDueDate};
		ca = new CustomCursorAdapter(onCreateContext, R.layout.simple_row, cursor, from, to);
		ca.setViewBinder(ca.new CustomViewBinder());
		tasksListView.setAdapter(ca);
		ca.notifyDataSetChanged();
		registerForContextMenu (tasksListView);
		
		Parse.initialize(getApplicationContext(), "HtjSvOtCY9F1NcJlCeKh3V2M46xXqJYuKbWJKHeY", "FFg7WGt3NgL82f73SXgwTQsn0VOJFiXjit3N5iP6");
		ParseUser.enableAutomaticUser();
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

		menu.setHeaderTitle(cursor.getString(1));
		String task = cursor.getString(1);
		if(task.length()>= 5){
			CharSequence taskS = (task.subSequence(0, 5));
			if(taskS.equals("Call ")){
				inflater.inflate(R.menu.dialog_call_menu, menu);
				menu.findItem(R.id.menuItemCall).setTitle(task);
				return;
			}
		}
		inflater.inflate(R.menu.dialog_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuItemCall:
			Intent intent = new Intent(Intent.ACTION_DIAL);
			String number = item.getTitle().subSequence(5, item.getTitle().length()).toString();
			intent.setData(Uri.parse("tel:"+number));
			startActivity(intent); 
			return true;
		case R.id.menuItemDelete:		
			long id = cursor.getInt(0);			
			db.delete("todo", "_id like ?", new String[]{String.valueOf(id)} );
			cursor = db.query("todo", new String[]{"_id","title","due"}, null, null, null, null, null);
			ca.swapCursor(cursor);
			ca.notifyDataSetChanged();
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("todo");
			query.getInBackground(ids.get(id), new GetCallback<ParseObject>() {
			  public void done(ParseObject object, ParseException e) {
			    if (e == null) {
			      object.deleteInBackground();
			      Toast toast = Toast.makeText(getApplicationContext(), "task deleted" , Toast.LENGTH_SHORT);
					toast.show();
			    } else {
			    	Toast toast = Toast.makeText(getApplicationContext(), "failed to remove task" , Toast.LENGTH_SHORT);
					toast.show();
			    }
			  }
			});
			
			ids.remove(id);
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
			long date = extras.getLong("dueDate");
			if(!newTask.isEmpty() && newTask!=null){
				ContentValues task = new ContentValues();
				task.put("title", newTask);
				task.put("due", date);
				long id = db.insert("todo", null, task);
	
				cursor = db.query("todo", new String[]{"_id", "title","due"}, null, null, null, null, null);
				ca.swapCursor(cursor);
				ca.notifyDataSetChanged();
				if(cursor ==  null || cursor.getCount() == -1 ){
					cursor.moveToFirst();
				}
				taskParseObj = new ParseObject("todo");
				taskParseObj.put("title", newTask);
				taskParseObj.put("due", date);
				taskParseObj.saveInBackground();
				String taskId = taskParseObj.getObjectId();
				ids.put(id, taskId);
			}
		}	
	} 

	public class DBHelper extends SQLiteOpenHelper { 
		public DBHelper(Context context) { 
			super(context, "todo_db", null, 1); 
		} 

		@Override
		public void onCreate(SQLiteDatabase db) { 
			db.execSQL( "create table todo ( " + 
					"_id integer primary key autoincrement, " + 
					"title text, " + 
					"due long );" ); 
		} 

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer){
		}
	}


	public class CustomCursorAdapter extends SimpleCursorAdapter {
		Context _context;
		Cursor _cursor;

		LayoutInflater _inflater;

		public CustomCursorAdapter(Context context, int layout, Cursor crs,
				String[] from, int[] to) {
			super(context, layout, crs, from, to, 0);
			_context = context;
			_cursor = crs;
			_inflater = LayoutInflater.from(context);
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View task;
			if (convertView == null) {
				task = newView(_context, cursor, parent);
			} else {
				task = convertView;
			}
			cursor.moveToPosition(position);
			bindView(task, _context, cursor);

			String date = cursor.getString(2);
			if(dateHasPassed(new Date(Long.valueOf(date)))){ 
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

		public class CustomViewBinder implements SimpleCursorAdapter.ViewBinder{
			@SuppressLint("SimpleDateFormat")
			@Override
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

				if(cursor != null && cursor.getCount()!=0){
					if(columnIndex == 1){
						((TextView)view.findViewById(R.id.txtTodoTitle)).setText(cursor.getString(columnIndex));
					}
					else if(columnIndex == 2){

						Date date = new Date(Long.valueOf(cursor.getString(columnIndex)));
						Format format = new SimpleDateFormat("dd/MM/yyyy");
						((TextView)view.findViewById(R.id.txtTodoDueDate)).setText(format.format(date).toString());
					}
					else{
						return false;
					}
				}
				return true;
			}
		}
	}
	
}
