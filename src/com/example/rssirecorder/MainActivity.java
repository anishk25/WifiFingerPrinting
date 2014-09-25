package com.example.rssirecorder;




import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import android.support.v7.app.ActionBarActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity implements OnClickListener {

	
	private static final int SCAN_LIMIT = 10;
	private int scanNumber = 0;;
	private Button startStopButton;
	private Button recordRSSIButton;
	private EditText etRecordNumber;
	
	private WifiManager mainWifi;
    private WifiReceiver receiverWifi;
    private List<ScanResult> wifiList; 
    
    private TreeMap<String, WifiAvg> wifiAverages;
    
    File resultsFile;
    FileOutputStream fileOutputStream;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgetsAndStructures();
    }

    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void initWidgetsAndStructures(){
    	startStopButton = (Button)findViewById(R.id.bRecordStart);
    	recordRSSIButton = (Button)findViewById(R.id.bGetRssi);
    	etRecordNumber = (EditText)findViewById(R.id.etRecordNumber);    	
    	startStopButton.setOnClickListener(this);
    	recordRSSIButton.setOnClickListener(this);
    	
    	mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	receiverWifi = new WifiReceiver(); 
        registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));        
        wifiAverages = new TreeMap<String, WifiAvg>();
        resultsFile = getSdCardFile();
    }
    
    private File getSdCardFile(){    	      	
    	File results= new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"avg_results.txt");
    	if(!results.exists()){
    		try {
				results.createNewFile();
			} catch (IOException e) {				
				e.printStackTrace();
			}
    	}    	
    	return results;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.bRecordStart:	
			recordStartInFile();
			break;
		case R.id.bGetRssi:
			beginScan();
			break;			
		}
		
	}
	
	private void beginScan(){
		wifiAverages.clear();
		for(int i = 0 ; i < SCAN_LIMIT; i++){
			mainWifi.startScan();
		}
		scanNumber++;
	}
	
	private void recordStartInFile(){
		scanNumber = 0;
		String nums = "Record Range:" + etRecordNumber.getText().toString()+ "\n";
		try {
			fileOutputStream = new FileOutputStream(resultsFile, true);
			
			fileOutputStream.write(nums.getBytes());
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {			
		} catch (IOException e) {			
		}	
		
	}
	
	private void writeAveragesToFile(){
		try {
			fileOutputStream = new FileOutputStream(resultsFile, true);
			StringBuilder wifiStr = new StringBuilder();
			wifiStr.append("Scan Number: " + scanNumber + "\n\n");
			for(String address: wifiAverages.keySet()){
				int avg = wifiAverages.get(address).giveAverageRSSI();
				wifiStr.append("Adress:" + address + " Average level:" + avg + "\n");
			}
			fileOutputStream.write(wifiStr.toString().getBytes());
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {			
		} catch (IOException e) {			
		}		
	}
	
	private class WifiReceiver extends BroadcastReceiver {    	
        public void onReceive(Context c, Intent intent) {        	
            wifiList = mainWifi.getScanResults(); 
            Collections.sort(wifiList, new WifiStrengthComparator());           
            for(ScanResult sr: wifiList){  
            	String address = sr.BSSID;            	
            	String baseAddress = address.substring(0,address.length()-3);            	
            	if(Constants.containsAddress(baseAddress)){
            		if(wifiAverages.containsKey(address)){
            			wifiAverages.get(address).addToTotal(sr.level);
            		}else{
            			wifiAverages.put(address, new WifiAvg(sr.level));
            		}
            	}
            }
            writeAveragesToFile();
        }        
    }
	
	
}
