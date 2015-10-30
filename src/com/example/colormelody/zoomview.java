package com.example.colormelody;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.PopupWindow;




public class zoomview extends View {
	
	private static final int RADIUS =116;
	private static final int SIZE = 262;
	private static final int HANDLE_SIZE = 66;
	private static final long DELAY_TIME = 250;
	
	private Rect srcRect;
	private Point dstPoint;
	private float zoomPointX;
	private float zoomPointY;
	
	private Bitmap magnifierBitmap;
	private Bitmap handleBitmap;
	private Bitmap maskBitmap;
	
	private Bitmap resBitmap;
	private Canvas canvas;
	public Bitmap bg;
	private PopupWindow popup;
	private Magnifier magnifier;
	public static int flag1;
	
	//private MainActivity Activi;
	public zoomview(Context context, AttributeSet attrs) {
		super(context, attrs);
		//this.bg=bg;
		
		bg = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

		BitmapDrawable magnifierDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.magnifier);
		magnifierBitmap = magnifierDrawable.getBitmap();
		BitmapDrawable handleDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.magnifier_handle);
		//handleBitmap = handleDrawable.getBitmap();
		BitmapDrawable maskDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mask);
		maskBitmap = maskDrawable.getBitmap();
		magnifier = new Magnifier(context);
		popup = new PopupWindow(magnifier, SIZE, SIZE);
		popup.setAnimationStyle(android.R.style.Animation_Toast);
		
		srcRect = new Rect(0, 0, RADIUS, RADIUS);
		dstPoint = new Point(0, 0);
	}
	
	public void setbg(Bitmap bitmap){
		bg=bitmap;
		invalidate();
	}
	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
			flag1 = 0;
			int x = (int) event.getX();
			int y = (int) event.getY();
			srcRect.offsetTo( x - RADIUS/2, y - RADIUS/2);
			dstPoint.set(x - RADIUS, y - 3 * RADIUS / 2);
			if (srcRect.left < 0) {
				srcRect.offset(-srcRect.left, 0);
			} else 
			if (srcRect.right > resBitmap.getWidth()) {
				srcRect.offset(resBitmap.getWidth() - srcRect.right, 0);
			}
			if (srcRect.top < 0) {
				srcRect.offset(0, -srcRect.top);
			} else
			if (srcRect.bottom > resBitmap.getHeight()) {
				srcRect.offset(0, resBitmap.getHeight() - srcRect.bottom);
			}
			if (y < 0) {
				// hide popup if out of bounds
				popup.dismiss();
				invalidate();
				return true;
			}
			if (action == MotionEvent.ACTION_DOWN) {
				removeCallbacks(showZoom);
				postDelayed(showZoom, DELAY_TIME);
			} else if (!popup.isShowing()) {
				showZoom.run();
			}
			popup.update(getLeft() + dstPoint.x, getTop() + dstPoint.y, -1, -1);
			magnifier.invalidate();
		}else if (action == MotionEvent.ACTION_UP) {
			removeCallbacks(showZoom);
			zoomPointX = srcRect.exactCenterX();
			zoomPointY = srcRect.exactCenterY();
			flag1=1;
		//	flag2=1;
			
			/*drawLayout();*/
		//	Activi.PlayLocation(getZoomX(), getZoomY());
			popup.dismiss();
			this.setVisibility(INVISIBLE);
			//magnifier.setVisibility(INVISIBLE);
		}
		invalidate();
		return true;
	}
	public int getZoomX(){
		return (int) zoomPointX;
	};
	public int getZoomY(){
		return (int) zoomPointY;
	};
	/*private void drawLayout() {
		canvas.drawBitmap(bg, 0, 0, null);
	}*/
	public void drawLayout() {
		canvas.drawBitmap(bg, 0, 0, null);
	}
	
	Runnable showZoom = new Runnable() {
		public void run() {
			popup.showAtLocation(zoomview.this,
					Gravity.NO_GRAVITY,
					getLeft() + dstPoint.x, 
					getTop() + dstPoint.y);
		}
	};
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int w = MeasureSpec.getSize(widthMeasureSpec);
		int h = MeasureSpec.getSize(heightMeasureSpec);//480 800
		createBitmap(w, h);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public void createBitmap(int w, int h) {
		resBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		canvas = new Canvas(resBitmap);
		//drawLayout();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.scale(1f, 1f);
		canvas.drawBitmap(resBitmap, 0, 0, null);
	}
	
	protected void Background(){
		
	}
	
	class Magnifier extends View {
		private Paint mPaint;
		private Rect rect;
		private Path clip;

		public Magnifier(Context context) {
			super(context);
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setColor(0xff008000);
			mPaint.setStyle(Style.STROKE);
			rect = new Rect(0, 0, RADIUS*2, RADIUS*2);
			clip = new Path();
			clip.addCircle(RADIUS, RADIUS, RADIUS, Direction.CW);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.save();
			canvas.clipPath(clip);
			// draw popup
			mPaint.setAlpha(255);
			canvas.drawBitmap(resBitmap, srcRect, rect, mPaint);
			canvas.restore();
			// draw popup frame
			mPaint.setAlpha(220);
			canvas.drawBitmap(magnifierBitmap, 0, 0, mPaint);
			// draw popup handle
			mPaint.setAlpha(255);
			//canvas.drawBitmap(handleBitmap, SIZE-HANDLE_SIZE, SIZE-HANDLE_SIZE, mPaint);
		}
	}
}