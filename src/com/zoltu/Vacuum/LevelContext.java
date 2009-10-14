package com.zoltu.Vacuum;

import android.os.Looper;
import com.zoltu.Vacuum.Messages.Level;
import com.zoltu.Vacuum.Messaging.Broadcaster;
import com.zoltu.Vacuum.Messaging.IListener;
import com.zoltu.Vacuum.Messaging.MessagingContext;

/**
 * The level thread. This thread will instantiate an implementation of LevelInterface which will deal with level
 * loading. Example implementations are randomly generated level, level loaded from disc, level loaded from the
 * Internet, or a level loaded from a multiplayer server.
 **/
public class LevelContext extends MessagingContext
{
	private ILevelLoader mLevelLoader;
	
	public LevelContext(Looper pLooper)
	{
		super(pLooper, "ProcessMessage");
	}
	
	/** Send a full state update to pListener. **/
	private void SendState(IListener pListener)
	{
		// Tell them about the current level type.
		if (mLevelLoader != null)
		{
			Level.TypeAnnouncement.Builder lAnnouncementBuilder = Level.TypeAnnouncement.newBuilder();
			lAnnouncementBuilder.setType(mLevelLoader.GetLevelType());
			Broadcaster.SendMessage(pListener, lAnnouncementBuilder.build());
		}
		
		// If a level is loading or has been loaded send the level loading announcement.
		if (mLevelLoader != null && mLevelLoader.GetLevelName() != null)
		{
			Level.LoadingAnnouncement.Builder lAnnouncementBuilder = Level.LoadingAnnouncement.newBuilder();
			lAnnouncementBuilder.setType(mLevelLoader.GetLevelType());
			lAnnouncementBuilder.setName(mLevelLoader.GetLevelName());
			Broadcaster.SendMessage(pListener, lAnnouncementBuilder.build());
		}
		
		// If the level load is complete then send the appropriate announcement.
		if (mLevelLoader != null)
		{
			Level.LoadedAnnouncement.Builder lAnnouncementBuilder = Level.LoadedAnnouncement.newBuilder();
			lAnnouncementBuilder.setType(mLevelLoader.GetLevelType());
			lAnnouncementBuilder.setName(mLevelLoader.GetLevelName());
			lAnnouncementBuilder.setMap(mLevelLoader.GetMap());
			Broadcaster.SendMessage(pListener, lAnnouncementBuilder.build());
		}
	}
	
	@Override public void AddListener(IListener pListener)
	{
		// Send the current state of things to any new listeners before we add them.
		SendState(pListener);
		super.AddListener(pListener);
	}
	
	private void AnnounceType()
	{
		Level.TypeAnnouncement.Builder lAnnouncementBuilder = Level.TypeAnnouncement.newBuilder();
		lAnnouncementBuilder.setType(mLevelLoader.GetLevelType());
		mBroadcaster.BroadcastMessage(lAnnouncementBuilder.build());
	}
	
	private void AnnounceLoading(String pLevelName)
	{
		Level.LoadingAnnouncement.Builder lAnnouncementBuilder = Level.LoadingAnnouncement.newBuilder();
		lAnnouncementBuilder.setType(mLevelLoader.GetLevelType());
		lAnnouncementBuilder.setName(mLevelLoader.GetLevelName());
		mBroadcaster.BroadcastMessage(lAnnouncementBuilder.build());
	}
	
	private void AnnounceLoaded()
	{
		Level.LoadedAnnouncement.Builder lAnnouncementBuilder = Level.LoadedAnnouncement.newBuilder();
		lAnnouncementBuilder.setType(mLevelLoader.GetLevelType());
		lAnnouncementBuilder.setName(mLevelLoader.GetLevelName());
		lAnnouncementBuilder.setMap(mLevelLoader.GetMap());
		mBroadcaster.BroadcastMessage(lAnnouncementBuilder.build());
	}
	
	private void ChangeLevelLoader(Level.Type pLevelType)
	{
		/* Instantiate the appropriate level loader (if it isn't already) and replace the old one with it. */
		switch (pLevelType)
		{
			case CAMPAIGN:
			case CUSTOM:
			case MULTIPLAYER:
			case ONLINE:
			case RANDOM:
			{
				if (!(mLevelLoader instanceof TestLevelLoader)) mLevelLoader = new TestLevelLoader();
			}
		}
		
		AnnounceType();
	}
	
	private void LoadLevel(String pLevelName)
	{
		AnnounceLoading(pLevelName);
		mLevelLoader.LoadLevel(pLevelName);
		AnnounceLoaded();
	}
	
	private void LoadNextLevel()
	{
		final String lNextLevelName = mLevelLoader.GetNextLevelName(); 
		AnnounceLoading(lNextLevelName);
		mLevelLoader.LoadLevel(lNextLevelName);
		AnnounceLoaded();
	}
	
	public void ProcessMessage(Level.TypeRequest pRequest)
	{
		ChangeLevelLoader(pRequest.getType());
	}
	
	public void ProcessMessage(Level.NextRequest pRequest)
	{
		LoadNextLevel();
	}
	
	public void ProcessMessage(Level.LoadRequest pRequest)
	{
		/* Switch to the appropriate loader. */
		ChangeLevelLoader(pRequest.getType());
		
		/* Load the requested level. */
		LoadLevel(pRequest.getName());
	}
}
