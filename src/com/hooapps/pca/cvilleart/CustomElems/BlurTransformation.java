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
		// Create a new bitmap to hold the result
		Bitmap result = Bitmap.createBitmap(source);
		
		// Allocate resources for use with RenderScript
		Allocation input = Allocation.createFromBitmap(rs, result, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
		Allocation output = Allocation.createTyped(rs, input.getType());
		
		// Load the instance of blur and set the radius
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		blurScript.setInput(input);
		blurScript.setRadius(30);
		
		// Perform the blur on the output
		blurScript.forEach(output);
		
		// Copy the blurred bitmap to the destination
		output.copyTo(result);
		source.recycle();
		
		return result;
	}

	@Override
	public String key() {
		return "blurTransformation()";
	}
}
