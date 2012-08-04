package appnet.diamond;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import appnet.diamond.R;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class DiamondActivity extends Activity
{
	private TextView txtTimer ,txtScore;
	private ImageButton btnGameStatus;
	private VerticalSeekBar seekbarU1, seekbarU2 ;
	private TextView nameUser1, nameUser2;
	private ImageView helpUser1, helpUser2;
	
	// set font style for timer and mine count to LCD style
	private Typeface lcdFont;
	
	private LinearLayout diamondField; // table layout to add diamonds to

//	private UpdateBlocksInRow updateRows[] ;
	private Queue <Integer> addedBlock = new LinkedList<Integer>();
	
	private Block diamonds[][]; // blocks for diamonds field

	private static int start = 0, end = 6;		// number of diamond colors
	private int numberOfRows = 8, numberOfColumns = 8;	//the number of rows & columns of game

	private boolean isBeginningGame = false;
	
	private static int countClick = 0;
	private static int score = 0, subScore = 0;
	
	//coorStClick[0] ([1]) (2): the coordinates of row (column) and (color) of the block of first click
	private int coorStClick[] = new int[3], coorNdClick[] = new int [3], suggest[] = new int[2];
	
	private Animation fadeIn, fadeOut , topDown;

	// timer to keep track of time elapsed
	private Handler timerTurn = new Handler();
	private Handler timerAnimShake = new Handler();
	private Handler timerAnimFadeOut = new Handler();
	private Handler timerAnimDown = new Handler();
	private Handler timerGame = new Handler();
	private Handler inforUser = new Handler();
	
	private int secondsTurn = 22, secondsAnimShake = 1;

	private boolean TURN = false , SUB_TURN_FINISH = false;
	private int MAX_SCORE = 100;
	private String USER1 = "Ohmygodntt", USER2 = "Babycat_258";
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	        
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
		fadeIn.reset();
		
		diamondField = (LinearLayout)findViewById(R.id.DiamondField);
		
		nameUser1 = (TextView)findViewById(R.id.nameUser1);
		nameUser2 = (TextView)findViewById(R.id.nameUser2);
		txtScore = (TextView) findViewById(R.id.score);
		txtTimer = (TextView) findViewById(R.id.timer);
		helpUser1 = (ImageView)findViewById(R.id.helpUser1);
		helpUser2 = (ImageView)findViewById(R.id.helpUser2);
		
		seekbarU1 = (VerticalSeekBar)findViewById(R.id.SeekBarUser1);
		seekbarU2 = (VerticalSeekBar)findViewById(R.id.SeekBarUser2);
		
		seekbarU1.setEnabled(false);
		seekbarU2.setEnabled(false);
		
		seekbarU1.setMax(MAX_SCORE + 10 % MAX_SCORE);
		seekbarU1.setMax(MAX_SCORE + 10 % MAX_SCORE);
		
		nameUser1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final Toast user1 = infoUser(USER1, score, R.drawable.ohmygodntt);
				user1.setGravity(Gravity.LEFT | Gravity.BOTTOM, 1, 1);
				new Thread(new Runnable() {
					public void run() {
						inforUser.post(new Runnable() {
							public void run() {
								user1.show();
							}
						});
					}
				}).start();
			}
		});
		
		nameUser2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				final Toast user2 = infoUser(USER2, 35, R.drawable.babycat_258);
				user2.setGravity(Gravity.RIGHT | Gravity.BOTTOM, 1,1);
				user2.show();
				
				new Thread(new Runnable() {
					public void run() {
						inforUser.post(new Runnable() {
							public void run() {
								user2.show();
							}
						});
					}
				}).start();
			}
		});
		
		helpUser1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				shakeText(diamonds[suggest[0]][suggest[1]]);
			}
		});
		
		lcdFont = Typeface.createFromAsset(getAssets(),"fonts/lcd2mono.ttf");
		
		txtScore.setTypeface(lcdFont);
		txtTimer.setTypeface(lcdFont);
		nameUser1.setTypeface(lcdFont);
		nameUser2.setTypeface(lcdFont);
		
		btnGameStatus = (ImageButton) findViewById(R.id.gameStatus);
		btnGameStatus.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				if (isBeginningGame == true )
					return;
				
				startNewGame();
				diamondField.setBackgroundResource(R.drawable.bg_caro);
				diamondField.startAnimation(fadeIn);
			}
		});
		
	}
	
	//Create information and upload avatar of users
	public Toast infoUser(String userName, int userScore, int avatar){
		Toast inforUser;
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.customtoast,(ViewGroup) findViewById(R.id.llToast));
			
			ImageView image = (ImageView) layout.findViewById(R.id.tvImageToast);
			image.setImageResource(avatar);
			
			TextView title = (TextView) layout.findViewById(R.id.tvTitleToast);
			title.setTypeface(lcdFont);
			title.setText(userName);
			
			TextView text = (TextView) layout.findViewById(R.id.tvTextToast);
			text.setTypeface(lcdFont);
			text.setText("Score : " + String.valueOf(userScore) + "/" + MAX_SCORE);
			
			inforUser = new Toast(getApplicationContext());
			inforUser.setDuration(Toast.LENGTH_LONG);
			inforUser.setView(layout);
			inforUser.show();
		
			return inforUser;
	};
	
	public String shortName(String name){
		String str = "";
		if (name.length() > 11){
			str += name.subSequence(0, 10) + "~";
			return str;
		}
		return name;
	}

	private void startNewGame()
	{
		// plant mines and do rest of the calculations
		createDiamondField();
		// display all blocks in UI
		showDiamondField();
		
		seekbarU1.setVisibility(1);
		seekbarU2.setVisibility(1);
		
		nameUser1.setText(shortName(USER1));
		nameUser2.setText(shortName(USER2));
		nameUser1.setVisibility(1);
		nameUser2.setVisibility(1);
		helpUser1.setVisibility(1);
		helpUser2.setVisibility(1);
		
		timerGame.removeCallbacks(updateTimerGame);
		timerGame.postAtTime(updateTimerGame, 100);
		
		isBeginningGame = true;
	}

	private void showDiamondField()											// 0-------------------> Row
	{																		// |
		// remember we will not show 0th and last Row and Columns			// |
		// they are used for calculation purposes only						// |
		diamondField.setOrientation(LinearLayout.HORIZONTAL);				// Column
    	LinearLayout[] layoutRows = new LinearLayout[13]; 
    	
    	LinearLayout.LayoutParams layout_param = new LinearLayout.LayoutParams(
    			new LayoutParams( 33 , 34 * numberOfColumns + 2));
    	

    	for (int i=0; i < numberOfRows; i++)
    	{
    		layoutRows[i] = new LinearLayout(this);
    		layoutRows[i].setOrientation(LinearLayout.VERTICAL);
    		layoutRows[i].setLayoutParams(layout_param);
    		layoutRows[i].setPadding(0, 0, 0, 2);
    		diamondField.addView(layoutRows[i]);
    	}
    	
    	for (int row = 0; row < numberOfRows ; row++)
    		for (int column = 0; column < numberOfColumns; column++)
			{
    			diamonds[row][column].setLayoutParams(new LayoutParams(31, 34));
    			diamonds[row][column].setPadding(3, 1, 3, 1);
				layoutRows[row].addView(diamonds[row][column]);
			}
	}

	//swap 2 block 
	public void swapBlocks(Block swap1, Block swap2){
		
		int tempColor = swap1.getColor();
		swap1.setDiamondIcon(swap2.getColor());
		swap2.setDiamondIcon(tempColor);
	}
	
	private void createDiamondField()
	{
//		updateRows = new UpdateBlocksInRow[numberOfRows];
		
		diamonds = new Block[numberOfRows][numberOfColumns];
		Random random = new Random();
		
		for (int row = 0; row < numberOfRows; row++)
		{
			for (int column = 0; column < numberOfColumns; column++)
			{	
				diamonds[row][column] = new Block(this);
				diamonds[row][column].setDiamondIcon(randomInteger(start, end, random));

				final int currentRow = row , currentColumn = column;
				
				OnClickListener ocl = new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						
						//Check this block is bomb block , isn't it
						if (diamonds[currentRow][currentColumn].isBombBlock()){
							subScore = bombBonus(currentRow, currentColumn);
							
							fadeAnimation();
							timerAnimFadeOut.removeCallbacks(updateTimeForAnimFadeOut);
							// tell timer to run call back after 0.1 second
							timerAnimFadeOut.postDelayed(updateTimeForAnimFadeOut, 100);
							return;
						}
						//Check this block is lightning block , isn't it
						if (diamonds[currentRow][currentColumn].isLightningBlock()){
							Log.v("", "Is lightning block");
							lightningBonus(currentRow, currentColumn);
							subScore = numberOfColumns + numberOfRows;
							
							fadeAnimation();
							timerAnimFadeOut.removeCallbacks(updateTimeForAnimFadeOut);
							// tell timer to run call back after 0.1 second
							timerAnimFadeOut.postDelayed(updateTimeForAnimFadeOut, 100);
							return;
						}
						
						//the first click of a turn
						if (countClick++ % 2 == 0){	
							diamonds[suggest[0]][suggest[1]].clearAnimation();
							coorStClick[0] = currentRow;
							coorStClick[1] = currentColumn;
							coorStClick[2] = diamonds[currentRow][currentColumn].getColor();
							shakeImage(diamonds[currentRow][currentColumn]);

							return;
						}
						
						//the second click of a turn
						else{	
							diamonds[coorStClick[0]][coorStClick[1]].clearAnimation();
							diamonds[suggest[0]][suggest[1]].clearAnimation();
							
							coorNdClick[0] = currentRow;
							coorNdClick[1] = currentColumn;
							coorNdClick[2] = diamonds[currentRow][currentColumn].getColor();
							
							//check situation right moving
							if (coorStClick[0] == coorNdClick[0] && coorStClick[1] == coorNdClick[1])
								return;
							
							if (Math.abs(coorStClick[0] - coorNdClick[0]) > 1 ||
									Math.abs(coorStClick[1] - coorNdClick[1]) > 1){
								return;
							}
							
							if (Math.abs(coorStClick[0] - coorNdClick[0]) == Math.abs(coorStClick[1] - coorNdClick[1]))
								return;
								
							//swap the color of 2 block
							swapBlocks(diamonds[coorStClick[0]][coorStClick[1]], diamonds[coorNdClick[0]][coorNdClick[1]]);

							subScore = checkBonus();
							
							//if this turn is wrong , swap 2 block an again
							if (subScore < 3){
								secondsAnimShake = 1;
								timerAnimShake.removeCallbacks(updateTimeForAnimShake);
								// tell timer to run call back after 0.5 second
								timerAnimShake.postDelayed(updateTimeForAnimShake, 500);
							}
							
							else {
								fadeAnimation();
								timerAnimFadeOut.removeCallbacks(updateTimeForAnimFadeOut);
								// tell timer to run call back after 0.1 second
								timerAnimFadeOut.postDelayed(updateTimeForAnimFadeOut, 100);
							}
						}
					}
				};
				diamonds[row][column].setOnClickListener(ocl);
			}
		}
	}
	
	//update score and diamond diamond field
	private void updateScore(){
		updateAfterBonus();
		
		score += subScore;		//rise up subScore points for "score"
		
		if (score <= MAX_SCORE)
			seekbarU1.setProgress(score);
		
//		if (score >= MAX_SCORE)
//			Toast.makeText(getApplicationContext(), "You Win", Toast.LENGTH_LONG).show();
		
		timerTurn.removeCallbacks(updateTimeForTurn);			//stop "timerTurn"
		if (score < 10)
		{
			txtScore.setText("00" + Integer.toString(score));
		}
		else if (score < 100)
		{
			txtScore.setText("0" + Integer.toString(score));
		}
		else if (score < 1000)
		{
			txtScore.setText(Integer.toString(score));
		}
	}
	
	//set animation fade out for a block
    private void fadeOutImage(Block block)
    {
    	fadeOut = AnimationUtils.loadAnimation(this, R.anim.hyperspace_jump);
        fadeOut.setRepeatMode(Animation.RESTART);
        fadeOut.setRepeatCount(Animation.INFINITE);
        fadeOut.setFillAfter(true);
        block.startAnimation(fadeOut);
    }
    
    //set animation shake for a block
    private void shakeImage(Block block)
    {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        shake.setRepeatMode(Animation.RESTART);
        shake.setRepeatCount(Animation.INFINITE);
        block.startAnimation(shake);
    }
    
    //set animation fade out for a block
    private void topDownImage(Block block)
    {
    	topDown = AnimationUtils.loadAnimation(this, GameResources.topDown[block.getNOFB()]);
        topDown.setRepeatMode(Animation.RESTART);
        topDown.setRepeatCount(Animation.INFINITE);
        block.startAnimation(topDown);
    }
    
    //set animation shake for textview
    private void shakeText(Object text){
    	Animation shakeText = AnimationUtils.loadAnimation(this, R.anim.shake_text);
    	shakeText.setRepeatMode(Animation.RESTART);
    	shakeText.setRepeatCount(Animation.INFINITE);
        ((View) text).startAnimation(shakeText);
    }
    
	//create the animation of each blocks when them fade
	public void fadeAnimation(){
		for (int i = 0 ; i < numberOfRows; i++)
			for (int j = 0 ; j < numberOfColumns; j++){
				if (diamonds[i][j].getFade() == true){
					fadeOutImage(diamonds[i][j]);
					//diamonds[i][j].setVisibility(4);
				}
			}
	}
	
	//create the animation "top - down" for blocks when the under this block hasn't any other blocks
	public void downAnimation(){
		for (int i = 0 ; i < numberOfRows; i++)
			for (int j = 0 ; j < numberOfColumns; j++){
				if (diamonds[i][j].getDown() == true){
					topDownImage(diamonds[i][j]);
				}
			}
	}
	
	//clear the fade animation of each blocks
	public void deFadeAnimation(){
		for (int i = 0 ; i < numberOfRows; i++)
			for (int j = 0 ; j < numberOfColumns; j++){
				if (diamonds[i][j].getFade() == true ){
					diamonds[i][j].clearAnimation();
					diamonds[i][j].resetFade();
				}
			}
	}
	
	//clear the top-down animation of each blocks
	public void deTopDownAnimation(){
		for (int i = 0 ; i < numberOfRows; i++)
			for (int j = 0 ; j < numberOfColumns; j++){
				if (diamonds[i][j].getDown() == true ){
					diamonds[i][j].clearAnimation();
					diamonds[i][j].resetDown();
				}
			}
	}

	public int bombBonus(int _row, int _column){
		diamonds[_row][_column].resetSpecialBlock();
		int bombScore = 0;
		for (int i = _row - 1; i <= _row + 1 ; i ++){
			
			if (i < 0 || i >= numberOfRows)
				continue;
			
			for (int j = _column - 1 ; j <= _column + 1; j ++){
				if (j < 0 || j >= numberOfColumns)
					continue;
				if (diamonds[i][j].isBombBlock())
					bombBonus(i, j);
				else if (diamonds[i][j].isLightningBlock())
					lightningBonus(i, j);
				else {
					diamonds[i][j].setFade();
					bombScore++;
				}
			}
		}
		return bombScore;
	}
	
	public void lightningBonus(int _row, int _column){
		diamonds[_row][_column].resetSpecialBlock();
		
		for (int i = 0 ; i < numberOfRows ; i++){
			if (diamonds[i][_column].isBombBlock())
				bombBonus(i, _column);
			else if (diamonds[i][_column].isLightningBlock())
				lightningBonus(i, _column);
			else
				diamonds[i][_column].setFade();
		}
		for (int j = 0 ; j < numberOfColumns ; j++){
			if (diamonds[_row][j].isBombBlock())
				bombBonus(_row, j);
			else if (diamonds[_row][j].isLightningBlock())
				lightningBonus(_row, j);
			else
				diamonds[_row][j].setFade();
		}
	}
	
	//check after turn hasn't bonus, has bonus. If has, find a suggest
	public String noMoreMoves(){

		//Case 1: has special block
		for (int row = 0; row < numberOfRows ; row++)
			for (int column = 0; column < numberOfColumns; column++)
				if(diamonds[row][column].isBombBlock() || diamonds[row][column].isLightningBlock())
					return String.valueOf(row) + String.valueOf(column);
		
		//					  x
		//Case 2: 	 		? o ?
		//					  x
		for (int row = 0; row < numberOfRows ; row++)
		{
			for (int column = 1; column < numberOfColumns - 1; column++)
			{
				if (diamonds[row][column-1].getColor() == diamonds[row][column + 1].getColor()){
					if ( diamonds[row][column-1].getColor() == (row >= 1 ? diamonds[row-1][column].getColor() : 100))
						return String.valueOf(row-1) + String.valueOf(column);
					
					if ( diamonds[row][column-1].getColor() == ((row + 1) < numberOfRows ? diamonds[row+1][column].getColor() : 100))
						return String.valueOf(row+1) + String.valueOf(column);
				}
			}
		}
		//		   		  	  ?
		//Case 3: 	 		x o x
		//					  ?
		for (int row = 1; row < numberOfRows - 1 ; row++)
		{
			for (int column = 0; column < numberOfColumns; column++)
			{
				if (diamonds[row-1][column].getColor() == diamonds[row+1][column].getColor()){
					if ( diamonds[row-1][column].getColor() == (column >= 1 ?diamonds[row][column-1].getColor() : 100))
						return String.valueOf(row) + String.valueOf(column-1);
					
					if ( diamonds[row-1][column].getColor() == ((column+1) < numberOfColumns ? diamonds[row][column+1].getColor() : 100)){
						return String.valueOf(row) + String.valueOf(column+1);}
				}
			}
		}
		//																							?
		// Case 4 : 																			 ?  o  ?
		for (int row = 0; row < numberOfRows; row++)	 								//			x
		{																				//			x
			for (int column = 0; column < numberOfColumns - 2 ; column++)				//		 ?  o  ?
			{																			//		    ?
				int currentColor = diamonds[row][column].getColor(); 

				if ( currentColor == diamonds[row][column + 1].getColor()){
					if (currentColor == (column >= 2 ? diamonds[row][column-2].getColor() : 100) )
						return String.valueOf(row) + String.valueOf(column-2);
				
					if (currentColor == ((column + 3) < numberOfColumns ? diamonds[row][column+3].getColor() : 100) )
						return String.valueOf(row) + String.valueOf(column+3);
					
					if (column > 1){
						if (currentColor == (row >= 1 ? diamonds[row-1][column-1].getColor() : 100))
							return String.valueOf(row-1) + String.valueOf(column-1);
						
						if (currentColor == ((row + 1) < numberOfRows ? diamonds[row+1][column-1].getColor() : 100))
							return String.valueOf(row+1) + String.valueOf(column-1);
					}
					
					if (currentColor == (row >= 1 ? diamonds[row-1][column+2].getColor() : 100))
						return String.valueOf(row-1) + String.valueOf(column+2);
					
					if (currentColor == ((row + 1) < numberOfRows ? diamonds[row+1][column+2].getColor() : 100))
						return String.valueOf(row+1) + String.valueOf(column+2);
				}
			}
		}
		//																	   ?		?	
		// Case 5															?  o  x  x  o  ?							
		for (int column = 0; column < numberOfColumns ; column++)	//		   ?		?	 							
		{																				
			for (int row = 0; row < numberOfRows - 2; row++)				
			{																			
				int currentColor = diamonds[row][column].getColor(); 

				if ( currentColor == diamonds[row+1][column].getColor()){
					if (currentColor == (row >= 2 ? diamonds[row-2][column].getColor() : 100) )
						return String.valueOf(row-2) + String.valueOf(column);
				
					if (currentColor == ((row + 3) < numberOfColumns ? diamonds[row+3][column].getColor() : 100) )
						return String.valueOf(row+3) + String.valueOf(column);
					
					if (row > 1){
						if (currentColor == (column >= 1 ? diamonds[row-1][column-1].getColor() : 100))
							return String.valueOf(row-1) + String.valueOf(column-1);
						
						if (currentColor == ((column + 1) < numberOfColumns ? diamonds[row-1][column+1].getColor() : 100))
							return String.valueOf(row-1) + String.valueOf(column+1);
					}
					
					if (currentColor == (column >= 1 ? diamonds[row+2][column-1].getColor() : 100))
						return String.valueOf(row+2) + String.valueOf(column-1);
					
					if (currentColor == ((column + 1) < numberOfColumns ? diamonds[row+2][column+1].getColor() : 100))
						return String.valueOf(row+2) + String.valueOf(column+1);
				}
			}
		}
		return "";
	}
	
	//check bonus after move
	// Check your turn is true , isn't it? and set fade for bonus blocks
	public int checkBonus(){
		
		//    	1 : x x x							5 : x x x x x
		//		2 : x x x x								  x					
		//		3 : x x x x x							  x					x
		//		4 : x x x x											6 :   x x x x
		//			x														x
		//			x														x
		
																							   //C   x
		int numberOfBlocks = 0 , currentColor;												   //    x
		int column_startBlock = 0, column_endBlock = 0;		// 2 position A & B : A x x x B			 x
		int row_startBlock = 0, row_endBlock = 0;			 	   		   //2 position C & D   :D   x	
		
		int blocksOfRow = 1, blockOfColumn = 1;
		int ignore = 100;
		
//***************** Check bonus follow row*********************************************
		for (int row = 0; row < numberOfRows ; row++)
		{
		    currentColor = diamonds[row][0].getColor();
		    row_startBlock = row;
		    blocksOfRow = 1;
		    ignore = 100;
			for (int column = 1; column < numberOfColumns ; column++)
			{
				//if color of the current block & the previous block are not same
				if (diamonds[row][column].getColor() == currentColor){
					blocksOfRow ++;
					if (blocksOfRow >= 3 && column == numberOfColumns - 1){
						if (blocksOfRow >= 4){
							ignore = column - 2;
							if (blocksOfRow == 4)
								diamonds[row][column-2].setBomb();
							else diamonds[row][column-2].setLightning();
						}
						
						numberOfBlocks += blocksOfRow;
						
						for (int i = numberOfColumns - blocksOfRow; i < numberOfColumns; i++){
							if (i == ignore)
								continue;
							diamonds[row][i].setFade();
						}
						
						blocksOfRow = 1;
						ignore = 100;
						continue;
					}
				}
				
				//if color of the current block & the previous block are not same
				else if (diamonds[row][column].getColor() != currentColor){
					currentColor = diamonds[row][column].getColor();
					column_endBlock	  = column;
					
					if (blocksOfRow >=3 ){
						if (blocksOfRow >= 4){
							ignore = column - 2;
							if (blocksOfRow == 4)
								diamonds[row][column-2].setBomb();
							else diamonds[row][column-2].setLightning();
						}
						
						numberOfBlocks += blocksOfRow;
						
						column_startBlock = column_endBlock - blocksOfRow ;
					
						for (int i = column_startBlock; i < column_endBlock ; i++){
							if (i == ignore)
								continue;
							diamonds[row][i].setFade();
						}
					}
					blocksOfRow = 1;
					ignore = 100;
				}
			}
		}
		
//********************Check bonus follow row*********************************************
		
//***************** Check bonus follow column*********************************************
		
		for (int column = 0; column < numberOfColumns ; column++)
		{
		    currentColor = diamonds[0][column].getColor();
		    column_startBlock = column;
		    blockOfColumn = 1;
		    ignore = 100;
			for (int row = 1; row < numberOfRows ; row++)
			{
				//if color of the current block & the previous block are not same
				if (diamonds[row][column].getColor() == currentColor){
					blockOfColumn ++;
					if (blockOfColumn >= 3 && row == numberOfRows - 1){
						if (blockOfColumn >= 4){
							ignore = row - 2;
							if (blockOfColumn == 4)
								diamonds[row-2][column].setBomb();
							else diamonds[row-2][column].setLightning();
						}
						
						numberOfBlocks += blockOfColumn;
						
						for (int i = numberOfRows - blockOfColumn; i < numberOfRows; i++){
							if (i == ignore)
								continue;
							if (diamonds[i][column].getFade() == true){
								diamonds[i][column].setLightning();
								continue;
							}
							diamonds[i][column].setFade();
						}
						
						blockOfColumn = 1;
						ignore = 100;
						continue;
					}
				}
				
				//if color of the current block & the previous block are not same
				else if (diamonds[row][column].getColor() != currentColor){
					currentColor = diamonds[row][column].getColor();
					row_endBlock	  = row;
					
					if (blockOfColumn >=3 ){
						if (blockOfColumn >= 4){
							ignore = row - 2;
							if (blockOfColumn == 4)
								diamonds[row-2][column].setBomb();
							else diamonds[row-2][column].setLightning();
						}
						
						numberOfBlocks += blockOfColumn;
						
						row_startBlock = row_endBlock - blockOfColumn;
					
						for (int i = row_startBlock; i < row_endBlock; i++){
							if (i == ignore)
								continue;
							
							if (diamonds[i][column].getFade() == true){
								diamonds[i][column].setLightning();
								continue;
							}
							diamonds[i][column].setFade();
						}
					}
					blockOfColumn = 1;
					ignore = 100;
				}
			}
		}
//***************** Check bonus follow column*********************************************	
		
		
//******************************
		return numberOfBlocks;
	}

/**
	//Find block must implement the down animation
	public void updateAfterBonus(){
		
		for (int row = 0; row < numberOfRows; row++){
			int landmarkColumn = numberOfColumns - 1;
			int numberOfFadeBlocks = -1;
			updateRows[row] = new UpdateBlocksInRow();
			updateRows[row].setMaxColor(numberOfColumns);
			
			while (landmarkColumn >= 0){
				if (diamonds[row][landmarkColumn].getFade() == true){
					
					for (int i = landmarkColumn ; i >= 0; i--){
						if (diamonds[row][i].getFade() == true){
							numberOfFadeBlocks++;
							continue;
						}
						else{
							diamonds[row][i].setDown(numberOfFadeBlocks);
							updateRows[row].addColor(diamonds[row][i].getColor());
						}
					}
					break;
				}
				landmarkColumn--;
			}
			updateRows[row].additionalColor();
		}
		downAnimation();
	}
*/
	
	//Find block must implement the down animation
	public void updateAfterBonus(){
		
		Random random = new Random();
		
		for (int row = 0; row < numberOfRows; row++){
			int landmarkColumn = numberOfColumns - 1;
			int j = 0 ,color = 0 ;
			
			while (landmarkColumn > 0 && diamonds[row][landmarkColumn].getFade() == false)
				landmarkColumn--;
				
			//if the fade block has coordinate : [row][0] so set color and setDown = 1 for it
			if (landmarkColumn == 0) {
				
				if (diamonds[row][landmarkColumn].getFade() == true){
					color = randomInteger(start, end, random);
					diamonds[row][0].setDiamondIcon(color);
					addedBlock.add(color);
					
					diamonds[row][0].setDown(1);
				}
					
				else continue;
			}
			
			j = landmarkColumn;
			
			for (int i = landmarkColumn; i >= 0 ; i --){
				
				while (j >= 0 && diamonds[row][j].getFade() == true)
					j--;
				
				if (j >= 0) {
					diamonds[row][i].setDiamondIcon(diamonds[row][j].getColor());
					diamonds[row][i].setDown(i - j);
					
					if (diamonds[row][j].isBombBlock())
						diamonds[row][i].setBomb();
					else if (diamonds[row][j].isLightningBlock())
						diamonds[row][i].setLightning();
					
					diamonds[row][j].resetSpecialBlock();
					j--;
				}
				
				else{
					for (int z = i ; z >= 0 ; z --){
						color = randomInteger(start, end, random);
						diamonds[row][z].setDiamondIcon(color);
						addedBlock.add(color);
						
						if (z > 0)
							diamonds[row][z].setDown(i);
						else 
							diamonds[row][0].setDown(1);
					}
					break;
				}
			}
			
		}
//		String added = "";
//		while(addedBlock.size() > 0)
//			added += GameResources.name[addedBlock.poll()] + "   " ;
//		
//		Log.v("", added);
	}
	
/**
	//Update Diamond Field 
	public void updateDiamondField(){
		for (int row = 0; row < numberOfRows; row++){
			int landmarkColumn = numberOfColumns - 1;
			
			while (landmarkColumn >= 0){
				if (diamonds[row][landmarkColumn].getFade() == true){
					
					for (int i = landmarkColumn ; i >= 0; i--){
						diamonds[row][i].setDiamondIcon(updateRows[row].pollColor());
						diamonds[row][i].setVisibility(1);
					}
					updateRows[row].resetQueue();
					break;
				}
				landmarkColumn--;
			}
		}
	}
*/

	//create a random number between "start" to "end"
	private static int randomInteger(int start, int end, Random random) {
		int range = end - start + 1;
		int fraction = (int) (range * random.nextDouble());
		int randomNumber = fraction + start;
		return randomNumber;
	}

	public void startTimer()
	{
		TURN = true;
		secondsTurn = 21;
		nameUser1.clearAnimation();
		shakeText(nameUser1);
		timerTurn.removeCallbacks(updateTimeForTurn);
		// tell timer to run call back after 1 second
		timerTurn.postDelayed(updateTimeForTurn, 1000);
	}

	// timer call back when timer is ticked
	private Runnable updateTimeForTurn = new Runnable()
	{
		public void run()
		{
			long currentMilliseconds = System.currentTimeMillis();
			--secondsTurn;

			if (secondsTurn < 10)
			{
				txtTimer.setText("00" + Integer.toString(secondsTurn));
			}
			else if (secondsTurn < 100)
			{
				txtTimer.setText("0" + Integer.toString(secondsTurn));
			}
			else if (secondsTurn < 1000)
			{
				txtTimer.setText(Integer.toString(secondsTurn));
			}

			// add notification
			timerTurn.postAtTime(this, currentMilliseconds);
			
			// notify to call back after 1 seconds
			// basically to remain in the timer loop
			timerTurn.postDelayed(updateTimeForTurn, 1000);
			
			if (secondsTurn == 0){
				TURN = false;
//				Toast.makeText(getApplicationContext(), "Lost Turn", Toast.LENGTH_LONG).show();
				
				btnGameStatus.setBackgroundResource(R.drawable.sad);
				timerTurn.removeCallbacks(updateTimeForTurn);
				
				return ;
			}
			
		}
	};
	
	// timer call back when timer is ticked
	private Runnable updateTimeForAnimShake = new Runnable()
	{
		public void run()
		{
			long currentMilliseconds = System.currentTimeMillis();
			--secondsAnimShake;

			timerAnimShake.postAtTime(this, currentMilliseconds);
			timerAnimShake.postDelayed(updateTimeForAnimShake, 500);
			
			if (secondsAnimShake == 0){
					swapBlocks(diamonds[coorStClick[0]][coorStClick[1]], diamonds[coorNdClick[0]][coorNdClick[1]]);
				
				timerAnimShake.removeCallbacks(updateTimeForAnimShake);
				return;
			}
		}
	};
	
	private Runnable updateTimeForAnimFadeOut = new Runnable()
	{
		public void run()
		{
			long currentMilliseconds = System.currentTimeMillis();

			timerAnimFadeOut.postAtTime(this, currentMilliseconds);
			timerAnimFadeOut.postDelayed(updateTimeForAnimFadeOut, 100);
			
			if (fadeOut.hasEnded()){
				timerAnimFadeOut.removeCallbacks(updateTimeForAnimFadeOut);
				
				updateScore();
				deFadeAnimation();
				subScore = checkBonus();
				
				downAnimation();
				timerAnimDown.removeCallbacks(updateTimeForAnimTopDown);
				timerAnimDown.postDelayed(updateTimeForAnimTopDown, 100);
				
				String sug = noMoreMoves();
				if (sug == ""){
					Toast.makeText(getApplicationContext(), "NO MORE MOVES", Toast.LENGTH_LONG).show();
				}
				else{
					suggest[0] = Integer.valueOf(sug.charAt(0)) - 48;
					suggest[1] = Integer.valueOf(sug.charAt(1)) - 48;
				}
				
				return ;
			}
		}
	};
	
	private Runnable updateTimeForAnimTopDown = new Runnable()
	{
		public void run()
		{
			long currentMilliseconds = System.currentTimeMillis();

			timerAnimDown.postAtTime(this, currentMilliseconds);
			timerAnimDown.postDelayed(updateTimeForAnimTopDown, 100);
			
			if (topDown.hasEnded()){
				timerAnimDown.removeCallbacks(updateTimeForAnimTopDown);
				deTopDownAnimation();
				SUB_TURN_FINISH = true ;
				
				return ;
			}
		}
	};
	
	private Runnable updateTimerGame = new Runnable()
	{
		public void run()
		{
			long currentMilliseconds = System.currentTimeMillis();

			timerGame.postAtTime(this, currentMilliseconds);
			timerGame.postDelayed(updateTimerGame, 100);
			
			if (TURN == false){
				diamonds[suggest[0]][suggest[1]].clearAnimation();
				startTimer();
			}
			
			if (secondsTurn == 0){
				timerGame.removeCallbacks(updateTimerGame);
			}
			
			else if (SUB_TURN_FINISH == true){
				SUB_TURN_FINISH = false ;

				if (subScore >= 3 ){
					fadeAnimation();
					timerAnimFadeOut.removeCallbacks(updateTimeForAnimFadeOut);
					timerAnimFadeOut.postDelayed(updateTimeForAnimFadeOut, 100);
				}
				else {
					diamonds[suggest[0]][suggest[1]].clearAnimation();
					startTimer();
				}
			}
		}
	};
}