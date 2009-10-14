package com.zoltu.Vacuum.View;

import com.zoltu.Vacuum.Map;
import android.content.Context;
import android.view.View;

public abstract class DoorView extends View
{
	public final Map.Door mDoor;
	
	public DoorView(Context pContext, Map.Door pDoor)
	{
		super(pContext);
		
		mDoor = pDoor;
	}
}
