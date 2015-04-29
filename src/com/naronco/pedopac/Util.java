package com.naronco.pedopac;

import java.io.*;
import java.util.*;

public class Util {
	public static String loadResourceText(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					Shader.class.getResourceAsStream(filename)));
			String content = "", line;
			while ((line = reader.readLine()) != null) {
				content += line + "\n";
			}
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> loadResourceLines(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					Shader.class.getResourceAsStream(filename)));
			String line;
			List<String> lines = new ArrayList<String>();
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int[] toIntArray(List<Integer> ints)
	{
		int[] arr = new int[ints.size()];
		for(int i = 0; i < ints.size(); i++)
			arr[i] = ints.get(i).intValue();
		return arr;
	}
}
