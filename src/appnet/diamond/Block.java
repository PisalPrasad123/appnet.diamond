package appnet.diamond;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class Block extends Button
{
	private int intColor;
	private boolean fade = false;
	private boolean down = false;
	private int numberOfFadeBlocks = -1;
	private boolean bombBlock = false;
	private boolean lightningBlock = false;
	
	public Block(Context context)
	{
		super(context);
	}

	public Block(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public Block(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	public void setFade(){
		fade = true;
	}

	public boolean getFade(){
		return fade;
	}
	
	public void setDown(int _numberOfFadeBlocks){
		down = true;
		numberOfFadeBlocks = _numberOfFadeBlocks;
	}
	
	public boolean getDown(){
		return down;
	}
	
	public void resetFade(){
		fade = false;
	}
	
	public void resetDown(){
		down = false;
		numberOfFadeBlocks = -1;
	}
	
	public void resetSpecialBlock(){
		bombBlock = false;
		lightningBlock = false;
	}

	public int getNOFB(){
		return numberOfFadeBlocks;
	}
	
	public int getColor(){
		return intColor;
	}
	
	public boolean isBombBlock(){
		return bombBlock;
	}
	
	public boolean isLightningBlock(){
		return lightningBlock;
	}
	
	public String getPath(){
		return GameResources.diamondName[intColor];
	}
	
	public void setBomb(){
		this.setBackgroundResource(GameResources.diamondIcon[7]);
		intColor = 7;
		bombBlock = true;
		fade = false;
	}
	
	public void setLightning(){
		this.setBackgroundResource(GameResources.diamondIcon[8]);
		intColor = 8;
		lightningBlock = true;
		fade = false;
	}
	
	// set default properties for the block
	public void setDiamondIcon(int _intColor)
	{
		this.setBackgroundResource(GameResources.diamondIcon[_intColor]);
		intColor = _intColor;
		fade = false;
		down = false;
	}
}
