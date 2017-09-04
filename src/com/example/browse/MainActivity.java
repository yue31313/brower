package com.example.browse;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements DownloadListener{
	private WindowManager.LayoutParams lp;
	private AlertDialog menuDialog;
	private View menuView;
	private ProgressDialog dialog;
	private EditText editText;
	private GridView gridView,menuGrid;
	private WebView webView;
	private ImageView go,star;
	private WebSettings websetting;
	private ArrayList<String> list_url,list_name;
	private OpenHelper helper;
	private SQLiteDatabase database;
	private Cursor c;
	private int sum;
	private ContentValues values = null;
	
	/*-- MENU�˵�ѡ���±� --*/
	private final int ITEM_0 = 0;
	private final int ITEM_1 = 1;
	private final int ITEM_2 = 2;
	private final int ITEM_3 = 3;
	private final int ITEM_4 = 4;
	private final int ITEM_5 = 5;
	private final int ITEM_6 = 6;
	private final int ITEM_7 = 7;
	
	/** �˵�ͼƬ **/
	int[] menu_image_array = {R.drawable.menu_add_to_bookmark,R.drawable.menu_refresh,R.drawable.menu_day
			,R.drawable.menu_nightmode,R.drawable.menu_checknet,R.drawable.menu_about,R.drawable.menu_quit,R.drawable.menu_syssettings};
	
	/** �˵����� **/
	String[] menu_name_array = { "�����ǩ", "ˢ��ҳ��", "�ռ�ģʽ", "ҹ��ģʽ", "�������", "�й���Ϣ",
			 "�˳�����","ϵͳ����"};
	
	/*-- Toolbar�ײ��˵�ѡ���±�--*/
	private final int TOOLBAR_ITEM_PAGEHOME = 0;// ��ҳ
	private final int TOOLBAR_ITEM_BACK = 1;// �˺�
	private final int TOOLBAR_ITEM_FORWARD = 2;// ǰ��
	private final int TOOLBAR_ITEM_MENU = 3;// �˵�
	
	/** �ײ��˵�ͼƬ **/
	int[] menu_toolbar_image_array = { R.drawable.controlbar_homepage,
			R.drawable.controlbar_backward_enable,
			R.drawable.controlbar_forward_enable, R.drawable.controlbar_menu};
	
	/** �ײ��˵����� **/
	String[] menu_toolbar_name_array = { "��ҳ", "����", "ǰ��", "�˵�"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		lp = getWindow().getAttributes();
		helper = new OpenHelper(MainActivity.this, "mylist.db", null, 1);
		database = helper.getWritableDatabase();
		list_url = new ArrayList<String>();
		list_name = new ArrayList<String>();
		c = database.rawQuery("select * from list", null);
		sum = c.getCount();
		if(sum==0){
		//	Toast.makeText(this, "Ŀǰ��û����ǩ�����ղ���ϲ������վ�ɣ�", Toast.LENGTH_SHORT).show();
			Toast.makeText(this, ""+sum, Toast.LENGTH_SHORT).show();
		}
		else{
			c.moveToFirst();
			for(int i=0;i<sum;i++){
				list_url.add(c.getString(0));
				list_name.add(c.getString(1));
				c.moveToNext();
			}
		}
		editText = (EditText)this.findViewById(R.id.editText1);
		gridView = (GridView)this.findViewById(R.id.gridView1);
		webView = (WebView)this.findViewById(R.id.webView1);
		webView.setInitialScale(100);
		websetting = webView.getSettings();
		websetting.setJavaScriptEnabled(true);
		// ʵ�ַŴ����С
		websetting.setSupportZoom(true);
		websetting.setBuiltInZoomControls(true); //ʵ�ַŴ����С
		//ʵ��˫���Ŵ󣬷Ŵ����˫����С
		websetting.setUseWideViewPort(true);
		//ʵ���ļ����ع���
		webView.setDownloadListener(this);
		// �����Զ���menu�˵�
		menuView = View.inflate(this, R.layout.gridview_menu, null);
		// ����AlertDialog
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
		menuGrid.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if(keyCode==KeyEvent.KEYCODE_BACK ){
					menuDialog.dismiss();
				}
				return false;
			}
		});
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
	    	@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
	    		switch(arg2){
	    		case ITEM_0:
	    			list_url.add(webView.getUrl());
	    			list_name.add(webView.getTitle());
	    			values = new ContentValues();
	    			values.put("url", webView.getUrl());
	    			values.put("name", webView.getTitle());
	    			database.insert("list", null, values);
	    			Toast.makeText(MainActivity.this, "�����ǩ�ɹ�", Toast.LENGTH_SHORT).show();
	    			break;
	    		case ITEM_1:
	    			webView.loadUrl(webView.getUrl());
	    			break;
	    		case ITEM_2:  //�����ռ�ģʽ
	    			lp.screenBrightness = 1.0f;
					MainActivity.this.getWindow().setAttributes(lp); 
	    			break;
	    		case ITEM_3: //����ҹ��ģʽ
	    			lp.screenBrightness = 0.05f;
					MainActivity.this.getWindow().setAttributes(lp);
	    			break;
	    		case ITEM_4:
	    			ConnectivityManager cm = (ConnectivityManager)MainActivity.this.getSystemService(MainActivity.this.CONNECTIVITY_SERVICE);
	    			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    			if(networkInfo==null){
	    				Toast.makeText(MainActivity.this, "û�з�������", Toast.LENGTH_SHORT).show();
	    			}
	    			else{
	    				Toast.makeText(MainActivity.this, "��ǰ�������ͣ�"+networkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
	    			}
	    			break;
	    		case ITEM_5:
	    			new AlertDialog.Builder(MainActivity.this)
	    			.setTitle("��ӭ����")
	    			.setMessage("�������")
	    			.setPositiveButton("ȷ��", null).show();
	    			break;
	    		case ITEM_6:
	    			MainActivity.this.finish();
	    			break;
	    		case ITEM_7:
	    			 Intent intent =  new Intent(Settings.ACTION_SETTINGS);  
	    	         startActivity(intent);  
	    			break;
	    		}
	    		menuDialog.dismiss();
			}
		});
//		dialog = new ProgressDialog(this);
//		dialog.setTitle("��ܰ��ʾ");
//		dialog.setMessage("���ڼ��أ������ĵȴ�!");
		star = (ImageView)this.findViewById(R.id.imageView1);
		go = (ImageView)this.findViewById(R.id.imageView2);
//		webView.loadUrl("http://www.hao123.com");
		webView.loadUrl("http://www.baidu.com");
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
//				dialog.show();
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				MainActivity.this.setTitle(view.getTitle());
//				dialog.dismiss();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
				Toast.makeText(MainActivity.this,"��������ʧ�ܣ����������磡" , Toast.LENGTH_SHORT).show();
			}
			
			
		});
		
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
				MainActivity.this.setProgress(newProgress*100);
			}
		});
		
		webView.setOnKeyListener(new OnKeyListener() {		
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				 if (event.getAction() == KeyEvent.ACTION_DOWN) {    
	                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) { 
	                        webView.goBack();    
	                        return true;  //�����Ѿ�������  
	                    }
	                    else{
	                    	opendialog();
	                    	return true;
	                    }
	                }
				return false;
			}
		});
		
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus){
					editText.setText("http://");
					editText.setSelection(7);  //�ѽ����ƶ������һ����/������
				}
				else{
					editText.setText("");
				}
			}
		});
		
		star.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this,Listview_activity.class);
				i.putStringArrayListExtra("list", list_name);
				startActivityForResult(i, 1);
			}
		});
		
		go.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String text = editText.getText().toString().trim();
				if(!text.equals("")){
					if(text.startsWith("http://"))
					   webView.loadUrl(editText.getText().toString().trim());
					else  {
						//����������� �����ٶȣ���ʾ��ҳ�����
						//https://www.baidu.com/s?wd=android
					 webView.loadUrl("https://www.baidu.com/s?wd="+text);
					}
				}
			}
		});
		
		gridView.setBackgroundResource(R.drawable.channelgallery_bg);
		gridView.setNumColumns(4);
		gridView.setGravity(Gravity.CENTER);
		gridView.setVerticalSpacing(10);
		gridView.setHorizontalSpacing(10);
		gridView.setAdapter(getMenuAdapter(menu_toolbar_name_array,menu_toolbar_image_array));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case TOOLBAR_ITEM_PAGEHOME:
					webView.clearHistory();
					webView.loadUrl("http://www.baidu.com");
//					webView.loadUrl("http://www.hao123.com");
					break;
				case TOOLBAR_ITEM_BACK:
					if(webView.canGoBack()){
						webView.goBack();
					}
					else{
						Toast.makeText(MainActivity.this, "�����ٺ����ˣ�", Toast.LENGTH_SHORT).show();
					}
					break;
				case TOOLBAR_ITEM_FORWARD:
					if(webView.canGoForward()){
						webView.goForward();
					}
					else{
						Toast.makeText(MainActivity.this, "����ǰ���ޣ�", Toast.LENGTH_SHORT).show();
					}
					break;
				case TOOLBAR_ITEM_MENU:
					menuDialog.show();
					break;
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		SubMenu sub1 = (SubMenu)menu.addSubMenu(0, 10, 0, R.string.wangzhan);
		sub1.add(0, 100, 0, R.string.xinlang);
		sub1.add(0, 101, 0, R.string.tengxun);
		sub1.add(0, 102, 0, R.string.sohu);
		sub1.add(0, 103, 0, R.string.threeG);
		sub1.add(0, 104, 0, R.string.hupu);
		
		SubMenu sub2 = (SubMenu)menu.addSubMenu(0, 11, 0, R.string.sousuo);
		sub2.add(0, 105, 0, R.string.baidu);
		sub2.add(0, 106, 0, R.string.guge);
		sub2.add(0, 107, 0, R.string.soso);
		sub2.add(0, 108, 0, R.string.yicha);
		sub2.add(0, 109, 0, R.string.yahu);
		
		SubMenu sub3 = (SubMenu)menu.addSubMenu(0, 12, 0, R.string.gouwu);
		sub3.add(0, 110, 0, R.string.taobao);
		sub3.add(0, 111, 0, R.string.jingdong);
		sub3.add(0, 112, 0, R.string.suning);
		sub3.add(0, 113, 0, R.string.yixun);
		sub3.add(0, 114, 0, R.string.yamaxun);
		
		SubMenu sub4 = (SubMenu)menu.addSubMenu(0, 13, 0, R.string.service);
		sub4.add(0, 115, 0, R.string.wuba);
		sub4.add(0, 116, 0, R.string.ganji);
		sub4.add(0, 117, 0, R.string.baixing);
		sub4.add(0, 118, 0, R.string.dazhong);
		
		SubMenu sub5 = (SubMenu)menu.addSubMenu(0, 14, 0, R.string.guanwang);
		sub5.add(0, 119, 0, R.string.sanxing);
		sub5.add(0, 120, 0, R.string.huawei);
		sub5.add(0, 121, 0, R.string.xiaomi);
		sub5.add(0, 122, 0, R.string.zhongxing);
		sub5.add(0, 123, 0, R.string.htc);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		case 100:
			webView.loadUrl("http://www.sina.com");
			break;
		case 101:
			webView.loadUrl("http://info.3g.qq.com");
			break;
		case 102:
			webView.loadUrl("http://sohu.com");
			break;
		case 103:
			webView.loadUrl("http://xuan.3g.cn");
			break;
		case 104:
			webView.loadUrl("http://m.hupu.com");
			break;
		case 105:
			webView.loadUrl("http://m.baidu.com");
			break;
		case 106:
			webView.loadUrl("http://www.google.com");
			break;
		case 107:
			webView.loadUrl("http://www.soso.com");
			break;
		case 108:
			webView.loadUrl("http://page.yicha.cn");
			break;
		case 109:
			webView.loadUrl("http://www.yahoo.com");
			break;
		case 110:
			webView.loadUrl("http://www.taobao.com");
			break;
		case 111:
			webView.loadUrl("http://m.jd.com");
			break;
		case 112:
			webView.loadUrl("http://www.suning.com");
			break;
		case 113:
			webView.loadUrl("http://m.51buy.com");
			break;
		case 114:
			webView.loadUrl("http://www.amazon.cn");
			break;
		case 115:
			webView.loadUrl("http://m.58.com");
			break;
		case 116:
			webView.loadUrl("http://www.ganji.com");
			break;
		case 117:
			webView.loadUrl("http://www.baixing.com");
			break;
		case 118:
			webView.loadUrl("http://wap.dianping.com");
			break;
		case 119:
			webView.loadUrl("http://www.samsung.com");
			break;
		case 120:
			webView.loadUrl("http://www.huawei.com");
			break;
		case 121:
			webView.loadUrl("http://www.xiaomi.com");
			break;
		case 122:
			webView.loadUrl("http://www.nubia.cn");
			break;
		case 123:
			webView.loadUrl("http://www.htc.com");
			break;
		default:
			return false;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
//		if (event.getAction() == KeyEvent.ACTION_DOWN) { 
//			opendialog();
//		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void opendialog(){
		new AlertDialog.Builder(this)   
		.setTitle("��ʾ")  
		.setMessage("ȷ��Ҫ�˳���")  
		.setPositiveButton("��", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
					MainActivity.this.finish();
			}
		})  
		.setNegativeButton("��", null)  
		.show();
	}

	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1&&resultCode==2){
			int onclick = data.getIntExtra("onclick", 0);
			webView.loadUrl(list_url.get(onclick));
		}
		if(requestCode==1&&resultCode==3){
			int onlongclick = data.getIntExtra("onlongclick", 0);
			list_url.remove(onlongclick);
			list_name.remove(onlongclick);
			int ok=database.delete("list", "name"+"="+"'"+Listview_activity.adapter.getItem(onlongclick)+"'", null);
			if(ok>=0){
				Toast.makeText(MainActivity.this,"��ɾ����ǩ", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		c.close();
		helper.close();
		database.close();
	}

	@Override
	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		// TODO Auto-generated method stub
		Uri uri = Uri.parse(url);
		Intent i = new Intent(Intent.ACTION_VIEW,uri);
		startActivity(i);
	}

}
