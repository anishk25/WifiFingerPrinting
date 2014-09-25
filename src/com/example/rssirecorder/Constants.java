package com.example.rssirecorder;

import java.util.Arrays;

public class Constants {
	
	public static final String[] routerAdressBases =
		{
		"00:26:cb:d1:67",
		"00:26:cb:d1:c6",
		"00:26:cb:d1:c8",
		"00:26:cb:ab:d2",
		"00:26:cb:aa:fc",
		"00:25:84:86:d2",
		"00:27:0d:09:77",
		"d4:a0:2a:cd:c5",
		"00:26:cb:ab:00",
		"00:26:cb:ab:03",
		"00:26:cb:ab:07",
		"00:26:cb:d1:c3"
		};
	public static boolean containsAddress(String address){
		return Arrays.asList(routerAdressBases).contains(address);				
	}

}
