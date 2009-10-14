package com.zoltu.Vacuum;

import android.app.Activity;
import android.os.Bundle;
import android.os.HandlerThread;
import com.zoltu.Vacuum.Messages.GamePause;
import com.zoltu.Vacuum.Messages.Level;
import com.zoltu.Vacuum.Messaging.Broadcaster;
import com.zoltu.Vacuum.Messaging.IBroadcaster;
import com.zoltu.Vacuum.Messaging.IListener;
import com.zoltu.Vacuum.Messaging.MessagingContext;

public class HUDActivity extends Activity implements IBroadcaster
{
	private Broadcaster mBroadcaster = new Broadcaster();;
	
	private final MessagingContext mGameContext;
	private final MessagingContext mLevelContext;
	private MessagingContext mRendererContext;
	
	public HUDActivity()
	{
		try
		{
			Class.forName("com.zoltu.Vacuum.Messages.GamePause");
			Class.forName("com.zoltu.Vacuum.Messages.Level");
		}
		catch (ClassNotFoundException lException)
		{
			lException.printStackTrace();
		}
		
		/* Start the threads. */
		HandlerThread lHandlerThread;
		
		lHandlerThread = new HandlerThread("Game");
		lHandlerThread.start();
		mGameContext = new GameContext(lHandlerThread.getLooper());
		
		lHandlerThread = new HandlerThread("Level");
		lHandlerThread.start();
		mLevelContext = new LevelContext(lHandlerThread.getLooper());
	}
	
	@Override public void onCreate(Bundle pSavedInstanceState)
	{
		super.onCreate(pSavedInstanceState);
		setContentView(R.layout.main);
		
		/* ViewRendererContext runs in the main thread since it interfaces so closely with the Android rendering system. */
		mRendererContext = new ViewRendererContext(this, getMainLooper(), (ViewGroupRenderer)findViewById(R.id.ViewGroupRenderer));
		
		/* Hook everything together. */
		this.AddListener(mGameContext);
		this.AddListener(mRendererContext);
		this.AddListener(mLevelContext);
		
		mLevelContext.AddListener(mGameContext);
		mLevelContext.AddListener(mRendererContext);
		
		/* Load a test level. */
		Broadcaster.SendMessage(mLevelContext, Level.LoadRequest.newBuilder().setType(Level.Type.CAMPAIGN).setName("Test").build());
	}
	
	@Override protected void onPause()
	{
		super.onPause();
		mBroadcaster.BroadcastMessage(GamePause.PauseAnnouncement.getDefaultInstance());
	}
	
	@Override protected void onResume()
	{
		super.onResume();
		mBroadcaster.BroadcastMessage(GamePause.UnpausedAnnouncement.getDefaultInstance());
	}
	
	@Override public void AddListener(IListener pListener)
	{
		mBroadcaster.AddListener(pListener);
	}
	
	@Override public void RemoveListener(IListener pListener)
	{
		mBroadcaster.RemoveListener(pListener);
	}
}
