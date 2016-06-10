package com.inte.indoorpositiontracker;

import android.content.Context;
import android.graphics.BitmapFactory;
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
	private static final int COLOR_PATH = Color.rgb(0, 179, 253);
	private static final int DESTINATION_POINT = Color.WHITE;

	private Fingerprint mFingerprint;
	
	private boolean mActive;
	POINT_TYPE pointType;
	String destName;
	String info = "";

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
		mRadius = 20f;
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

			// Draw whole circle
			mPaint.setStyle(Paint.Style.FILL);

			// Set point specific color
			SetPointViewProperties(pointType);
			canvas.drawCircle(mRelativeX, mRelativeY, mRadius, mPaint);

			// Draw border
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.BLACK);
			if (pointType == POINT_TYPE.destPoint) // if dest point -> put thick border
			mPaint.setStrokeWidth(8f);
			else
				mPaint.setStrokeWidth(5f);

			canvas.drawCircle(mRelativeX, mRelativeY, mRadius, mPaint);

			if (info != "")
				DrawInfo(canvas, info);
	    }

		if (pointType == POINT_TYPE.destPoint) {

			// draw little vertex
			mPaint.setStyle(Paint.Style.FILL);
			mPaint.setColor(Color.BLACK);
			mPaint.setStrokeWidth(5f);

			canvas.drawCircle(mRelativeX, mRelativeY, 12f, mPaint);

			DrawInfo(canvas, destName);
			// Draw Pin
			canvas.drawBitmap(BitmapFactory.decodeResource
					(getResources(), R.drawable.pin_pointer),mRelativeX - 102.5f ,mRelativeY - 190f ,null);
		}
	}

	private  void DrawInfo(Canvas canvas, String text)
	{
		Paint.FontMetrics fm = new Paint.FontMetrics();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.BLACK);
		mPaint.setTextSize(45f);
		mPaint.getFontMetrics(fm);
		int margin = 20;
		canvas.drawRect(mRelativeX + 80f - margin, mRelativeY - mRadius * 5 + fm.top - margin,
				mRelativeX + 80f + mPaint.measureText(text) + margin,
				mRelativeY - mRadius * 5 + fm.bottom + margin, mPaint);
		mPaint.setColor(Color.WHITE);
		mPaint.setStrokeWidth(0);
		mPaint.setTextSize(45f);
		mPaint.setAntiAlias(true);
		canvas.drawText(text, mRelativeX + 80f, mRelativeY - mRadius * 5, mPaint); // TEXT
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
				break;
			case Path: {
				mPaint.setColor(COLOR_PATH);
				break;
			}
			case FingerPrint: {
				mPaint.setColor(COLOR_FINGERPRINT);
				mPaint.setStyle(Paint.Style.FILL);
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
