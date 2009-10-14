package com.zoltu.Vacuum.View;

import android.content.Context;
import com.zoltu.Vacuum.R;
import com.zoltu.Vacuum.Map.Door;

public class DoorViewOpening extends DoorView
{
	public DoorViewOpening(Context pContext, Door pDoor)
	{
		super(pContext, pDoor);
		
		boolean lIsVertical = mDoor.GetLocation().Row() % 2 == 0;
		
		setBackgroundResource(R.drawable.opening);
		if (lIsVertical) getBackground().setLevel(10000);
	}
}
