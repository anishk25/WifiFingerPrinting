package com.example.rssirecorder;

import java.util.Comparator;

import android.net.wifi.ScanResult;

public class WifiStrengthComparator implements Comparator<ScanResult> {

	@Override
	public int compare(ScanResult lhs, ScanResult rhs) {
		return rhs.level-lhs.level;
	}

}
