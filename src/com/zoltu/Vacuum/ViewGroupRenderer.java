package com.zoltu.Vacuum;

import java.util.ArrayList;
import java.util.List;
import com.zoltu.Vacuum.View.DoorView;
import com.zoltu.Vacuum.View.PieceView;
import com.zoltu.Vacuum.View.RoomView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ViewGroupRenderer extends ViewGroup
{
	private List<RoomView> mRooms;
	private List<DoorView> mDoors;
	private List<PieceView> mPieces;
	private Map mMap;
	
	public ViewGroupRenderer(Context pContext, AttributeSet pAttributeSet)
	{
		super(pContext, pAttributeSet);
		
		mRooms = new ArrayList<RoomView>();
		mDoors = new ArrayList<DoorView>();
		mPieces = new ArrayList<PieceView>();
	}
	
	public void SetMap(Map pMap)
	{
		mMap = pMap;
	}
	
	public void AddRoom(RoomView pRoom)
	{
		addView(pRoom);
		mRooms.add(pRoom);
	}
	
	public void AddDoor(DoorView pDoor)
	{
		addView(pDoor);
		mDoors.add(pDoor);
	}
	
	public void AddPiece(PieceView pPiece)
	{
		addView(pPiece);
		mPieces.add(pPiece);
	}
	
	@Override protected void onLayout(boolean pIsChanged, int pLeft, int pTop, int pRight, int pBottom)
	{
		/* If we don't have a map yet then don't try to render anything. */
		if (mMap == null) return;
		
		/* Calculate the size of the rooms. */
		int lWidth = pRight - pLeft;
		int lHeight = pBottom - pTop;
		int lNumRoomCols = (mMap.GetColCount() + 1) / 2;
		int lNumRoomRows = (mMap.GetRowCount() + 1) / 2;
		int lMaxRoomWidth = lWidth / lNumRoomCols;
		int lMaxRoomHeight = lHeight / lNumRoomRows;
		int lRoomSize = (lMaxRoomWidth < lMaxRoomHeight) ? lMaxRoomWidth : lMaxRoomHeight;
		
		/* Calculate the vertical and horizontal offsets necessary to center the map. */
		int lLeftOffset = (lWidth - lRoomSize * lNumRoomCols) / 2;
		int lTopOffset = (lHeight - lRoomSize * lNumRoomRows) / 2;
		
		for (RoomView lRoom : mRooms)
		{
			Map.Location lLocation = lRoom.mRoom.GetLocation();
			int lLeft = (lLocation.Col() / 2) * lRoomSize + lLeftOffset;
			int lTop = (lLocation.Row() / 2) * lRoomSize + lTopOffset;
			int lRight = lLeft + lRoomSize;
			int lBottom = lTop + lRoomSize;
			lRoom.layout(lLeft, lTop, lRight, lBottom);
		}
		
		for (DoorView lDoor : mDoors)
		{
			Map.Location lLocation = lDoor.mDoor.GetLocation();
			int lLeft;
			int lTop;
			/* Even row doors are vertical. */
			if (lLocation.Row() % 2 == 0)
			{
				lLeft = ((lLocation.Col() + 1) / 2) * lRoomSize - (lRoomSize / 2) + lLeftOffset;
				lTop = (lLocation.Row() / 2) * lRoomSize + lTopOffset;
			}
			/* Odd row doors are horizontal. */
			else
			{
				lLeft = (lLocation.Col() / 2) * lRoomSize + lLeftOffset;
				lTop =  ((lLocation.Row() + 1) / 2) * lRoomSize - (lRoomSize / 2) + lTopOffset;
			}
			int lRight = lLeft + lRoomSize;
			int lBottom = lTop + lRoomSize;
			lDoor.layout(lLeft, lTop, lRight, lBottom);
		}
		
		for (PieceView lPiece : mPieces)
		{
			Map.Location lLocation = lPiece.mPiece.GetLocation();
			int lLeft = (lLocation.Col() / 2) * lRoomSize + lLeftOffset;
			int lTop = (lLocation.Row() / 2) * lRoomSize + lTopOffset;
			int lRight = lLeft + lRoomSize;
			int lBottom = lTop + lRoomSize;
			lPiece.layout(lLeft, lTop, lRight, lBottom);
		}
	}
}
