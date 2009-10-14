package com.zoltu.Vacuum.View;

import android.content.Context;
import com.zoltu.Vacuum.R;
import com.zoltu.Vacuum.Map.Door;

public class DoorViewWall extends DoorView
{
	public DoorViewWall(Context pContext, Door pDoor)
	{
		super(pContext, pDoor);
		
		boolean lIsVertical = mDoor.GetLocation().Row() % 2 == 0;
		
		setBackgroundResource(R.drawable.wall);
		if (lIsVertical) getBackground().setLevel(10000);
	}
}
