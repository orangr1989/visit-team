package com.inte.indoorpositiontracker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;

import DataModel.Fingerprint;

public class MapView extends ImageView {
	// We can be in one of these 3 states
	private static final int MAP_STATE_NONE = 0;
	private static final int MAP_STATE_DRAG = 1;
	private static final int MAP_STATE_ZOOM = 2;
	
	// These matrices will be used to move and zoom image
	private Matrix mMatrix = new Matrix();
	private Matrix mSavedMatrix = new Matrix();

	private int mode = MAP_STATE_NONE;

	// Remember some things for zooming
	private PointF mStart = new PointF();
	private PointF mid = new PointF();
	float mOldDist = 1f;
	
	private ArrayList<WifiPointView> mWifiPoints;
	private ArrayList<WifiPointView> mWifiPath;

	
	/** CONSTRUCTORS */
	
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mWifiPoints = new ArrayList<WifiPointView>();
		mWifiPath = new ArrayList<WifiPointView>();
	}
	
	
	
	/** INSTANCE METHODS */
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		float[] values = new float[9];
		mMatrix.getValues(values);
		
		// draw all visible "fingerprints"
		for(WifiPointView point : mWifiPoints) {
			point.drawWithTransformations(canvas, values);
		}

		if (mWifiPath.size() > 0) {
			for (int i = 0; i < mWifiPath.size(); i += 15) {
				mWifiPath.get(i).drawWithTransformations(canvas, values);
			}

			mWifiPath.get(mWifiPath.size() - 1).drawWithTransformations(canvas, values); //draw dest point
		}
	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        ImageView map = (ImageView)findViewById(R.id.mapView);

        int height = map.getDrawable().getIntrinsicHeight();
        int width = map.getDrawable().getIntrinsicWidth();

        RectF drawableRect = new RectF(0, 0, width, height);
        RectF viewRect = new RectF(0, 0, map.getWidth(), map.getHeight());
        mMatrix.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

        map.setImageMatrix(mMatrix);

    }

	/**
	 * Map moving and zooming
	 */

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onMultiTouchStart(event);
                break;
            case MotionEvent.ACTION_UP:
                onTouchEnd(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onMultiTouchEnd(event);
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
        }
        
		return true;
	}
	    
	public void onTouchStart(MotionEvent event) {
        mSavedMatrix.set(mMatrix);
        mStart.set(event.getX(), event.getY()); // save the location where touch started
        mode = MAP_STATE_DRAG;
    }
	    
    public void onTouchEnd(MotionEvent event) {
        mode = MAP_STATE_NONE;
    }
	    
    public void onMultiTouchStart(MotionEvent event) {
        mOldDist = spacing(event);
        
        // start zoom mode if touch points are spread far enough from each other
        if (mOldDist > 10f) { 
            mSavedMatrix.set(mMatrix);
            midPoint(mid, event);
            mode = MAP_STATE_ZOOM;
        }
    }
	    
    public void onMultiTouchEnd(MotionEvent event) {
        mSavedMatrix.set(mMatrix);
        mode = MAP_STATE_DRAG;
    }
    
    public void onTouchMove(MotionEvent event) {
        if (mode == MAP_STATE_DRAG) {
            mapMove(event);
        }
        else if (mode == MAP_STATE_ZOOM) {
            mapZoom(event);
        }
    }
    
    public void mapMove(MotionEvent event) {
        mMatrix.set(mSavedMatrix);
        mMatrix.postTranslate(event.getX() - mStart.x, event.getY() - mStart.y); // translates map
        setImageMatrix(mMatrix);
    }
    
    public void mapZoom(MotionEvent event) {
        float newDist = spacing(event);
        
        // zoom in/out if touch points are spread far enough from each other
        if (newDist > 10f) {
            mMatrix.set(mSavedMatrix);
            float scale = newDist / mOldDist;
            mMatrix.postScale(scale, scale, mid.x, mid.y); // zoom in/out
            setImageMatrix(mMatrix);
        }
    }
	
	/**
	 * Helper methods for zoom functionality
	 */

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	
	
	/**
	 * Functions for creating new WifiPointViews
	 */
	
	/** create new WifiPointView to given location */
	public WifiPointView createNewWifiPointOnMap(PointF location) {
		WifiPointView wpView = new WifiPointView(getContext(), WifiPointView.POINT_TYPE.CurrentLocation);
		float[] values = new float[9];
		mMatrix.getValues(values);
		location.set((location.x - values[2]) / values[0], (location.y - values[5]) / values[4]);
		wpView.setLocation(location);
		mWifiPoints.add(wpView);
		return wpView;
	}
	
	/** create new WifiPointView and bind it to given fingerprint */
	public WifiPointView createNewWifiPointOnMap(Fingerprint fingerprint) {
	    WifiPointView wpView = new WifiPointView(getContext(), WifiPointView.POINT_TYPE.FingerPrint);
	    wpView.setFingerprint(fingerprint);
	    mWifiPoints.add(wpView);
	    return wpView;
	}
	
	/** create new WifiPointView, bind it to given fingerprint and set its visibility*/
	public WifiPointView createNewWifiPointOnMap(Fingerprint fingerprint, boolean visible) {
	    WifiPointView wpView = createNewWifiPointOnMap(fingerprint);
	    wpView.setVisible(visible);
	    return wpView;
	}

	public WifiPointView createPath(float x, float y) {
		WifiPointView wpView = new WifiPointView(getContext(), WifiPointView.POINT_TYPE.Path);
		wpView.setPathPoint(x, y);
		mWifiPath.add(wpView);

		wpView.setVisible(true);
		return wpView;
	}
	
	
	
	/** set given WifiPointView to given location */
	public void setWifiPointViewPosition(WifiPointView pointer, PointF location) {
        float[] values = new float[9];
        mMatrix.getValues(values);
        location.set((location.x - values[2]) / values[0], (location.y - values[5]) / values[4]);
        pointer.setLocation(location);
    }
	
	/**
	 * Functions for modifying the ArrayList of all WifiPointViews on the map
	 */
	
	public ArrayList<WifiPointView> getWifiPoints() {
		return mWifiPoints;
	}

	public void setWifiPoints(ArrayList<WifiPointView> wifiPoints) {
		mWifiPoints = wifiPoints;
	}
	
	public void setWifiPointsVisibility(boolean visible) {
	    for(WifiPointView point : mWifiPoints) {
	        point.setVisible(visible);
	    }
	}
	
	public void deleteFingerprints() {
	    ArrayList<WifiPointView> itemsToRemove = new ArrayList<WifiPointView>();   
	    for (WifiPointView point : mWifiPoints) {
	        if(point.getFingerprint() != null) {
	            itemsToRemove.add(point);
	        }	        
	    }
	    mWifiPoints.removeAll(itemsToRemove);
	    invalidate();
	}

	public void deletePath() {
		ArrayList<WifiPointView> itemsToRemove = new ArrayList<WifiPointView>();
		for (WifiPointView point : mWifiPath) {
				itemsToRemove.add(point);
		}
		mWifiPath.removeAll(itemsToRemove);
		invalidate();
	}
}
