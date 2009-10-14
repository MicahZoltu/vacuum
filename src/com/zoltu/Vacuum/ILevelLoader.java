package com.zoltu.Vacuum;

import com.zoltu.Vacuum.Messages.Level.Map;
import com.zoltu.Vacuum.Messages.Level.Type;

/**
 * An interface for levels. LevelContext will load an implementation of this interface and use it to get information
 * about the level being played. The implementation may load a level from disc, from the Internet, from a multiplayer
 * server, or randomly generate one.
 **/
public interface ILevelLoader
{
	/**
	 * Get the currently loaded level.
	 * 
	 * @return The currently loaded level. Null if level is not loaded yet.
	 **/
	Map GetMap();
	
	/**
	 * Get the name of the level being loaded, or the name of the last level loaded if none is currently loaded, or an
	 * empty string if no level is being loaded and no previous level has been loaded.
	 * 
	 * @return The name of the level. This might be a URI, seed #, file name, etc.)
	 **/
	String GetLevelName();
	
	/**
	 * The type of level this loader loads.
	 * 
	 * @return The type of level that this level loader loads.
	 **/
	Type GetLevelType();
	
	/**
	 * Get the level name of the next level in a series. Examples of what this might do for various implementations are: Randomly
	 * generate and return a level seed, present the user with a load file dialog, get the name of the next level from a predetermined set of
	 * levels, request a new level name from the Internet, request a new level name from a game server, etc.
	 **/
	String GetNextLevelName();
	
	/**
	 * Load the specified level. This may be a seed for random levels, a URI for downloaded levels, a filename for
	 * levels off disc or a name for packaged levels.
	 **/
	Map LoadLevel(String pLevelName);
}
