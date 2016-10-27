package org.gztech.heartrate.utils;

import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

/**
 * @author Kern:
 * @date 2016-4-25 pm7:53:56
 * @version 1.0
 * @parameter
 * @since
 * @return
 */
public class HeartRateManager {

	private MindsetManager mindsetManager;
	private static OnMindsetChangeListener mindsetChangeListener;
	private ArrayList<Integer> data = null;
	private int riseRate, fallRate;
	private int riseCount, fallCount;

	public HeartRateManager() {
		super();

		data = new ArrayList<Integer>();
		mindsetManager = new MindsetManager();

	}

	public void addElement(int rate) {

		if (data.size() < 300) {
			// 当长度小于300时，只添加不删除
			data.add(rate);
		} else if (data.size() >= 300) {
			// 当长度大于300时，边添加边删除
			data.remove(0);
			data.add(rate);
		}
		Log.e("sos", "data:" + data.size());
		judgeElement(data);
	}

	/*****
	 * 
	 * 判断条件
	 * 
	 **/
	public void judgeElement(ArrayList<Integer> data) {

		if (data.size() >= 10 && data.size() < 20) {

			int[] agrs = analyzeData(data, data.size());
			int mindset = mindsetManager.getMindset(agrs);
			String mindsetString = MindsetManager.getMindsetString(mindset);

			if (mindsetChangeListener != null) {
				mindsetChangeListener.onMindsetChange(agrs, mindsetString);
			}

		} else if (data.size() >= 20) {

			int[] agrs = analyzeData(data, data.size());
			int mindset = mindsetManager.getMindset(agrs);
			String mindsetString = MindsetManager.getMindsetString(mindset);

			if (mindsetChangeListener != null) {
				mindsetChangeListener.onMindsetChange(agrs, mindsetString);
			}
		}
	}

	public int[] analyzeData(ArrayList<Integer> sub_data, int size) {

		int[] agrs = new int[8];
		int range = 0;

		int max = Collections.max(sub_data);
		int min = Collections.min(sub_data);

		for (int i = 0; i < sub_data.size() - 1; i++) {

			range = sub_data.get(i + 1) - sub_data.get(i);

			if (range > 0) {

				riseCount += range;

				float d = sub_data.get(i + 1) - sub_data.get(i);
				float f = sub_data.get(i + 1);
				float rate = d / f;
				riseRate += (int) (rate * 100);

			} else if (range < 0) {

				fallCount += range;

				float d = sub_data.get(i) - sub_data.get(i + 1);
				float f = sub_data.get(i);
				float rate = d / f;
				fallRate += (int) (rate * 100);

			}
		}
		// Log.e("sos", "---riseRate总共：" + riseRate + ";fallRate总共:" +
		// fallRate);

		agrs[0] = max;
		agrs[1] = min;
		agrs[2] = riseCount;
		agrs[3] = fallCount;
		agrs[4] = riseRate;
		agrs[5] = fallRate;

		riseCount = 0;
		fallCount = 0;
		riseRate = 0;
		fallRate = 0;

		return agrs;
	}

	public void setOnMindsetChangeListener(
			OnMindsetChangeListener mindsetChangeListener) {

		this.mindsetChangeListener = mindsetChangeListener;
	}
}
