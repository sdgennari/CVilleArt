package com.hooapps.pca.cvilleart.CustomElems;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Customized ImageView to display rounded images with shadows. Takes a 
 * pre-loaded, regular bitmap and scales in accordingly to a set size.
 * 
 * @author Spencer Gennari
 */

public class RoundedImageView extends ImageView {
	
	// Size of the inner circle
	private static final int DIAMETER = 48;
	
	// Size of the overall image
	private static final int SIZE = 64;
	
	private float density;
	private int size;
	private int radius;
	
	public RoundedImageView(Context context) {
		super(context);
		initVars();
	}
	
	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initVars();
	}
	
	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVars();
	}
	
	/**
	 * Initializes the dimensions for the image based on the screen density.
	 */
	public void initVars() {
		density = getResources().getDisplayMetrics().density;
		size = (int) (SIZE * density);
		radius = (int) (DIAMETER/2 * density);
	}
	
	// When the image is first set, resize it accordingly
	// Do this here to avoid repeating the same task every time the image is drawn
	/**
	 * Sets the image to be displayed in the ImageView.
	 * 
	 * @param bmp The original, unmodified bitmap to be displayed. The image 
	 * is modified accordingly in this method. 
	 */
	@Override
	public void setImageBitmap(Bitmap bmp) {
		// Focus on the center part of the image
		Log.d("IMG", "Cropping bitmap...");
		bmp = scaleAndCropBitmap(bmp);
		
		// Draw the image boarder, shadows, etc.
		Log.d("IMG", "Drawing modified bitmap...");
		bmp = getModifiedBitmap(bmp);
		
		// Set bmp as the Bitmap for the ImageView
		Log.d("IMG", "Setting bitmap...");
		super.setImageBitmap(bmp);
	}
	
	/**
	 * Crops the bitmap to focus on the center. Scales the newly cropped 
	 * section to fit in the circle inside the ImageView.
	 * 
	 * @param bmp The original bitmap to be scaled and cropped
	 * @return The modified bitmap that has been scaled to fit in the radius
	 */
	private Bitmap scaleAndCropBitmap(Bitmap bmp) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		Bitmap dest;
		// If the width is larger, scale based on height
		// Else, scale based on width
		if(width > height) {
			dest = Bitmap.createBitmap(bmp, (width-height)/2, 0, height, height);
		} else {
			dest = Bitmap.createBitmap(bmp, 0, (height-width)/2, width, width);
		}
		
		// Adjust for the density of pixels on the screen
		dest = Bitmap.createScaledBitmap(dest, radius*2, radius*2, false);
		return dest;
	}
	
	
	/**
	 * Draws the bitmap as a circle and creates the border and shadow effect.
	 * 
	 * @param bmp A pre-scaled version of the bitmap
	 * @return The finished bitmap, complete with border and shadow.
	 */
	private Bitmap getModifiedBitmap(Bitmap bmp) {
		Bitmap output = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		
		// Draw the shadow
		int gradRadius = size/2;
		RadialGradient gradient = new RadialGradient(size/2, size/2, gradRadius, 0xFF000000, 0x00000000, TileMode.CLAMP);
		paint.setDither(true);
		paint.setShader(gradient);
		canvas.drawCircle(size/2, size/2, gradRadius, paint);
		
		// Draw the white border circle
		paint.setColor(0xFFFFFFFF);
		paint.setShader(null);
		int circleRadius = radius + (size/2-radius)/4;
		canvas.drawCircle(size/2, size/2, circleRadius, paint);
		
		// Draw the bitmap as a circle in the center of the ImageView
		BitmapShader bitmapShader = new BitmapShader(bmp, TileMode.CLAMP, TileMode.CLAMP);
		paint.setShader(bitmapShader);
		canvas.drawCircle(size/2, size/2, radius, paint);
		
		return output;
	}

}
