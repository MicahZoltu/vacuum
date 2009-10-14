package com.zoltu.Vacuum.View;

import com.zoltu.Vacuum.Map;
import com.zoltu.Vacuum.R;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Type;
import android.content.Context;
import android.view.View;

public class RoomView extends View
{
	public final Map.Room mRoom;
	
	public RoomView(Context pContext, Map.Room pRoom)
	{
		super(pContext);
		
		mRoom = pRoom;
		
		if (mRoom.GetType() == Type.ROOM) setBackgroundResource(R.drawable.room);
		else setBackgroundResource(R.drawable.void_room);
	}
}
