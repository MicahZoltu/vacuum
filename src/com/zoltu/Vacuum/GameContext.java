package com.zoltu.Vacuum;

import android.os.Looper;
import com.zoltu.Vacuum.Messages.Level;
import com.zoltu.Vacuum.Messages.Level.Map;
import com.zoltu.Vacuum.Messaging.MessagingContext;

public class GameContext extends MessagingContext
{
	public Map mMap;
	
	public GameContext(Looper pLooper)
	{
		super(pLooper, "ProcessMessage");
	}
	
	public void ProcessMessage(Level.LoadedAnnouncement pAnnouncement)
	{
		mMap = pAnnouncement.getMap();
	}
}
