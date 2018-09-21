package com.easyreader.utils;

import com.easyreader.bean.AuthorInfo;
import com.easyreader.database.bean.Writer;

import java.util.Comparator;

public class PinyinComparator implements Comparator<Writer> {

	public int compare(Writer o1, Writer o2) {
		if (o1.firstLetter.equals("@")
				|| o2.firstLetter.equals("#")) {
			return 1;
		} else if (o1.firstLetter.equals("#")
				|| o2.firstLetter.equals("@")) {
			return -1;
		} else {
			return o1.firstLetter.compareTo(o2.firstLetter);
		}
	}

}
