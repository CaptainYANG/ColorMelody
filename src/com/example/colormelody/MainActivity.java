package com.example.colormelody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.example.colormelody.zoomview;
import com.example.colormelody.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity implements SurfaceHolder.Callback,OnGestureListener{

    private Button mButton;
    private Button galleryButton;
    
	private GestureDetectorCompat gestureDetector;
    View.OnTouchListener gestureListener;
	
  //  private ImageView mImageView;//”√”⁄œ‘ æ’’∆¨
    private ImageView screenview;
    private zoomview zoom;
    private RelativeLayout screen;
    private SurfaceView surview;
    private SurfaceHolder holder;
    private Camera camera;
    
    private Bitmap bitmap1,bitmap2;
    public static int flag1=zoomview.flag1;
    public static int flag2=0;
    

    private TextView textColorname;
    
    private  MediaPlayer mMediaPlayer;
    
    public final static int CAMERA_RESULT = 8888;
    public final static String TAG = "xx";

    private int dispHeight;
    private int dispWidth;
  
    
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //news
		requestWindowFeature(Window.
				FEATURE_NO_TITLE );
		int flag=WindowManager.LayoutParams.
				FLAG_FULLSCREEN ;
		Window myWindow= this.getWindow(); 
		myWindow.setFlags(flag,flag);
        setContentView(R.layout.activity_main);
        //surfaceview
        
      gestureDetector = new GestureDetectorCompat(this,this);
      zoom = (zoomview) findViewById(R.id.zoom);
      zoom.setVisibility(View.INVISIBLE);
      zoom.setOnTouchListener(new OnTouchListener(){

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_UP){
				PlayLocation(zoom.getZoomX(),zoom.getZoomY());
				System.out.println("upzoom");
			}
			return false;
		}
    	  
      });
      flag2=0;
      Display display = getWindowManager().getDefaultDisplay();
      dispHeight = display.getHeight();
      dispWidth = display.getWidth();
      /* final CameraSurfaceView cameraSurfaceView = new CameraSurfaceView(this);
       
        screen.addView(cameraSurfaceView);*/
        screen=(RelativeLayout)findViewById(R.id.screen);
        surview=(SurfaceView)findViewById(R.id.surview);
        holder=surview.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        screenview=(ImageView)findViewById(R.id.screenview);
   
        textColorname = (TextView) findViewById(R.id.textViewA); 
        
 //       screenview.setOnLongClickListener(new piclongclickListener());
		
//		gestureListener = new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                return gestureDetector.onTouchEvent(event);
//            }
//        };
//        screenview.setOnTouchListener(gestureListener);
      

    }
    public boolean onTouchEvent(MotionEvent event){ 
        this.gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
    
    public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
    //	Toast.makeText(getApplicationContext(), "longpressed", Toast.LENGTH_LONG).show();
			zoom.setVisibility(View.VISIBLE);
			flag2=1;
	     	zoom.bringToFront();
	     	zoom.drawLayout();
	     	
	     	this.camera.setPreviewCallback(new PreviewCallback() {
                public void onPreviewFrame(byte[] _data, Camera _camera) {
              	 //support 1920,1080  1280,720  720,480  176,144   640,480  320,240
                  Camera.Parameters params = _camera.getParameters();
                     int w = params.getPreviewSize().width;
                     int h = params.getPreviewSize().height;
                     int format = params.getPreviewFormat();//20 17 4
                     YuvImage image = new YuvImage(_data, format, w, h, null);
                     ByteArrayOutputStream out = new ByteArrayOutputStream();
                     Rect area = new Rect(0, 0, w, h);
                     image.compressToJpeg(area,100, out);
                     Bitmap bm = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
                     Matrix m = new Matrix();  
                     m.postRotate(90);  
                     bitmap1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),  
                             bm.getHeight(), m, true); //bm width 640, bm height 480
                     bitmap1 = Bitmap.createScaledBitmap(bitmap1,dispWidth,dispHeight,true);
                     zoom.setbg(bitmap1);
                    // Log.i(TAG,"screenshot");
                     //screenview.setImageBitmap(bitmap);      
                }
              });
	     	
	}
	public boolean onSingleTapUp(MotionEvent e) {
		//Toast.makeText(getApplicationContext(), "tap", Toast.LENGTH_LONG).show();
		            int x = (int) e.getX();
                    int y = (int) e.getY(); 
                    PlayLocation(x,y);
			return true;
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
		return true;
	}
	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
	//	Toast.makeText(getApplicationContext(), "scroll", Toast.LENGTH_LONG).show();
        this.camera.setPreviewCallback(new PreviewCallback() {
            public void onPreviewFrame(byte[] _data, Camera _camera) {   
            	
            }
          });

		            int x = (int) e2.getX();
                    int y = (int) e2.getY(); 
                    PlayLocation(x,y);
        		return true;
	}



	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return true ;
	}
    
    public void Play(int soundRes){
    	try{
    		mMediaPlayer=MediaPlayer.create(this, soundRes);
    		Log.i(TAG,"played sound");
    		mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){
            
    			
    			@Override
				public void onPrepared(MediaPlayer mp) {
    				mp.start();
    			}		
    		});	
    		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
    		{
    			@Override
				public void onCompletion(MediaPlayer arg0){
    				mMediaPlayer.release();
    			}
    		});
    		
    		mMediaPlayer.prepare();
    	}
    		
    	catch(IllegalStateException e){
    		e.printStackTrace();
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
    }
    public void PlayLocation(int x, int y){
        float hregion = 0;
        float sregion = 0;
        float vregion = 0;
        int num = 2;
                    for (int i = 0; i < num; i++){
                    int color = bitmap1.getPixel(x+i, y+i);
                	 float[] hsv = new float[3];
                     Color.colorToHSV(color, hsv);
                	 float h = hsv[0];
                     float s = hsv[1];
                     float vv = hsv[2];
                     hregion += h;
                     sregion += s;
                     vregion += vv;
                    }
                    hregion /= num;
                    sregion /= num;
                    vregion /= num;
                    float[] colorRegion = {hregion,sregion,vregion};
                     /*textH.setText("H:"+h);
                     textS.setText("S:"+s);
                     textV.setText("V:"+vv);*/
                     textColorname.setBackgroundColor(Color.HSVToColor(colorRegion));
                     
                     if(vregion<=0.3){
                    	 textColorname.setText("Color: Black");
                    	 Play(R.raw.black);
                     }
                     else if(vregion < 0.9 && sregion<=0.1){
                    	 textColorname.setText("Color: Gray");
                    	 Play(R.raw.grey);
                     }else if(vregion >= 0.9 && sregion<=0.1){
                    	 textColorname.setText("Color: White");
                     }else if(vregion <=0.7){
                    	 if(hregion<=20 || hregion>311){
                        	 textColorname.setText("Color: Red");
                        	 Play(R.raw.red);
                         }else if(hregion>21 && hregion<=50){
                        	 textColorname.setText("Color: Orange");
                        	 Play(R.raw.orange);
                         }else if(hregion>51 && hregion<=68){
                        	 textColorname.setText("Color: Yellow");
                        	 Play(R.raw.yellow);
                         }else if(hregion>69&& hregion<=154){
                        	 textColorname.setText("Color: Green");
                        	 Play(R.raw.green);
                         }else if(hregion>155 && hregion<=198){
                        	 textColorname.setText("Color: Cyan");
                        	 Play(R.raw.cyan);
                         }else if(hregion>199 && hregion<=248){
                        	 textColorname.setText("Color: Blue");
                        	 Play(R.raw.blue);
                         }else if(hregion>249 && hregion<=310){
                        	 textColorname.setText("Color: Purple");
                        	 Play(R.raw.purple);
                         }
                     }else{
                    	 if(hregion<=20 || hregion>311){
                        	 textColorname.setText("Color: Red");
                        	 Play(R.raw.red);
                         }else if(hregion>21 && hregion<=50){
                        	 textColorname.setText("Color: Orange");
                        	 Play(R.raw.orange);
                         }else if(hregion>51 && hregion<=68){
                        	 textColorname.setText("Color: Yellow");
                        	 Play(R.raw.yellow);
                         }else if(hregion>69&& hregion<=154){
                        	 textColorname.setText("Color: Green");
                        	 Play(R.raw.green);
                         }else if(hregion>155 && hregion<=198){
                        	 textColorname.setText("Color: Cyan");
                        	 Play(R.raw.cyan);
                         }else if(hregion>199 && hregion<=248){
                        	 textColorname.setText("Color: Blue");
                        	 Play(R.raw.blue);
                         }else if(hregion>249 && hregion<=310){
                        	 textColorname.setText("Color: Purple");
                        	 Play(R.raw.purple);
                         }
                     }
    }

//new
	//int initial = 0;
    @SuppressWarnings("deprecation")
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
    	//Toast.makeText(getApplicationContext(), "initail=0", Toast.LENGTH_LONG).show();
    	
		// TODO Auto-generated method stub
		try
        {
			final Display display = getWindowManager().getDefaultDisplay();
                this.camera = Camera.open();
                camera.setDisplayOrientation(90);
                this.camera.setPreviewDisplay(this.holder);
                //new
              //  if(initial == 0){
                this.camera.setPreviewCallback(new PreviewCallback() {
                  public void onPreviewFrame(byte[] _data, Camera _camera) {
                	 //support 1920,1080  1280,720  720,480  176,144   640,480  320,240
                    Camera.Parameters params = _camera.getParameters();
                    params.setPreviewSize(480, 320);
//        	        List<Size> sizes = params.getSupportedPreviewSizes();
//       	         int maxSize = 0;
//       	         int width = 0;
//       	         int height = 0;
//       	        for (int i = 0; i < sizes.size(); i++) {
//       	             Size size = sizes.get(i);
//       	                 width = size.width;
//       	                height = size.height;
//       	                System.out.println("w"+width+","+"h"+height);
//       	         }
                       int w = params.getPreviewSize().width;
                       int h = params.getPreviewSize().height;
                       int format = params.getPreviewFormat();//20 17 4
                       YuvImage image = new YuvImage(_data, format, w, h, null);
                       ByteArrayOutputStream out = new ByteArrayOutputStream();
                       Rect area = new Rect(0, 0, w, h);
                       image.compressToJpeg(area,100, out);
                       Bitmap bm = BitmapFactory.decodeByteArray(out.toByteArray(), 0, out.size());
                       Matrix m = new Matrix();  
                       m.postRotate(90);  
                       bitmap1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),  
                               bm.getHeight(), m, true); //bm width 640, bm height 480
                       bitmap1 = Bitmap.createScaledBitmap(bitmap1,dispWidth,dispHeight,true);
                       zoom.setbg(bitmap1);
                      // Log.i(TAG,"screenshot");
                       //screenview.setImageBitmap(bitmap);      
                  }
                });
                //new
             //   initial = 1;
              //  }

        }
        catch(IOException ioe)
        {
                ioe.printStackTrace(System.out);
        }
	}

	@SuppressWarnings("deprecation")
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		 this.camera.startPreview();
		
		
	}

	@SuppressWarnings("deprecation")
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		  this.camera.stopPreview();
          this.camera.release();
          this.camera = null;
		// TODO Auto-generated method stub
		
	}

	
}
