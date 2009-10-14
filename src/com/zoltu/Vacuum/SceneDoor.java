package com.zoltu.Vacuum;

import com.zoltu.Vacuum.Messages.Level.Map.Room.Door;
import android.graphics.Point;

public class SceneDoor extends SceneItem
{
	final private Door mDoor;
	
	public SceneDoor(Point pLocation, Door pDoor)
	{
		super(pLocation);
		mDoor = pDoor;
	}
	
	public Door.Type GetType()
	{
		return mDoor.getType();
	}
}
