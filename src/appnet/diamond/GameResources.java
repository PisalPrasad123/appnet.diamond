package appnet.diamond;

import appnet.diamond.R;

public interface GameResources {
	Integer diamondIcon[] = {
		R.drawable.diamond_blue, R.drawable.diamond_green,
		R.drawable.diamond_orange, R.drawable.diamond_pink,
		R.drawable.diamond_red, R.drawable.diamond_white, 
		R.drawable.diamond_yellow, R.drawable.bomb,
		R.drawable.lightning
	};
	
	String diamondName[] = {
			"drawable-nodpi/diamond_blue.png", "drawable-nodpi/diamond_green.png" ,
			"drawable-nodpi/diamond_orange.png","drawable-nodpi/diamond_pink.png" ,
			"drawable-nodpi/diamond_red.png" , "drawable-nodpi/diamond_white.png",
			"drawable-nodpi/diamond_yellow.png"
	};
	
	Integer topDown[] = {
			R.anim.top_down_0 , R.anim.top_down_1 , R.anim.top_down_2 , R.anim.top_down_3,
			R.anim.top_down_4 , R.anim.top_down_5 ,	R.anim.top_down_6, 
			R.anim.top_down_7
	};
	
	String name[] = {
			"Blue", "Green" , "Orange", "Pink", "Red", "White", "Yellow"
	};
}
