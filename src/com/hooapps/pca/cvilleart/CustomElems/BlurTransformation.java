package com.hooapps.pca.cvilleart.CustomElems;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import com.squareup.picasso.Transformation;

public class BlurTransformation implements Transformation {
	
	private RenderScript rs;
	
	public BlurTransformation(Context context) {
		rs = RenderScript.create(context);
	}
	
	@Override
	public Bitmap transform(Bitmap source) {
		
        // Create another bitmap that will hold the results of the filter.
        Bitmap blurredBitmap = Bitmap.createBitmap(source);
 
        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(rs, input.getType());
 
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);
 
        // Set the blur radius
        script.setRadius(25);
 
        // Start the ScriptIntrinisicBlur
        // Max blur radius is 25, so run twice
        script.forEach(output);
        script.forEach(output);
 
        // Copy the output to the blurred bitmap
        output.copyTo(blurredBitmap);
        if (blurredBitmap != source) {
        	source.recycle();	
        }
 
        return blurredBitmap;
		/*
		// Create a new bitmap to hold the result
		Bitmap result = Bitmap.createBitmap(source, 0, 0, 800, 400);
		
		if (result != source) {
			source.recycle();
		}
		
		// Allocate resources for use with RenderScript
		Allocation input = Allocation.createFromBitmap(rs, source, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SCRIPT);
		Allocation output = Allocation.createTyped(rs, input.getType());
		
		// Load the instance of blur and set the radius
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		blurScript.setInput(input);
		blurScript.setRadius(10);
		
		// Perform the blur on the output
		blurScript.forEach(output);
		
		// Copy the blurred bitmap to the destination
		output.copyTo(result);
		return result;
		*/
	}

	@Override
	public String key() {
		return "blurTransformation()";
	}
}
