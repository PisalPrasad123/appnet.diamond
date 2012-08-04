package appnet.diamond;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class UpdateBlocksInRow {
	private Queue<Integer> colorQueue;
	private  static int maxColor ;
	
	public UpdateBlocksInRow(){
		colorQueue = new LinkedList<Integer>();
	}
	
	public void setMaxColor(int _maxColor) {
		maxColor = _maxColor;
	}
	
	public void addColor(int color){
		colorQueue.add(color);
	}
	
	public int pollColor(){
		return colorQueue.poll(); 
	}
	
	public void resetQueue(){
		if (colorQueue.isEmpty() == false)
			colorQueue.clear();
	}
	
	public void additionalColor(){
		if (colorQueue.size() < maxColor){
			Random random = new Random();
			while (colorQueue.size() <= maxColor)
				colorQueue.add(randomInteger(0, 6, random));
		}
	}
	
	//create a random number between "start" to "end"
	private static int randomInteger(int start, int end, Random random) {
		int range = end - start + 1;
		int fraction = (int) (range * random.nextDouble());
		int randomNumber = fraction + start;
		return randomNumber;
	}
}
