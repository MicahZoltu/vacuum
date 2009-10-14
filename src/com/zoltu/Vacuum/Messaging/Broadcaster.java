package com.zoltu.Vacuum.Messaging;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Broadcaster implements IBroadcaster
{
	@Override public void AddListener(IListener pListener)
	{
		mListeners.add(pListener);
	}
	
	@Override public void RemoveListener(IListener pListener)
	{
		mListeners.remove(pListener);
	}
	
	/**
	 * Called by the owner of this broadcaster when it wants to send a message out to all of it's listeners.
	 * 
	 * @param pMessage
	 *            The message to send.
	 **/
	public void BroadcastMessage(com.google.protobuf.Message pMessage)
	{
		synchronized (mListeners)
		{
			for (IListener lListener : mListeners)
			{
				SendMessage(lListener, pMessage);
			}
		}
	}
	
	/**
	 * Called by someone when they want to send a message to a specific Listener.
	 * 
	 * @param pListener
	 *            The listener to send the message to.
	 * @param pMessage
	 *            The message to send.
	 **/
	public static void SendMessage(IListener pListener, com.google.protobuf.Message pMessage)
	{
		pListener.ReceiveMessage(pMessage);
	}
	
	private Set<IListener> mListeners = Collections.synchronizedSet(new HashSet<IListener>());
}
