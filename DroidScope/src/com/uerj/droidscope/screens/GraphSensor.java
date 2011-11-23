package com.uerj.droidscope.screens;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.uerj.droidscope.Main;
import com.uerj.droidscope.R;

public class GraphSensor extends Activity {
	
	private GraphView graphView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.graphsensor);
		LinearLayout layout = (LinearLayout) findViewById(R.id.layout);  
		layout.addView(drawGraphSensor(this));  
	}
		
	protected GraphView drawGraphSensor(Context context) {
		// draw sin curve  
		int num = 150;  
		GraphViewData[] data = new GraphViewData[num];  
		double v=0;  
		for (int i=0; i<num; i++) {  
		   v += 0.2;  
		   data[i] = new GraphViewData(i, Math.sin(v));  
		}  
		graphView = new LineGraphView(context
		      , "GraphSensor"  
		);  
		// add data  
		graphView.addSeries(new GraphViewSeries(data));  
		// set view port, start=2, size=40  
		graphView.setViewPort(2, 40);  
		graphView.setScrollable(true);  
		// optional - activate scaling / zooming  
		graphView.setScalable(true);  
		return graphView;	  
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, Main.class);
		startActivity(intent);
		finish();
	}
}
