package com.zoltu.Vacuum.Messaging;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import android.os.Handler;
import android.util.Log;

/**
 * Implementation of a local listener (same process). Optimized such that the message is created in memory in the
 * sending thread and then read from memory in the receiving (this) thread. Also handles the dispatching of received
 * messages.
 **/
public class LocalListener implements IListener
{
	private final Object mProcessor;
	private final String mMethodName;
	private final Handler mHandler;
	
	/**
	 * This object will allow you to easily unpack and dispatch messages packed by a Broadcaster.
	 * 
	 * @param pProcessor
	 *            The object which contains the method overloads that will be called once the message is unpacked.
	 * @param pMethodName
	 *            The name of the overloaded method to call on pObject.
	 * @param pHandler
	 *            The handler that this listener receives messages on.
	 **/
	public LocalListener(Object pProcessor, String pMethodName, Handler pHandler)
	{
		mProcessor = pProcessor;
		mMethodName = pMethodName;
		mHandler = pHandler;
	}
	
	/**
	 * Given a Message built by a Broadcaster, this method will unpack the Message and call an overload of: (where
	 * pProcessor and pMethod are the values past into the Listener constructor)
	 * 
	 * <br/>
	 * <b>pProcessor.pMethodName(<i>[pMessage class]</i> <i>[pMessage unpacked]</i>)</b>
	 * 
	 * @param pHandlerMessage
	 *            A message that has been packaged by a Broadcaster.
	 * @return true if the message was dispatched, false if it was not (see System.err stream for details on why it
	 *         failed).
	 **/
	/* TODO: Investigate Messenger to see if we can do some fancier things with question / response. */
	public boolean DispatchMessage(android.os.Message pHandlerMessage)
	{
		// If anything goes wrong unpacking a message just drop the message and log the exception.
		try
		{
			/* If it's an empty message just call mProcessor.mMethodName(pMessage.what). */
			if (pHandlerMessage.obj == null && pHandlerMessage.peekData() == null)
			{
				DispatchObject(pHandlerMessage.what);
			}
			
			/* If this message has an object then call the appropriate overload for it (if it exists). */
			else if (pHandlerMessage.obj != null)
			{
				DispatchObject(pHandlerMessage.obj);
			}
			
			/* If we received a message with a bundle, unpack the bundle and process it. */
			else
			{
				// TODO: Unpack the bundle (which should contain a protocol buffer message) and turn it into an object.
				// TODO: DispatchObject(lObject);
				Log.w(this.getClass().getName(), "Messages with bundles not yet handled.");
				return false;
			}
		}
		catch (NoSuchMethodException lException)
		{
			Log.w(this.getClass().getName(), lException);
			return false;
		}
		catch (InvocationTargetException lException)
		{
			Log.w(this.getClass().getName(), lException);
			return false;
		}
		catch (IllegalAccessException lException)
		{
			Log.w(this.getClass().getName(), lException);
			return false;
		}
		
		return true;
	}
	
	private void DispatchObject(Object lObject) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
	{
		// Create the parameter list for the processing function.
		Class<?>[] lParameterTypes = new Class[]
		{ lObject.getClass() };
		// Look for a Process function that takes just an int parameter.
		Method lProcessMethod = mProcessor.getClass().getMethod(mMethodName, lParameterTypes);
		// Call the appropriate process method for the given object.
		lProcessMethod.invoke(mProcessor, lObject);
	}
	
	@Override public void ReceiveMessage(com.google.protobuf.Message pMessage)
	{
		android.os.Message lHandlerMessage = mHandler.obtainMessage(0, pMessage);
		mHandler.sendMessage(lHandlerMessage);
	}
}
