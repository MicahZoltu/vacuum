package com.zoltu.Vacuum.Messaging;

import android.os.Handler;
import android.os.Looper;

/**
 * An enhanced version of Handler that deals with broadcasting and listening for you. Users need only to extend this
 * class and create the appropriately name processing functions for the messages they want to handle.
 **/
public abstract class MessagingContext extends Handler implements IBroadcaster, IListener
{
	protected final Broadcaster mBroadcaster = new Broadcaster();
	private final LocalListener mListener;
	
	/**
	 * @param pHandler
	 *            The handler that will be feeding us messages.
	 * @param pProcessingMethodName
	 *            The name of the method to call when dispatching incoming messages. Create an overload of this method
	 *            name for each message you want to be able to process.
	 **/
	public MessagingContext(Looper pLooper, String pProcessingMethodName)
	{
		super(pLooper);
		mListener = new LocalListener(this, pProcessingMethodName, this);
	}
	
	@Override public void AddListener(IListener pListener)
	{
		mBroadcaster.AddListener(pListener);
	}
	
	@Override public void RemoveListener(IListener pListener)
	{
		mBroadcaster.RemoveListener(pListener);
	}
	
	@Override public void ReceiveMessage(com.google.protobuf.Message pMessage)
	{
		mListener.ReceiveMessage(pMessage);
	}
	
	@Override public void handleMessage(android.os.Message pMessage)
	{
		mListener.DispatchMessage(pMessage);
	}
}
