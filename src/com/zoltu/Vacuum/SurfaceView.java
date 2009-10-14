package com.zoltu.Vacuum;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class SurfaceView extends android.view.SurfaceView implements SurfaceHolder.Callback
{
	private GraphicsContext mGraphicsContext;
	
	public SurfaceView(Context pContext, AttributeSet pAttributeSet)
	{
		super(pContext, pAttributeSet);
		
		/* Set ourselves up to be notified of significant changes to the surface. */
		getHolder().addCallback(this);
		
		// Make sure we get key events.
		setFocusable(true);
	}
	
	public void SetGraphicsContext(GraphicsContext pGraphicsContext)
	{
		mGraphicsContext = pGraphicsContext;
		
		/* If the surface is already done creating then immediately tell the GraphicsContext that a surface is ready. */
		if (!getHolder().isCreating())
		{
			android.os.Message lMessage = mGraphicsContext.obtainMessage(0, new SurfaceHolderHolder(getHolder()));
			mGraphicsContext.sendMessage(lMessage);
		}
	}
	
	@Override public void surfaceChanged(SurfaceHolder pSurfaceHolder, int pFormat, int pWidth, int pHeight)
	{
		/* Handled by the GraphicsContext. */
	}
	
	@Override public void surfaceCreated(SurfaceHolder pSurfaceHolder)
	{
		/* Tell the GraphicsContext about the SurfaceHolder so it can render to the surface. */
		if (mGraphicsContext == null) return;
		android.os.Message lMessage = mGraphicsContext.obtainMessage(0, new SurfaceHolderHolder(pSurfaceHolder));
		mGraphicsContext.sendMessage(lMessage);
	}
	
	@Override public void surfaceDestroyed(SurfaceHolder pSurfaceHolder)
	{
		/*
		 * Send a message to GraphicsContext telling it about the pending destruction of the SurfaceHolder and then spin
		 * until it is verified that the SurfaceHolder is no longer referenced.
		 */
		DestroySurfaceRequest lRequest = new DestroySurfaceRequest(pSurfaceHolder);
		android.os.Message lMessage = mGraphicsContext.obtainMessage(0, lRequest);
		mGraphicsContext.sendMessage(lMessage);
		
		/* We can't return from this function until the SurfaceHolder isn't used anymore. */
		while (!lRequest.mSurfaceDestroyed)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException lException)
			{
				lException.printStackTrace();
			}
		}
	}
	
	/**
	 * This message is sent to the GraphicsContext when the system tells us we have to get rid of a SurfaceHolder in
	 * another thread. Once we send this message is sent, the thread that sent it will most likely spin until
	 * mSurfaceDestroyed is true so the receiving thread needs to be quick about dealing with it.
	 **/
	protected class DestroySurfaceRequest
	{
		/** The thread that wants to destroy the surface will spin on this flag until it's set to true. **/
		public volatile boolean mSurfaceDestroyed = false;
		/**
		 * The SurfaceHolder that is being destroyed. Once it's verified that nothing is referencing it we can set
		 * mSurfaceDestroyed to true.
		 **/
		public final SurfaceHolder mSurfaceHolder;
		
		public DestroySurfaceRequest(SurfaceHolder pSurfaceHolder)
		{
			mSurfaceHolder = pSurfaceHolder;
		}
	}
}

/**
 * We can't use reflection to call a function with a parameter typed as an Interface Implementation, so we wrap the
 * SurfaceHolder in this class and pass it as a message which can then use reflection to call the correct processing
 * function.  A bit hacky. :(
 **/
class SurfaceHolderHolder
{
	public final SurfaceHolder mSurfaceHolder;
	
	public SurfaceHolderHolder(SurfaceHolder pSurfaceHolder)
	{
		mSurfaceHolder = pSurfaceHolder;
	}
}
