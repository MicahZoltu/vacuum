package com.zoltu.Vacuum;

import android.graphics.Point;

public class ScenePiece extends SceneItem
{
	public final int mID; 
	
	public ScenePiece(final Point pLocation, final int pID)
	{
		super(pLocation);
		
		mID = pID;
	}
}
