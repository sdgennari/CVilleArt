~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
To Make the Code Easy to Read for All Devs,
	Please Embrace a Barebones Versionof JavaDoc Notation:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Common Tags:
@author [name]--		use with classes and interfaces
	(whenever you add code to a class, be sure to add your name at the top)
@param [var] [desc]--	specify the parameters for a method
@return [desc]--		specify the return object for a method
@throws [exception]--	briefly describe the exception being thrown

Format:
[see example below for more info]
Start:	'/**'
Text:	Enter as lines of plain text beginning with '*'
Tags:	Enter tag as shown with '@' symbol
End:	'*/'
 

===============================================================================
----------------------------------< Example >----------------------------------
===============================================================================

//Below is code from a .java file showing the use of JavaDoc comments

/**
 * Returns an Image object that can then be painted on the screen. 
 * The url argument must specify an absolute {@link URL}. The name
 * argument is a specifier that is relative to the url argument. 
 * <p>
 * This method always returns immediately, whether or not the 
 * image exists. When this applet attempts to draw the image on
 * the screen, the data will be loaded. The graphics primitives 
 * that draw the image will incrementally paint on the screen. 
 *
 * @param  url  an absolute URL giving the base location of the image
 * @param  name the location of the image, relative to the url argument
 * @return      the image at the specified URL
 * @see         Image
 */
 public Image getImage(URL url, String name) {
        try {
            return getImage(new URL(url, name));
        } catch (MalformedURLException e) {
            return null;
        }
 }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
App Architecture: A Brief Overview:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
[] MainActivity- Main activity for the app, called when the app first starts up

[] HomeScreenFragment- Fragment initially created when the app starts. It
	provides a list of the potential app screens

[] NearMeFragment- Fragment to display location-specific art in CVille

[] DiscoverFragment- Fragment to list various art attractions

[] TransportationFragment- Fragment to provide information about various
	transportation routes to and from art venues

[] EventFragment- Fragment to display upcoming events in CVille in a calendar
