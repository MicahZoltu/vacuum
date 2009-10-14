package com.zoltu.Vacuum.View;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.RotateDrawable;
import com.zoltu.Vacuum.Map;
import com.zoltu.Vacuum.R;
import com.zoltu.Vacuum.Messages.Level.Map.Door.State;

public class DoorViewDoor extends DoorView
{	
	private final boolean mIsVertical;
	private final boolean mIsClosed;
	
	public DoorViewDoor(Context pContext, Map.Door pDoor) throws IllegalArgumentException
	{
		super(pContext, pDoor);
		
		mIsVertical = mDoor.GetLocation().Row() % 2 == 0;
		mIsClosed = mDoor.GetState() == State.CLOSED;
		
		switch (pDoor.GetType())
		{
			case BLUE:
				if (mIsClosed) setBackgroundResource(R.drawable.door_closing_blue);
				else setBackgroundResource(R.drawable.door_opening_blue);
				break;
			case CYAN:
			case GREEN:
				if (mIsClosed) setBackgroundResource(R.drawable.door_closing_green);
				else setBackgroundResource(R.drawable.door_opening_green);
				break;
			case MAGENTA:
			case RED:
				if (mIsClosed) setBackgroundResource(R.drawable.door_closing_red);
				else setBackgroundResource(R.drawable.door_opening_red);
				break;
			case YELLOW:
				break;
			case ENTRANCE:
			case EXIT:
			case OPENING:
			case WALL:
			default:
				throw new IllegalArgumentException("An attempt was made to instantiate a DoorView with an Entrance, Exit, Opening, Wall, or Unhandled type.");
		}
		
		if (mIsVertical) getBackground().setLevel(10000);
	}
	
	/* Once the view is attached to the window we can safely start the initial animation. */
	@Override protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		
		RotateDrawable lRotatedBackground = (RotateDrawable)getBackground();
		AnimationDrawable lAnimatedBackground = (AnimationDrawable)lRotatedBackground.getDrawable();
		if (lAnimatedBackground != null)
		{
			lAnimatedBackground.start();
		}
	}
}
