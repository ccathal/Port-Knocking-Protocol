package src;

import java.util.Comparator;

public class SingleKnockSort implements Comparator<SingleKnock>{

	@Override
	public int compare(SingleKnock s1, SingleKnock s2) {
		int x = 0;
		
		if(s1.getTime() < s1.getTime()) {
	        x =  -1;
	    } else if(s1.getTime() > s1.getTime()) {
	        x =  1;
	    } else if(s1.getTime() == s1.getTime()) {
	        x =  0;
	    }
	    return x;
	}

}
