package com.example.browse;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class Listview_activity extends Activity {
	private ListView listview;
	public static ArrayAdapter< String> adapter;
	private ArrayList<String> list;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		listview = (ListView)this.findViewById(R.id.listView1);
		list =this.getIntent().getStringArrayListExtra("list");
		adapter = new ArrayAdapter< String>(Listview_activity.this,android.R.layout.simple_list_item_1,list);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.putExtra("onclick", arg2);
				setResult(2, i);
				Listview_activity.this.finish();
			}
		});
		
		listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int arg2, long arg3) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(Listview_activity.this)
				.setTitle("WARM").setMessage("是否要删除本条书签").setPositiveButton("确定", new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent i = new Intent();
						i.putExtra("onlongclick", arg2);
						setResult(3, i);
						Listview_activity.this.finish();
					}
				}).setNegativeButton("取消", null).create().show();
				return true;
			}
		});
	}
	

}
