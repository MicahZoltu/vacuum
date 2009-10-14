package com.zoltu.Vacuum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.zoltu.Vacuum.Messages.Level.Piece;
import com.zoltu.Vacuum.Messages.Level.Map.Room;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door.Orientation;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door.Type;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.util.Log;
import android.view.SurfaceHolder;

public class SurfaceRenderer implements Renderer
{
	protected final Context mContext;
	protected SurfaceHolder mSurfaceHolder;
	
	/** Each type of SceneItem needs to be associated with an appropriate renderer. **/
	private final Map<Class<? extends SceneItem>, SceneItemRenderer> mSceneItemRendererMap = new HashMap<Class<? extends SceneItem>, SceneItemRenderer>();
	
	/** A Drawable of the void. **/
	private final Drawable mVoid;
	/** A Drawable of a room. **/
	private final Drawable mRoom;
	/** A Drawable of a wall. **/
	private final Drawable mWall;
	/** An AnimationDrawable of a door that starts closed and then opens. **/
	private final AnimationDrawable mDoorOpening;
	/** A rotation of an AnimationDrawable of a door that starts open and then closes. **/
	private final RotateDrawable mDoorClosingHorizontal;
	/** A rotation of an AnimationDrawable of a door that starts open and then closes. **/
	private final RotateDrawable mDoorClosingVertical;
	/** An AnimationDrawable of a good piece that moves one room to the right. **/
	private final AnimationDrawable mGoodPiece;
	/** An AnimationDrawable of a bad piece that moves one room to the right. **/
	private final AnimationDrawable mBadPiece;
	
	public SurfaceRenderer(final Context pContext, final SurfaceHolder pSurfaceHolder)
	{
		mContext = pContext;
		mSurfaceHolder = pSurfaceHolder;
		
		/* Associate the renderers with the things they render. */
		mSceneItemRendererMap.put(SceneVoid.class, new SceneVoidRenderer());
		mSceneItemRendererMap.put(SceneRoom.class, new SceneRoomRenderer());
		mSceneItemRendererMap.put(SceneDoorHorizontal.class, new SceneDoorHorizontalRenderer());
		mSceneItemRendererMap.put(SceneDoorVertical.class, new SceneDoorVerticalRenderer());
		mSceneItemRendererMap.put(ScenePieceGood.class, new ScenePieceGoodRenderer());
		mSceneItemRendererMap.put(ScenePieceBad.class, new ScenePieceBadRenderer());
		
		Resources lResources = mContext.getResources();
		mVoid = lResources.getDrawable(R.drawable.void_room);
		mRoom = lResources.getDrawable(R.drawable.room);
		mWall = lResources.getDrawable(R.drawable.wall);
		mDoorOpening = (AnimationDrawable) lResources.getDrawable(R.drawable.door_opening);
		mDoorClosingHorizontal = (RotateDrawable) lResources.getDrawable(R.drawable.door_closing_horizontal);
		mDoorClosingVertical = (RotateDrawable) lResources.getDrawable(R.drawable.door_closing_vertical);
		mGoodPiece = (AnimationDrawable) lResources.getDrawable(R.drawable.piece_good);
		mBadPiece = (AnimationDrawable) lResources.getDrawable(R.drawable.piece_bad);
	}
	
	public void ChangeSurfaceHolder(final SurfaceHolder pSurfaceHolder)
	{
		/* Set the new SurfaceHolder as the one we are using. */
		mSurfaceHolder = pSurfaceHolder;
	}
	
	@Override public boolean CanRender()
	{
		return true;
	}
	
	@Override public void RenderScene()
	{
//		Canvas lCanvas = null;
//		try
//		{
//			lCanvas = mSurfaceHolder.lockCanvas();
//			final int lRoomWidth = lCanvas.getWidth() / pMapWidth;
//			final int lRoomHeight = lCanvas.getHeight() / pMapHeight;
//			for (SceneItem lSceneItem : pSceneGraph)
//			{
//				final int lRoomSize = (lRoomWidth < lRoomHeight) ? lRoomWidth : lRoomHeight;
//				
//				final SceneItemRenderer lSceneItemRenderer = mSceneItemRendererMap.get(lSceneItem.getClass());
//				if (lSceneItemRenderer == null)
//				{
//					Log.w(this.getClass().getName(), "I don't know how to render a " + lSceneItem.getClass().getName() + ".  Teach me!");
//					continue;
//				}
//				lSceneItemRenderer.Render(lCanvas, lSceneItem, lRoomSize);
//			}
//		}
//		catch (NullPointerException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			if (lCanvas != null) mSurfaceHolder.unlockCanvasAndPost(lCanvas);
//		}
	}
	
	private interface SceneItemRenderer
	{
		public void Render(final Canvas pCanvas, final SceneItem pSceneItem, final int pRoomSize) throws ClassCastException;
	}
	
	private class SceneVoidRenderer implements SceneItemRenderer
	{
		@Override public void Render(Canvas pCanvas, SceneItem pSceneItem, int pRoomSize) throws ClassCastException
		{
			SceneVoid lSceneVoid = (SceneVoid) pSceneItem;
			final Rect lBounds = new Rect();
			lBounds.left = (pRoomSize / 2) * (lSceneVoid.mLocation.x - 1);
			lBounds.right = lBounds.left + pRoomSize;
			lBounds.top = (pRoomSize / 2) * (lSceneVoid.mLocation.y - 1);
			lBounds.bottom = lBounds.top + pRoomSize;
			mVoid.setBounds(lBounds);
			mVoid.draw(pCanvas);
		}
	}
	
	private class SceneRoomRenderer implements SceneItemRenderer
	{
		@Override public void Render(Canvas pCanvas, SceneItem pSceneItem, int pRoomSize) throws ClassCastException
		{
			final SceneRoom lSceneRoom = (SceneRoom) pSceneItem;
			final Rect lBounds = new Rect();
			lBounds.left = (pRoomSize / 2) * (lSceneRoom.mLocation.x - 1);
			lBounds.right = lBounds.left + pRoomSize;
			lBounds.top = (pRoomSize / 2) * (lSceneRoom.mLocation.y - 1);
			lBounds.bottom = lBounds.top + pRoomSize;
			mRoom.setBounds(lBounds);
			mRoom.draw(pCanvas);
		}
	}
	
	private class SceneDoorHorizontalRenderer implements SceneItemRenderer
	{
		@Override public void Render(Canvas pCanvas, SceneItem pSceneItem, int pRoomSize) throws ClassCastException
		{
			final SceneDoorHorizontal lSceneDoor = (SceneDoorHorizontal) pSceneItem;
			final Rect lBounds = new Rect();
			lBounds.left = (pRoomSize / 2) * (lSceneDoor.mLocation.x - 1);
			lBounds.right = lBounds.left + pRoomSize;
			lBounds.top = (pRoomSize / 2) * (lSceneDoor.mLocation.y - 1);
			lBounds.bottom = lBounds.top + pRoomSize;
			mDoorClosingHorizontal.setBounds(lBounds);
			int lColor = 0x000000;
			switch (lSceneDoor.GetType())
			{
				case BLUE:
					lColor = 0x0000FF;
					break;
				case CYAN:
					lColor = 0x00FFFF;
					break;
				case GREEN:
					lColor = 0x00FF00;
					break;
				case MAGENTA:
					lColor = 0xFF00FF;
					break;
				case RED:
					lColor = 0xFF0000;
					break;
				case YELLOW:
					lColor = 0xFFFF00;
					break;
				case ENTRANCE:
					lColor = 0x808080;
					break;
				case EXIT:
					lColor = 0x808080;
					break;
				case OPENING:
					lColor = 0xFFFFFF;
					break;
				case WALL:
					lColor = 0x000000;
					break;
			}
			mDoorClosingHorizontal.setColorFilter(new LightingColorFilter(lColor, 0));
			mDoorClosingHorizontal.draw(pCanvas);
		}
	}
	
	private class SceneDoorVerticalRenderer implements SceneItemRenderer
	{
		@Override public void Render(Canvas pCanvas, SceneItem pSceneItem, int pRoomSize) throws ClassCastException
		{
			final SceneDoorVertical lSceneDoor = (SceneDoorVertical) pSceneItem;
			final Rect lBounds = new Rect();
			lBounds.left = (pRoomSize / 2) * (lSceneDoor.mLocation.x - 1);
			lBounds.right = lBounds.left + pRoomSize;
			lBounds.top = (pRoomSize / 2) * (lSceneDoor.mLocation.y - 1);
			lBounds.bottom = lBounds.top + pRoomSize;
			mDoorClosingVertical.setBounds(lBounds);
			int lColor = 0x000000;
			switch (lSceneDoor.GetType())
			{
				case BLUE:
					lColor = 0x0000FF;
					break;
				case CYAN:
					lColor = 0x00FFFF;
					break;
				case GREEN:
					lColor = 0x00FF00;
					break;
				case MAGENTA:
					lColor = 0xFF00FF;
					break;
				case RED:
					lColor = 0xFF0000;
					break;
				case YELLOW:
					lColor = 0xFFFF00;
					break;
				case ENTRANCE:
					lColor = 0x808080;
					break;
				case EXIT:
					lColor = 0x808080;
					break;
				case OPENING:
					lColor = 0xFFFFFF;
					break;
				case WALL:
					lColor = 0x000000;
					break;
			}
			mDoorClosingVertical.setColorFilter(new LightingColorFilter(lColor, 0));
			final int lSaveCount = pCanvas.save();
			pCanvas.rotate(90, lBounds.left + (pRoomSize / 2), lBounds.top + (pRoomSize / 2));
			mDoorClosingVertical.draw(pCanvas);
			pCanvas.restoreToCount(lSaveCount);
		}
	}
	
	private class ScenePieceGoodRenderer implements SceneItemRenderer
	{
		@Override public void Render(Canvas pCanvas, SceneItem pSceneItem, int pRoomSize) throws ClassCastException
		{
			
		}
	}
	
	private class ScenePieceBadRenderer implements SceneItemRenderer
	{
		@Override public void Render(Canvas pCanvas, SceneItem pSceneItem, int pRoomSize) throws ClassCastException
		{
			
		}
	}

	@Override public void AddDoor(Door pDoor, Point pLocation, Orientation pOrientation)
	{
		// TODO Auto-generated method stub
		
	}

	@Override public void AddPiece(Piece pPiece, Point pLocation)
	{
		// TODO Auto-generated method stub
		
	}

	@Override public void AddRoom(Room pRoom, Point pLocation)
	{
		// TODO Auto-generated method stub
		
	}

	@Override public void MovePiece(Piece pPiece, Point pLocation)
	{
		// TODO Auto-generated method stub
		
	}

	@Override public void ToggleDoor(Type pType, boolean pStateFlipped)
	{
		// TODO Auto-generated method stub
		
	}
}
