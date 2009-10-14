package com.zoltu.Vacuum;

import java.util.Collection;
import java.util.HashMap;
import com.zoltu.Vacuum.Messages.Level;

/**
 * A map is a collection of rooms, doors, and pieces laid out on a grid. There should be rooms on all (even,even)
 * row,column coordinates (Location) and doors should be on all (even,odd) or (odd,even) Locations. Pieces can be
 * anywhere there is a room. No two rooms or doors should share a location and no two pieces should share a location.
 **/
public class Map
{
	/** The location of an object (room, door, piece) in a map. **/
	public static final class Location
	{
		private final int mRow;
		private final int mCol;
		private final int mHashCode;
		private final Level.Map.Location mLocationMessage;
		
		public Location(final int pRow, final int pCol)
		{
			mRow = pRow;
			mCol = pCol;
			mHashCode = (mRow * 17) ^ mCol;
			mLocationMessage = Level.Map.Location.newBuilder().setRow(mRow).setCol(mCol).build();
		}
		
		public Location(final Level.Map.Location pLocationMessage)
		{
			mRow = pLocationMessage.getRow();
			mCol = pLocationMessage.getCol();
			mHashCode = (mRow * 17) ^ mCol;
			mLocationMessage = pLocationMessage;
		}
		
		/** Build a protocol buffers message for this location. **/
		public Level.Map.Location Build()
		{
			return mLocationMessage;
		}
		
		@Override public boolean equals(Object pOther)
		{
			if (pOther == this) return true;
			if (pOther == null) return false;
			if (!(pOther instanceof Location)) return false;
			final Location lOther = (Location) pOther;
			if (lOther.mRow != mRow) return false;
			if (lOther.mCol != mCol) return false;
			
			return true;
		}
		
		@Override public int hashCode()
		{
			return mHashCode;
		}
		
		public int Row()
		{
			return mRow;
		}
		
		public int Col()
		{
			return mCol;
		}
	}
	
	/** A room is an object at an (even,even) location that can be a plain room or a vacuum. **/
	public static class Room
	{
		private final Location mLocation;
		private final Door mLeftDoor;
		private final Door mTopDoor;
		private final Door mRightDoor;
		private final Door mBottomDoor;
		private Level.Map.Room.Type mType;
		
		public Room(final Level.Map.Room pRoom, final java.util.Map<Location, Door> pDoorMap) throws InvalidRoomLocationException
		{
			mLocation = new Location(pRoom.getLocation());
			if (mLocation.mRow % 2 != 0 || mLocation.mCol % 2 != 0) throw new InvalidRoomLocationException(mLocation);
			
			mLeftDoor = pDoorMap.get(new Location(mLocation.Row(), mLocation.Col() - 1));
			mTopDoor = pDoorMap.get(new Location(mLocation.Row() - 1, mLocation.Col()));
			mRightDoor = pDoorMap.get(new Location(mLocation.Row(), mLocation.Col() + 1));
			mBottomDoor = pDoorMap.get(new Location(mLocation.Row() + 1, mLocation.Col()));
			mType = pRoom.getType();
		}
		
		public Room(final Location pLocation, final Door pLeftDoor, final Door pTopDoor, final Door pRightDoor, final Door pBottomDoor) throws InvalidRoomLocationException
		{
			mLocation = pLocation;
			if (mLocation.mRow % 2 != 0 || mLocation.mCol % 2 != 0) throw new InvalidRoomLocationException(mLocation);
			
			mLeftDoor = pLeftDoor;
			mTopDoor = pTopDoor;
			mRightDoor = pRightDoor;
			mBottomDoor = pBottomDoor;
			mType = Level.Map.Room.Type.VACUUM;
		}
		
		/** Build a protocol buffers message for this room. **/
		public Level.Map.Room Build()
		{
			Level.Map.Room.Builder lBuilder = Level.Map.Room.newBuilder();
			lBuilder.setLocation(mLocation.Build());
			lBuilder.setType(mType);
			return lBuilder.build();
		}
		
		public Location GetLocation()
		{
			return mLocation;
		}
		
		public Level.Map.Room.Type GetType()
		{
			return mType;
		}
		
		public Room SetType(Level.Map.Room.Type pType)
		{
			mType = pType;
			
			return this;
		}
		
		public Door GetLeftDoor()
		{
			return mLeftDoor;
		}
		
		public Door GetTopDoor()
		{
			return mTopDoor;
		}
		
		public Door GetRightDoor()
		{
			return mRightDoor;
		}
		
		public Door GetBottomDoor()
		{
			return mBottomDoor;
		}
	}
	
	/**
	 * A door is an object at an (even,odd) or (odd,even) location that can be a colored door, a wall, an opening, or a
	 * one-way opening.
	 **/
	public static class Door
	{
		private final Location mLocation;
		private Level.Map.Door.Type mType;
		private Level.Map.Door.State mState;
		
		public Door(Level.Map.Door pDoor) throws InvalidDoorLocationException
		{
			mLocation = new Location(pDoor.getLocation());
			if ((mLocation.mRow % 2 == 0 && mLocation.mCol % 2 == 0) || (mLocation.mRow % 2 != 0 && mLocation.mCol % 2 != 0)) throw new InvalidDoorLocationException(mLocation);
			
			mType = pDoor.getType();
			mState = pDoor.getState();
		}
		
		public Door(Location pLocation) throws InvalidDoorLocationException
		{
			mLocation = pLocation;
			if ((mLocation.mRow % 2 == 0 && mLocation.mCol % 2 == 0) || (mLocation.mRow % 2 != 0 && mLocation.mCol % 2 != 0)) throw new InvalidDoorLocationException(mLocation);
			
			mType = Level.Map.Door.Type.OPENING;
			mState = Level.Map.Door.State.CLOSED;
		}
		
		/** Build a protocol buffers message for this door. **/
		public Level.Map.Door Build()
		{
			Level.Map.Door.Builder lBuilder = Level.Map.Door.newBuilder();
			lBuilder.setLocation(mLocation.Build());
			lBuilder.setType(mType);
			lBuilder.setState(mState);
			return lBuilder.build();
		}
		
		public Location GetLocation()
		{
			return mLocation;
		}
		
		public Level.Map.Door.Type GetType()
		{
			return mType;
		}
		
		public Level.Map.Door.State GetState()
		{
			return mState;
		}
		
		public Door SetType(Level.Map.Door.Type pType)
		{
			mType = pType;
			
			return this;
		}
		
		public Door SetState(Level.Map.Door.State pState)
		{
			mState = pState;
			
			return this;
		}
	}
	
	/** A piece is a game object that can move around the board. **/
	public static class Piece
	{
		private final int mId;
		private Location mLocation;
		private final Level.Map.Piece.Team mTeam;
		
		public Piece(Level.Map.Piece pPiece)
		{
			mId = pPiece.getId();
			mLocation = new Location(pPiece.getLocation());
			mTeam = pPiece.getTeam();
		}
		
		public Piece(int pId, Location pLocation, Level.Map.Piece.Team pTeam)
		{
			mId = pId;
			mLocation = pLocation;
			mTeam = pTeam;
		}
		
		public Level.Map.Piece Build()
		{
			Level.Map.Piece.Builder lBuilder = Level.Map.Piece.newBuilder();
			lBuilder.setId(mId);
			lBuilder.setLocation(mLocation.Build());
			lBuilder.setTeam(mTeam);
			return lBuilder.build();
		}
		
		public Location GetLocation()
		{
			return mLocation;
		}
		
		public int GetId()
		{
			return mId;
		}
		
		public Level.Map.Piece.Team GetTeam()
		{
			return mTeam;
		}
	}
	
	private final int mRowCount;
	private final int mColCount;
	private final java.util.Map<Location, Room> mRoomMap;
	private final java.util.Map<Location, Door> mDoorMap;
	private final java.util.Map<Integer, Piece> mPieceMap;
	
	/**
	 * Construct a Map from a protocol buffers message.
	 * 
	 * @param pMap
	 *            A com.zoltu.Vacuum.Messages.Level.Map protocol buffers message.
	 * @throws InvalidMapException
	 *             See {@link com.zoltu.Vacuum.Map#Validate(com.zoltu.Vacuum.Messages.Level.Map)} .
	 **/
	public Map(Level.Map pMap) throws InvalidRoomLocationException, DuplicateRoomException, MissingRoomException, InvalidDoorLocationException, DuplicateDoorException, MissingDoorException
	{
		Validate(pMap);
		
		mRowCount = pMap.getRowCount();
		mColCount = pMap.getColCount();
		mRoomMap = new HashMap<Location, Room>();
		mDoorMap = new HashMap<Location, Door>();
		mPieceMap = new HashMap<Integer, Piece>();
		
		for (Level.Map.Door lDoorMessage : pMap.getDoorList())
		{
			Door lDoor = new Door(lDoorMessage);
			mDoorMap.put(lDoor.GetLocation(), lDoor);
		}
		
		for (Level.Map.Room lRoomMessage : pMap.getRoomList())
		{
			Room lRoom = new Room(lRoomMessage, mDoorMap);
			mRoomMap.put(lRoom.GetLocation(), lRoom);
		}
		
		for (Level.Map.Piece lPieceMessage : pMap.getPieceList())
		{
			Piece lPiece = new Piece(lPieceMessage);
			mPieceMap.put(lPiece.GetId(), lPiece);
		}
	}
	
	/**
	 * Construct a default map with a specific size (the rooms/doors can be changed later). Note that the number of
	 * doors in a map is {@code ((rows-1)/2 + (cols-2)/2)} and the number of doors in a map is {@code ((rows+1)/2 +
	 * (cols+1)/2)}.
	 * 
	 * @param pRowCount
	 *            The number of rows in the new map. Must be an odd number >= 3.
	 * @param pColCount
	 *            The number of columns in the new map. Must be an odd number >= 3.
	 * @throws InvalidMapSizeException
	 *             If you supply an odd number or a number less than 3 for pRowCount or pColCount.
	 **/
	public Map(int pRowCount, int pColCount) throws InvalidMapSizeException
	{
		if (pRowCount % 2 == 0 || pColCount % 2 == 0 || pRowCount < 3 || pColCount < 3)
		{
			throw new InvalidMapSizeException(pRowCount, pColCount);
		}
		
		mRowCount = pRowCount;
		mColCount = pColCount;
		mRoomMap = new HashMap<Location, Room>();
		mDoorMap = new HashMap<Location, Door>();
		mPieceMap = new HashMap<Integer, Piece>();
		
		for (int lRow = 0; lRow < mRowCount; lRow += 2)
		{
			for (int lCol = 0; lCol < mColCount; lCol += 2)
			{
				/* Edge rows/columns have null doors along the edge. */
				Door lLeftDoor = null;
				Door lTopDoor = null;
				Door lRightDoor = null;
				Door lBottomDoor = null;
				/*
				 * Since we generate the map from left to right, left doors will already exist (just need to point to
				 * them).
				 */
				if (lCol != 0)
				{
					lLeftDoor = mDoorMap.get(new Location(lRow, lCol - 1));
				}
				/*
				 * Since we generate the map from top to bottom, top doors will already exist (just need to point to
				 * them).
				 */
				if (lRow != 0)
				{
					lTopDoor = mDoorMap.get(new Location(lRow - 1, lCol));
				}
				/* For all but the last column of rooms we generate a door to the right. */
				if (lCol < mColCount - 1)
				{
					lRightDoor = new Door(new Location(lRow, lCol + 1));
					mDoorMap.put(lRightDoor.GetLocation(), lRightDoor);
				}
				/* For all but the last row of rooms we generate a door below. */
				if (lRow < mRowCount - 1)
				{
					lBottomDoor = new Door(new Location(lRow + 1, lCol));
					mDoorMap.put(lBottomDoor.GetLocation(), lBottomDoor);
				}
				
				/* Now that all the doors are created we can create the room pointing at it's doors (or null doors). */
				Room lRoom = new Room(new Location(lRow, lCol), lLeftDoor, lTopDoor, lRightDoor, lBottomDoor);
				
				/* Put the room into our room mapped by location. */
				mRoomMap.put(lRoom.GetLocation(), lRoom);
			}
		}
	}
	
	/** Build a protocol buffer message for this map. **/
	public Level.Map Build()
	{
		Level.Map.Builder lBuilder = Level.Map.newBuilder();
		lBuilder.setRowCount(mRowCount);
		lBuilder.setColCount(mColCount);
		for (Room lRoom : mRoomMap.values())
		{
			lBuilder.addRoom(lRoom.Build());
		}
		for (Door lDoor : mDoorMap.values())
		{
			lBuilder.addDoor(lDoor.Build());
		}
		for (Piece lPiece : mPieceMap.values())
		{
			lBuilder.addPiece(lPiece.Build());
		}
		return lBuilder.build();
	}
	
	/**
	 * Gets the number of rows in this map.
	 * 
	 * @return The number of rows in this map. n rows means {@code (n-1)/2} rooms and {@code (n+1)/2} doors in a column.
	 **/
	public int GetRowCount()
	{
		return mRowCount;
	}
	
	/**
	 * Gets the number of columns in this map.
	 * 
	 * @return The number of columns in this map. n columns means {@code (n-1)/2} rooms and {@code (n+1)/2} doors in a
	 *         row.
	 **/
	public int GetColCount()
	{
		return mColCount;
	}
	
	/**
	 * Gets the room at the specified location.
	 * 
	 * @param pLocation
	 *            The location of the room to return.
	 * @return The room at pLocation.
	 * @throws InvalidRoomLocationException
	 *             If no room exists at pLocation.
	 **/
	public Room GetRoom(Location pLocation) throws InvalidRoomLocationException
	{
		final Room lRoom = mRoomMap.get(pLocation);
		if (lRoom == null) throw new InvalidRoomLocationException(pLocation);
		return lRoom;
	}
	
	/**
	 * Gets the door at the specified location.
	 * 
	 * @param pLocation
	 *            The location of the door to return.
	 * @return The door at pLocation.
	 * @throws InvalidDoorLocationException
	 *             If no door exists at pLocation.
	 **/
	public Door GetDoor(Location pLocation) throws InvalidDoorLocationException
	{
		final Door lDoor = mDoorMap.get(pLocation);
		if (lDoor == null) throw new InvalidDoorLocationException(pLocation);
		return lDoor;
	}
	
	/**
	 * Gets the piece at the specified location.
	 * 
	 * @param pId
	 *            The ID of the piece to return.
	 * @return The piece with ID pId or null if no piece with that ID exists.
	 **/
	public Piece GetPiece(int pId)
	{
		return mPieceMap.get(pId);
	}
	
	/**
	 * Removes a piece from the level.
	 * 
	 * @param pId
	 *            The ID of the piece to remove from the map.
	 **/
	public void RemovePiece(int pId)
	{
		mPieceMap.remove(pId);
	}
	
	/**
	 * Adds a piece to the level.
	 * 
	 * @param pPiece
	 *            The Piece to add to the map.
	 **/
	public void AddPiece(Piece pPiece)
	{
		mPieceMap.put(pPiece.GetId(), pPiece);
	}
	
	/**
	 * Gets a collection of all rooms in the map, useful for looping through them all.
	 * 
	 * @return An iterable collection of rooms.
	 **/
	public Collection<Room> GetRoomCollection()
	{
		return mRoomMap.values();
	}
	
	/**
	 * Gets a collection of all doors in the map, useful for looping through them all.
	 * 
	 * @return An iterable collection of doors.
	 **/
	public Collection<Door> GetDoorCollection()
	{
		return mDoorMap.values();
	}
	
	/**
	 * Gets a collection of all pieces in the map, useful for looping through them all.
	 * 
	 * @return An iterable collection of pieces.
	 **/
	public Collection<Piece> GetPieceCollection()
	{
		return mPieceMap.values();
	}
	
	/**
	 * Validates a Map message to ensure that it is a valid map.
	 * 
	 * @param pMap
	 *            The com.zoltu.Vacuum.Messages.Level.Map to validate.
	 * @throws InvalidRoomLocationException
	 *             If a room has a location that is out of bounds or falls on an odd row or column.
	 * @throws DuplicateRoomException
	 *             If two rooms have the same location.
	 * @throws MissingRoomException
	 *             If there is a valid room location without a room.
	 * @throws InvalidDoorLocationException
	 *             If a door has a location that is out of bounds or falls on an (even,even) or (odd,odd) location.
	 * @throws DuplicateDoorException
	 *             If two doors have the same location.
	 * @throws MissingDoorException
	 *             If there is a valid door location without a door.
	 * @throws InvalidPieceLocationException
	 *             If a piece has a location that is out of bounds or falls on an odd row or column.
	 * @throws DuplicatePieceException
	 *             If two pieces have the same location.
	 * **/
	public static void Validate(Level.Map pMap) throws InvalidRoomLocationException, DuplicateRoomException, MissingRoomException, InvalidDoorLocationException, DuplicateDoorException, MissingDoorException, InvalidPieceLocationException, DuplicatePieceException
	{
		final int lRowCount = pMap.getRowCount();
		final int lColCount = pMap.getColCount();
		
		/*
		 * Make sure all of the rooms are in the range ([0,row_count), [0,col_count)), are only on odd row/columns and
		 * every odd (row,col) has a room.
		 */
		Level.Map.Room lRooms[][] = new Level.Map.Room[lRowCount][lColCount];
		for (Level.Map.Room lRoom : pMap.getRoomList())
		{
			int lRow = lRoom.getLocation().getRow();
			int lCol = lRoom.getLocation().getCol();
			if (lRow < 0 || lRow >= lRowCount) throw new InvalidRoomLocationException(lRow, lCol);
			if (lCol < 0 || lCol >= lColCount) throw new InvalidRoomLocationException(lRow, lCol);
			if (lRow % 2 != 0 || lCol % 2 != 0) throw new InvalidRoomLocationException(lRow, lCol);
			if (lRooms[lRow][lCol] != null) throw new DuplicateRoomException(lRow, lCol);
			lRooms[lRow][lCol] = lRoom;
		}
		for (int lRow = 0; lRow < lRowCount; lRow += 2)
		{
			for (int lCol = 0; lCol < lColCount; lCol += 2)
			{
				if (lRooms[lRow][lCol] == null) throw new MissingRoomException(lRow, lCol);
			}
		}
		
		/*
		 * Make sure all of the doors are in the range ([0,row_count), [0,col_count)), are only on odd,even coordinate
		 * pairs and every odd,even coordinate pair has a room.
		 */
		Level.Map.Door lDoors[][] = new Level.Map.Door[lRowCount][lColCount];
		for (Level.Map.Door lDoor : pMap.getDoorList())
		{
			int lRow = lDoor.getLocation().getRow();
			int lCol = lDoor.getLocation().getCol();
			if (lRow < 0 || lRow >= lRowCount) throw new InvalidDoorLocationException(lRow, lCol);
			if (lCol < 0 || lCol >= lColCount) throw new InvalidDoorLocationException(lRow, lCol);
			if (lRow % 2 == 0 && lCol % 2 == 0) throw new InvalidDoorLocationException(lRow, lCol);
			if (lRow % 2 != 0 && lCol % 2 != 0) throw new InvalidDoorLocationException(lRow, lCol);
			if (lDoors[lRow][lCol] != null) throw new DuplicateDoorException(lRow, lCol);
			lDoors[lRow][lCol] = lDoor;
		}
		for (int lRow = 0; lRow < lRowCount; ++lRow)
		{
			for (int lCol = 0; lCol < lColCount; ++lCol)
			{
				if (lRow % 2 == 0 && lCol % 2 == 0) continue;
				if (lRow % 2 != 0 && lCol % 2 != 0) continue;
				if (lDoors[lRow][lCol] == null) throw new MissingDoorException(lRow, lCol);
			}
		}
		/* Make sure all of the pieces are in the range ([0,row_count), [0,col_count)) and are only on odd row/columns. */
		Level.Map.Piece lPieces[][] = new Level.Map.Piece[lRowCount][lColCount];
		for (Level.Map.Piece lPiece : pMap.getPieceList())
		{
			int lRow = lPiece.getLocation().getRow();
			int lCol = lPiece.getLocation().getCol();
			if (lRow < 0 || lRow >= lRowCount) throw new InvalidPieceLocationException(lRow, lCol);
			if (lCol < 0 || lCol >= lColCount) throw new InvalidPieceLocationException(lRow, lCol);
			if (lRow % 2 != 0 || lCol % 2 != 0) throw new InvalidPieceLocationException(lRow, lCol);
			if (lPieces[lRow][lCol] != null) throw new DuplicatePieceException(lRow, lCol);
			lPieces[lRow][lCol] = lPiece;
		}
	}
	
	enum Side
	{
		LEFT,
		TOP,
		RIGHT,
		BOTTOM,
	}
	
	public abstract static class InvalidMapException extends RuntimeException
	{
		private static final long serialVersionUID = 722542843736084887L;
	}
	
	public static class InvalidMapSizeException extends InvalidMapException
	{
		private static final long serialVersionUID = 3359951551637844285L;
		
		public final int mRowCount;
		public final int mColCount;
		
		public InvalidMapSizeException(final int pRowCount, final int pColCount)
		{
			mRowCount = pRowCount;
			mColCount = pColCount;
		}
	}
	
	public static class InvalidRoomLocationException extends InvalidMapException
	{
		private static final long serialVersionUID = 5504196912366956798L;
		
		public final Location mLocation;
		
		public InvalidRoomLocationException(final int pRow, final int pCol)
		{
			mLocation = new Location(pRow, pCol);
		}
		
		public InvalidRoomLocationException(final Location pLocation)
		{
			mLocation = pLocation;
		}
	}
	
	public static class DuplicateRoomException extends InvalidMapException
	{
		private static final long serialVersionUID = 2205197478095866092L;
		
		public final int mRow;
		public final int mCol;
		
		public DuplicateRoomException(final int pRow, final int pCol)
		{
			mRow = pRow;
			mCol = pCol;
		}
	}
	
	public static class MissingRoomException extends InvalidMapException
	{
		private static final long serialVersionUID = 2567441135894457586L;
		
		public final int mRow;
		public final int mCol;
		
		public MissingRoomException(final int pRow, final int pCol)
		{
			mRow = pRow;
			mCol = pCol;
		}
	}
	
	public static class InvalidDoorLocationException extends InvalidMapException
	{
		private static final long serialVersionUID = 1969681357437262518L;
		
		public final Location mLocation;
		
		public InvalidDoorLocationException(final int pRow, final int pCol)
		{
			mLocation = new Location(pRow, pCol);
		}
		
		public InvalidDoorLocationException(final Location pLocation)
		{
			mLocation = pLocation;
		}
	}
	
	public static class DuplicateDoorException extends InvalidMapException
	{
		private static final long serialVersionUID = 5139161965307123743L;
		
		public final int mRow;
		public final int mCol;
		
		public DuplicateDoorException(final int pRow, final int pCol)
		{
			mRow = pRow;
			mCol = pCol;
		}
	}
	
	public static class MissingDoorException extends InvalidMapException
	{
		private static final long serialVersionUID = -6528092139882070028L;
		
		public final int mRow;
		public final int mCol;
		
		public MissingDoorException(final int pRow, final int pCol)
		{
			mRow = pRow;
			mCol = pCol;
		}
	}
	
	public static class InvalidPieceLocationException extends InvalidMapException
	{
		private static final long serialVersionUID = 5504196912366956798L;
		
		public final Location mLocation;
		
		public InvalidPieceLocationException(final int pRow, final int pCol)
		{
			mLocation = new Location(pRow, pCol);
		}
		
		public InvalidPieceLocationException(final Location pLocation)
		{
			mLocation = pLocation;
		}
	}
	
	public static class DuplicatePieceException extends InvalidMapException
	{
		private static final long serialVersionUID = 2205197478095866092L;
		
		public final int mRow;
		public final int mCol;
		
		public DuplicatePieceException(final int pRow, final int pCol)
		{
			mRow = pRow;
			mCol = pCol;
		}
	}
}
