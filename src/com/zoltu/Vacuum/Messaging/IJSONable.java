package com.zoltu.Vacuum.Messaging;

import org.json.JSONObject;

/**
 * Any object that needs to be sent between processes or over the network needs to implement this interface. The class
 * that implements this interface <b>MUST have a constructor that takes in a JSONObject output by ToJSON()</b>.
 **/
public interface IJSONable
{
	/** The implementation of this function should serialize the entire object into a JSONObject and return it. **/
	public JSONObject ToJSON();
	
	/**
	 * Calling this function should return a copy of the object. This is used in cases where we don't need to send the
	 * object over a network or store it on disc and we don't want the overhead of converting it to a string and back
	 * just to get a copy in another thread.
	 **/
	public IJSONable Copy();
}
