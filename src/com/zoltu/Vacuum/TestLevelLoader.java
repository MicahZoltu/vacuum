package com.zoltu.Vacuum;

import com.zoltu.Vacuum.Messages.Level;

/** LevelLoader that loads a test-level from memory. **/
public class TestLevelLoader implements ILevelLoader
{
	private final Level.Map mMap;
	private final String mName = "Test";
	
	public TestLevelLoader()
	{
		Map.Room lRoom;
		
		/* Generate our test level. */
		Map lMap = new Map(5, 3);
		
		// First row.
		// First room is a vacuum with an opening to all sides (default room).
		lRoom = lMap.GetRoom(new Map.Location(0, 0));
		lRoom.SetType(Level.Map.Room.Type.VACUUM);
		lRoom.GetRightDoor().SetType(Level.Map.Door.Type.OPENING);
		lRoom.GetBottomDoor().SetType(Level.Map.Door.Type.GREEN).SetState(Level.Map.Door.State.OPEN);
		// Second room is a vacuum with a wall to the bottom, open everywhere else.
		lRoom = lMap.GetRoom(new Map.Location(0, 2));
		lRoom.SetType(Level.Map.Room.Type.VACUUM);
		lRoom.GetBottomDoor().SetType(Level.Map.Door.Type.WALL);
		
		// Second row.
		// First room has a wall to the left, a blue door to the right and a green open door to the bottom.
		lRoom = lMap.GetRoom(new Map.Location(2, 0));
		lRoom.SetType(Level.Map.Room.Type.ROOM);
		lRoom.GetRightDoor().SetType(Level.Map.Door.Type.BLUE).SetState(Level.Map.Door.State.CLOSED);
		lRoom.GetBottomDoor().SetType(Level.Map.Door.Type.GREEN).SetState(Level.Map.Door.State.CLOSED);
		// Second room has an opening to the bottom.
		lRoom = lMap.GetRoom(new Map.Location(2, 2));
		lRoom.SetType(Level.Map.Room.Type.ROOM);
		lRoom.GetBottomDoor().SetType(Level.Map.Door.Type.OPENING);
		
		// Third row.
		// First room has a wall to the left, right and bottom and an evil piece in it.
		lRoom = lMap.GetRoom(new Map.Location(4, 0));
		lRoom.SetType(Level.Map.Room.Type.ROOM);
		lRoom.GetRightDoor().SetType(Level.Map.Door.Type.WALL);
		lMap.AddPiece(new Map.Piece(1, new Map.Location(4, 0), Level.Map.Piece.Team.EVIL));
		
		// Second room has a good piece in it.
		lRoom = lMap.GetRoom(new Map.Location(4, 2));
		lRoom.SetType(Level.Map.Room.Type.ROOM);
		lMap.AddPiece(new Map.Piece(2, new Map.Location(4, 2), Level.Map.Piece.Team.GOOD));
		
		/* Build the map object. */
		mMap = lMap.Build();
	}
	
	@Override public Level.Map GetMap()
	{
		return mMap;
	}
	
	@Override public String GetLevelName()
	{
		return mName;
	}
	
	@Override public Level.Type GetLevelType()
	{
		return Level.Type.CAMPAIGN;
	}
	
	@Override public String GetNextLevelName()
	{
		return mName;
	}
	
	@Override public Level.Map LoadLevel(String pLevelName)
	{
		return mMap;
	}
}
