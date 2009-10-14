package com.zoltu.Vacuum;

import android.content.Context;
import android.graphics.Point;
import android.os.Looper;
import android.view.SurfaceHolder;
import com.zoltu.Vacuum.Messages.GamePause;
import com.zoltu.Vacuum.Messages.Level;
import com.zoltu.Vacuum.Messages.Level.Map;
import com.zoltu.Vacuum.Messages.Level.Piece;
import com.zoltu.Vacuum.Messages.Level.Map.Room;
import com.zoltu.Vacuum.Messages.Level.Map.RoomRow;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door;
import com.zoltu.Vacuum.Messages.Level.Map.Room.Door.Type;
import com.zoltu.Vacuum.Messaging.MessagingContext;
import com.zoltu.Vacuum.SurfaceView.DestroySurfaceRequest;

/**
 * This context handles all of the graphics for the game. The renderer actually does all of the real work with drawing
 * to the screen and the renderer may change over time if the user switches themes or something. The GraphicsContext
 * handles all communication with the outside world.
 **/
public class GraphicsContext extends MessagingContext
{
	/** The GraphicsContext needs access to the application context so it can access resources (art, models, etc.). **/
	private final Context mContext;
	/** Start out using the NullRenderer, until we get a better idea of what kind of render target we have. **/
	private volatile Renderer mRenderer = new NullRenderer();
	
	/** The magic number that is pushed onto the message queue to signify that it's time to render another frame. **/
	private static final int MAGIC_RENDER_MESSAGE_NUMBER = -1;
	
	/**
	 * The context passed into this constructor is where the Renderer will look for system resources such as art,
	 * models, etc.
	 **/
	public GraphicsContext(Looper pLooper, Context pContext)
	{
		super(pLooper, "ProcessMessage");
		mContext = pContext;
	}
	
	/** Render one frame of the game, push another render request onto the message queue, then return. **/
	private void RenderFrame()
	{
		/* If the renderer is not in a state where it's ready to render, don't waste time trying. */
		if (!mRenderer.CanRender())
		{
			/* Wait a short amount of time and then queue up another message before returning. */
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			sendEmptyMessage(MAGIC_RENDER_MESSAGE_NUMBER);
			return;
		}
		
		/* Tell the renderer to render the scene graph now. */
		mRenderer.RenderScene();
		
		/*
		 * Post the next frame render message onto the MessageQueue so we will render another frame once the message
		 * queue is cleared out.
		 */
		sendEmptyMessage(MAGIC_RENDER_MESSAGE_NUMBER);
	}
	
	/**
	 * Special ProcessMessage function to handle empty messages. The most common empty message is the magic render
	 * message which is called every frame.
	 * 
	 * @param pWhat
	 *            Identifier for the specially handled message. Usually represents a message that needs to be fast or
	 *            small.
	 **/
	public void ProcessMessage(Integer pWhat)
	{
		switch (pWhat)
		{
			case MAGIC_RENDER_MESSAGE_NUMBER:
			{
				RenderFrame();
				return;
			}
		}
	}
	
	/** Received when the game has been paused. **/
	public void ProcessMessage(GamePause.PauseAnnouncement pAnnouncement)
	{
		/* Clear out any pending render messages. */
		removeMessages(MAGIC_RENDER_MESSAGE_NUMBER);
	}
	
	/** Received when the game has been unpaused. **/
	public void ProcessMessage(GamePause.UnpausedAnnouncement pAnnouncement)
	{
		/*
		 * Clear out any pending render messages, this is done to prevent multiple render messages getting into the
		 * queue.
		 */
		removeMessages(MAGIC_RENDER_MESSAGE_NUMBER);
		/* Push a new render message onto the message queue. */
		sendEmptyMessage(MAGIC_RENDER_MESSAGE_NUMBER);
	}
	
	/** Received when a level has finished loading and we should start rendering it. **/
	public void ProcessMessage(Level.LoadedAnnouncement pAnnouncement)
	{
		/** Easy access to the map. **/
		final Map lMap = pAnnouncement.getMap();
		/** Location of the room we are working with (re-used throughout this function). **/
		final Point lRoomLocation = new Point(1, 1);
		
		/* Tell the renderer about all of the rooms. */
		for (RoomRow lRow : lMap.getRoomRowsList())
		{
			lRoomLocation.x = 1;
			for (Room lRoom : lRow.getRoomsList())
			{
				mRenderer.AddRoom(lRoom, lRoomLocation);
				
				lRoomLocation.x += 2;
			}
			lRoomLocation.y += 2;
		}
		
		/* Tell the renderer about all of the doors. */
		lRoomLocation.y = 1;
		for (int i = 0; i < lMap.getRoomRowsCount(); ++i)
		{
			/** True if this is the last row of rooms (special cased). **/
			final boolean lLastRow = (i == lMap.getRoomRowsCount() - 1) ? true : false;
			/** A row of rooms. **/
			final RoomRow lRow = lMap.getRoomRows(i);
			
			lRoomLocation.x = 1;
			
			for (int j = 0; j < lRow.getRoomsCount(); ++j)
			{
				/** True if this is the last column of rooms (special cased). **/
				final boolean lLastColumn = (j == lRow.getRoomsCount() - 1) ? true : false;
				/** The room we are currently working with. **/
				final Room lRoom = lRow.getRooms(j);
				/** The location of the right door. **/
				final Point lRightDoorLocation = new Point(lRoomLocation.x + 1, lRoomLocation.y);
				/** The location of the down door. **/
				final Point lDownDoorLocation = new Point(lRoomLocation.x, lRoomLocation.y + 1);
				
				/* The last column of rooms always has a wall to the right, unless it's a vacuum. */
				if (lLastColumn && !lRoom.getVacuum())
				{
					mRenderer.AddDoor(Door.newBuilder().setType(Type.WALL).build(), lRightDoorLocation, Door.Orientation.VERTICAL);
				}
				/* For all other rooms add the right door. */
				else
				{
					mRenderer.AddDoor(lRoom.getRight(), lRightDoorLocation, Door.Orientation.VERTICAL);
				}
				
				/* The last row of rooms always has a wall to the bottom, unless it's a vacuum. */
				if (lLastRow && !lRoom.getVacuum())
				{
					mRenderer.AddDoor(Door.newBuilder().setType(Type.WALL).build(), lDownDoorLocation, Door.Orientation.HORIZONTAL);
				}
				/* For all other rooms add the bottom door. */
				else
				{
					mRenderer.AddDoor(lRoom.getDown(), lDownDoorLocation, Door.Orientation.HORIZONTAL);
				}
				
				lRoomLocation.x += 2;
			}
			
			lRoomLocation.y += 2;
		}
		
		/* Tell the renderer about all the pieces. */
		lRoomLocation.y = 1;
		for (RoomRow lRow : lMap.getRoomRowsList())
		{
			lRoomLocation.x = 1;
			for (Room lRoom : lRow.getRoomsList())
			{
				if (lRoom.hasPiece())
				{
					final Piece lPiece = lRoom.getPiece();
					mRenderer.AddPiece(lPiece, lRoomLocation);
				}
				
				lRoomLocation.x += 2;
			}
			lRoomLocation.y += 2;
		}
	}
	
	/**
	 * Received when a SurfaceHolder has become available to us. This usually means we will be switching Renderers. If
	 * we are currently using a SurfaceRenderer then set this new SurfaceHolder as the one it uses. If we are not
	 * currently using a SurfaceRenderer then switch to the basic SurfaceRenderer.
	 **/
	public void ProcessMessage(SurfaceHolderHolder pSurfaceHolderHolder)
	{
		final SurfaceHolder lSurfaceHolder = pSurfaceHolderHolder.mSurfaceHolder;
		if (mRenderer instanceof SurfaceRenderer)
		{
			((SurfaceRenderer) mRenderer).ChangeSurfaceHolder(lSurfaceHolder);
		}
		else
		{
			mRenderer = new SurfaceRenderer(mContext, lSurfaceHolder);
		}
	}
	
	/**
	 * Received when a SurfaceHolder has become invalid (about to be destroyed). We need to stop referencing the
	 * SurfaceHolder and then set the mSurfaceDestroyed flag to true so the thread that dropped this message into our
	 * queue knows it's safe to destroy the surface.
	 **/
	public void ProcessMessage(DestroySurfaceRequest pDestroySurfaceRequest)
	{
		/*
		 * Check to see if we are using the SurfaceHolder being destroyed; if we are then create a new NullRenderer to
		 * replace the old one.
		 */
		if ((mRenderer instanceof SurfaceRenderer) || ((SurfaceRenderer) mRenderer).mSurfaceHolder == pDestroySurfaceRequest.mSurfaceHolder)
		{
			/*
			 * If the SurfaceHolder is still in use then create a new NullRenderer to replace the old, invalid,
			 * renderer.
			 */
			mRenderer = new NullRenderer();
		}
		
		/*
		 * Setting the mSurfaceDestroyed flag lets the thread that is waiting for the destruction know that we aren't
		 * using this anymore.
		 */
		pDestroySurfaceRequest.mSurfaceDestroyed = true;
	}
}
