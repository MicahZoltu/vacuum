package com.zoltu.Vacuum.View;

import com.zoltu.Vacuum.Map;
import com.zoltu.Vacuum.R;
import android.content.Context;
import android.view.View;

public class PieceView extends View
{
	public final Map.Piece mPiece;
	
	public PieceView(Context pContext, Map.Piece pPiece) throws IllegalArgumentException
	{
		super(pContext);
		
		mPiece = pPiece;
		
		switch (mPiece.GetTeam())
		{
			case EVIL:
				setBackgroundResource(R.drawable.piece_bad);
				break;
			case GOOD:
				setBackgroundResource(R.drawable.piece_good);
				break;
			default:
				throw new IllegalArgumentException("Unhandled piece team.");
		}
	}
}
