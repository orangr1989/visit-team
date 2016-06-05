package com.inte.indoorpositiontracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

import DataModel.Fingerprint;
import DataModel.Location;

public class WifiPointView extends View {
	public  enum  POINT_TYPE {CurrentLocation, Path, FingerPrint, destPoint }

	private static final int COLOR_FINGERPRINT = Color.YELLOW;
	private static final int COLOR_ACTIVE = Color.GREEN;
	private static final int COLOR_PATH = Color.rgb(0,179,253);
	private static final int DESTINATION_POINT = Color.rgb(170,47,206);

	private Fingerprint mFingerprint;
	
	private boolean mActive;
	POINT_TYPE pointType;
	String destName;

	public Paint mPaint; // draw color
	
	private PointF mLocation; // location on screen
	private float mRadius; // circle radius
	
	// placeholders for calculated screen positions
	private float mRelativeX, mRelativeY;

	private boolean mVisible;

	public WifiPointView(Context context, POINT_TYPE type) {
		super(context);
		mPaint = new Paint();

		mPaint.setTextSize(50);
		mPaint.setAntiAlias(true);
		
		mActive = false;
		mVisible = true;
		mRadius = 10f;
		mLocation = new PointF(0,0);
		mFingerprint = null;
		pointType = type;
		SetPointViewProperties(pointType);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}
	
	protected void drawWithTransformations(Canvas canvas, float[] matrixValues) {
		mRelativeX = matrixValues[2] + mLocation.x * matrixValues[0];
		mRelativeY = matrixValues[5] + mLocation.y * matrixValues[4];

		if(mVisible == true) { // draw only if set visible
			SetPointViewProperties(pointType);
    		canvas.drawCircle(mRelativeX, mRelativeY, mRadius, mPaint);
	    }

		if (pointType == POINT_TYPE.destPoint) {
			mPaint.setColor(Color.BLACK);
			mPaint.setTextSize(16);
			canvas.drawText(destName, mRelativeX, mRelativeY - mRadius * 2, mPaint);
		}
	}
	
	public void setLocation(PointF location) {
		mLocation = location;
	}

	public void setPathPoint(float x, float y) {
		mLocation = new PointF(x, y);
	}
	
	public PointF getLocation() {
	    return mLocation;
	}

	public void SetPointViewProperties(POINT_TYPE type)
	{
		switch (type) {
			case CurrentLocation:
				mPaint.setColor(COLOR_ACTIVE);
				mRadius = 10f;
				break;
			case Path: {
				mPaint.setColor(COLOR_PATH);
				break;
			}
			case FingerPrint: {
				mPaint.setColor(COLOR_FINGERPRINT);
				break;
			}
			case  destPoint: {
				mPaint.setColor(DESTINATION_POINT);
				break;
			}
			default:
		}
	}

	public void setSize(float radius) {
	    mRadius = radius;
	}
	
	public void setFingerprint(Fingerprint fingerprint) {
		mFingerprint = fingerprint;
		mLocation = new PointF((float)fingerprint.getLocation().getMapXcord(),
				(float)fingerprint.getLocation().getMapYcord());
	}

	public void setFingerprint(Location location) {
		mLocation = new PointF((float)location.getMapXcord(),
				(float)location.getMapYcord());
	}

	public void setPoint(PointF point) {
		mLocation = point;
	}
	
	public Fingerprint getFingerprint() {
	    return mFingerprint;
	}
	
	public void activate() {
		mActive = true;
	}
	
	public void deactivate() {
		mActive = false;
	}
	
	public void setVisible(boolean visible) {
	    mVisible = visible;
	}
	
	public boolean isVisible() {
	    return mVisible;
	}
}
