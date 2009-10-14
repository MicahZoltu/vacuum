package com.zoltu.Vacuum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.os.Looper;
import android.util.Log;
import com.zoltu.Vacuum.Messages.Level;
import com.zoltu.Vacuum.Messaging.MessagingContext;
import com.zoltu.Vacuum.View.DoorView;
import com.zoltu.Vacuum.View.DoorViewDoor;
import com.zoltu.Vacuum.View.DoorViewOpening;
import com.zoltu.Vacuum.View.DoorViewWall;
import com.zoltu.Vacuum.View.PieceView;
import com.zoltu.Vacuum.View.RoomView;

public class ViewRendererContext extends MessagingContext
{
	private final ViewGroupRenderer mViewGroup;
	private final Context mContext;
	private Map mMap;
	private java.util.Map<Level.Map.Door.Type, List<DoorView>> mDoorsByType;
	private java.util.Map<Integer, PieceView> mPiecesById;
	
	public ViewRendererContext(Context pContext, Looper pLooper, ViewGroupRenderer pViewGroup)
	{
		super(pLooper, "ProcessMessage");
		
		mViewGroup = pViewGroup;
		mContext = pContext;
		
		/* Create the door maps. */
		mDoorsByType = new HashMap<Level.Map.Door.Type, List<DoorView>>();
		for (Level.Map.Door.Type lType : Level.Map.Door.Type.values())
		{
			mDoorsByType.put(lType, new ArrayList<DoorView>());
		}
		
		/* Create the piece map. */
		mPiecesById = new HashMap<Integer, PieceView>();
	}
	
	/** Received when a level has finished loading and we should start rendering it. **/
	public void ProcessMessage(Level.LoadedAnnouncement pAnnouncement)
	{
		try
		{
			/* Construct an in-memory Map from the map we received. */
			mMap = new Map(pAnnouncement.getMap());
		}
		catch (Map.InvalidMapException lException)
		{
			Log.w(this.getClass().getName(), lException);
			return;
		}
		
		/* Tell the ViewGroup about the map. */
		mViewGroup.SetMap(mMap);
		
		/* Add the rooms to the ViewGroup as separate views. */
		for (Map.Room lRoom : mMap.GetRoomCollection())
		{
			mViewGroup.AddRoom(new RoomView(mContext, lRoom));
		}
		
		/* Add the doors to the ViewGroup and store them in collections by type. */
		for (Map.Door lDoor : mMap.GetDoorCollection())
		{
			DoorView lDoorView;
			switch (lDoor.GetType())
			{
				case OPENING:
				{
					lDoorView = new DoorViewOpening(mContext, lDoor);
					break;
				}
				case WALL:
				{
					lDoorView = new DoorViewWall(mContext, lDoor);
					break;
				}
				case BLUE:
				case CYAN:
				case GREEN:
				case MAGENTA:
				case RED:
				case YELLOW:
				{
					lDoorView = new DoorViewDoor(mContext, lDoor);
					break;
				}
				case ENTRANCE:
				case EXIT:
				{
					// TODO: Handle entrances and exits.
					throw new IllegalArgumentException("Entrances and Exits not handled yet.");
				}
				default:
				{
					throw new IllegalArgumentException("Unhandled case in door type switch statement.");
				}
			}
			mViewGroup.AddDoor(lDoorView);
			mDoorsByType.get(lDoor.GetType()).add(lDoorView);
		}
		
		/* Add the pieces to the view group and store them in a collection keyed by ID. */
		for (Map.Piece lPiece : mMap.GetPieceCollection())
		{
			PieceView lPieceView = new PieceView(mContext, lPiece);
			mViewGroup.AddPiece(lPieceView);
			mPiecesById.put(lPiece.GetId(), lPieceView);
		}
	}
}
