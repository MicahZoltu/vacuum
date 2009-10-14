package com.zoltu.Vacuum;

import android.graphics.Point;
import com.zoltu.Vacuum.Messages.Level.Piece;
import com.zoltu.Vacuum.Messages.Level.Map.Room;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door;

/**
 * Interface for a SurfaceRenderer. An implementation of this is used by the GraphicsContext to determine exactly how to
 * draw things.
 **/
public interface Renderer
{
	/**
	 * Called by the GraphicsContext to see if it should skip attempting to render anything at this time. Note that this
	 * is just a hint to the GraphicsContext, do not rely on returning false from this to not generate rendering calls.
	 * 
	 * @return true if this Renderer is in a state that it can do some drawing, false if rendering calls should be
	 *         avoided for optimization.
	 **/
	public boolean CanRender();
	
	/**
	 * Implementations of this interface should render one frame of the game when this function is called.
	 **/
	public void RenderScene();
	
	/**
	 * Notification that a room has been added to the scene.
	 * 
	 * @param pRoom
	 *            The newly added room.
	 * @param pLocation
	 *            The location of the new room.
	 **/
	public void AddRoom(final Room pRoom, final Point pLocation);
	
	/**
	 * Notification that a door has been added to the scene.
	 * 
	 * @param pDoor
	 *            The newly added door.
	 * @param pLocation
	 *            The location of the new door.
	 * @param pOrientation
	 *            The orientation of the door.
	 **/
	public void AddDoor(final Door pDoor, final Point pLocation, final Door.Orientation pOrientation);
	
	/**
	 * Notification that a piece has been added to the scene.
	 * 
	 * @param pPiece
	 *            The newly added piece.
	 * @param pLocation
	 *            The location of the new piece.
	 **/
	public void AddPiece(final Piece pPiece, final Point pLocation);
	
	/**
	 * Notification that a door type has been toggled.
	 * 
	 * @param pType
	 *            The type of door that has been toggled.
	 * @param pStateFlipped
	 *            False if the door should be in it's default state (open/closed), true if it should be the opposite.
	 **/
	public void ToggleDoor(final Door.Type pType, final boolean pStateFlipped);
	
	/**
	 * Notification that a piece has moved.
	 * 
	 * @param pPiece
	 *            The piece that has been moved.
	 * @param pLocation
	 *            The location the piece has moved to.
	 **/
	public void MovePiece(final Piece pPiece, final Point pLocation);
}
