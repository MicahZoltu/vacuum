package com.zoltu.Vacuum.Messaging;

public interface IBroadcaster
{	
	/**
	 * Add the provided Handler to a list of handlers (listeners) interested in
	 * getting notified of interesting things happening with this level loader.
	 * 
	 * @param pHandler
	 *            The handler to be added to the list of listeners.
	 **/
	void AddListener(IListener pListener);
	
	/**
	 * Remove the provided Handler from the list of handlers (listeners).
	 * 
	 * @param pHandler
	 *            The handler you wish to remove from the listener list.
	 **/
	void RemoveListener(IListener pListener);
}
