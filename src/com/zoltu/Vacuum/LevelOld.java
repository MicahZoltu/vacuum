package com.zoltu.Vacuum;

import java.util.HashSet;
import java.util.Set;

/** Class that defines a level. **/
@Deprecated public class LevelOld
{
	public enum Colors
	{
		RED, GREEN, BLUE, OPENING, WALL,
	}
	
	public static class Room
	{
		private static final long serialVersionUID = 1L;
		
		/** Flag to indicate this room is a vacuum. **/
		public volatile boolean mVacuum = true;
		
		/** Doors (order: North, East, South, West). **/
		public final Door[] mDoors = new Door[4];
	}
	
	public static class Door
	{
		private static final long serialVersionUID = 1L;
		
		/** Color of the door (or wall/opening). **/
		public volatile Colors mColor = Colors.WALL;
		
		/** Default state of the door (true=open, false=closed). **/
		public volatile boolean mOpen = false;
		
		/** The two rooms connected to this door. **/
		public final Room[] mRooms = new Room[2];
		
		public Door(final Room pRoom1, final Room pRoom2)
		{
			mRooms[0] = pRoom1;
			mRooms[1] = pRoom2;
		}
	}
	
	public enum Team
	{
		GOOD, EVIL,
	}
	
	public static class Piece
	{
		private static final long serialVersionUID = 1L;
		
		/** The room this Piece is currently in. **/
		public volatile Room mRoom;
		/** The team this Piece is on. **/
		public volatile Team mTeam;
		
		public Piece(final Room pRoom, final Team pTeam)
		{
			mRoom = pRoom;
			mTeam = pTeam;
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	private final String mLevelName;
	public final Room[][] mRooms;
	public final Set<Piece> mPieces;
	
	public LevelOld(final String pLevelName, final int pRoomColumnCount, final int pRoomRowCount)
	{
		mLevelName = pLevelName;
		
		/* Create the room array. */
		mRooms = new Room[pRoomColumnCount][pRoomRowCount];
		
		/* Fill the array with default rooms (all vacuum rooms) and doors connecting them. */
		for (int i = 0; i < pRoomColumnCount; ++i)
		{
			for (int j = 0; j < pRoomRowCount; ++i)
			{
				/* Create the room. */
				Room lRoom = new Room();
				/* Put the room into the rooms array. */
				mRooms[i][j] = lRoom;
				/* Create doors to other adjacent rooms already created. */
				if (j != 0)
				{
					Room lRoomUp = mRooms[i][j - 1];
					lRoom.mDoors[0] = lRoomUp.mDoors[2] = new Door(lRoom, lRoomUp);
				}
				if (i != 0)
				{
					Room lRoomLeft = mRooms[i - 1][j];
					lRoom.mDoors[3] = lRoomLeft.mDoors[1] = new Door(lRoom, lRoomLeft);
				}
			}
		}
		
		/* Create the array of pieces. */
		mPieces = new HashSet<Piece>();
	}
	
	public String GetLevelName()
	{
		return mLevelName;
	}
}
