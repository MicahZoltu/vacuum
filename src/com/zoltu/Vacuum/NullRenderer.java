package com.zoltu.Vacuum;

import java.util.List;
import android.graphics.Point;
import com.zoltu.Vacuum.Messages.Level.Piece;
import com.zoltu.Vacuum.Messages.Level.Map.Room;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door.Orientation;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door.Type;

public class NullRenderer implements Renderer
{
	@Override public boolean CanRender()
	{
		return false;
	}

	@Override public void AddDoor(Door pDoor, Point pLocation, Orientation pOrientation)
	{
		// Do nothing.
	}

	@Override public void AddPiece(Piece pPiece, Point pLocation)
	{
		// Do nothing.
	}

	@Override public void AddRoom(Room pRoom, Point pLocation)
	{
		// Do nothing.
	}

	@Override public void MovePiece(Piece pPiece, Point pLocation)
	{
		// Do nothing.
	}

	@Override public void RenderScene()
	{
		// Do nothing.
	}

	@Override public void ToggleDoor(Type pType, boolean pStateFlipped)
	{
		// Do nothing.
	}
}
