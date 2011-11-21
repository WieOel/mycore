PanoJS.MSG_BEYOND_MIN_ZOOM = 'component.iview2.panojs.minzoom';
PanoJS.MSG_BEYOND_MAX_ZOOM = 'component.iview2.panojs.maxzoom';

PanoJS.prototype.inputHandlerEnabled=true;
/**
 * @function
 * @memberOf iview.General
 * @name isInputHandlerEnabled
 * @returns true if input events (keyboard, mouse) are captured
 */
PanoJS.prototype.isInputHandlerEnabled = function() {
  return this.inputHandlerEnabled;
};
 
/**
 * @function
 * @memberOf iview.General
 * @name disableInputHandler
 * @description disable input events
 */
PanoJS.prototype.disableInputHandler = function() {
  this.inputHandlerEnabled=false;
};
 
 /**
 * @function
 * @memberOf iview.General
 * @name enableInputHandler
 * @description enable input events
 */
PanoJS.prototype.enableInputHandler = function() {
  this.inputHandlerEnabled=true;
};

/**
 * @public
 * @function
 * @memberOf	PanoJS
 * @name		zoomCenter
 * @description	Zooms the given Viewer so that the given point will be in center of view
 * @param		{integer} direction to zoom in = 1 out = -1
 * @param		{object} point coordinates to center
 * @param		{integer} point.x x-coordinate to center
 * @param		{integer} point.y y-coordinate to center
 */
PanoJS.prototype.zoomCenter = function(direction, point) {
	var viewer = this;
	var preload = this.iview.preload;
	var preDim = {"x" :toInt(preload.left()),"y":toInt(preload.top()), "width":preload.width(), "height":preload.height()};
	viewer.zoom(direction);
	var newDim = {"width":preload.width(), "height":preload.height()};
	viewer.x = 0;
	viewer.y = 0;
	var npoint ={'x': ((-preDim.x + point.x) / preDim.width) * newDim.width,
				'y': ((-preDim.y + point.y) / preDim.height) * newDim.height};
	viewer.resetSlideMotion();
	viewer.recenter(npoint,true);
}

/**
 * @public
 * @function
 * @name		zoomViewer
 * @memberOf	PanoJS
 * @description	handles the direction of zooming in the viewer
 * @param 		{boolean} direction: true = zoom in, false = zoom out
 */
PanoJS.prototype.zoomViewer = function(direction) {
	var dir = 0;
	if (direction) {
		//if zoomWidth or zoomScreen was active and we're already in the max zoomlevel just reset the displayMode
		if (this.iview.currentImage.zoomInfo.zoomScreen) {
			this.pictureScreen(true);
		} else if (this.iview.currentImage.zoomInfo.zoomWidth) {
			this.pictureWidth(true);
		}
		if (this.zoomLevel != this.iview.currentImage.zoomInfo.maxZoom) {
			dir = 1;
		}
	} else {
		dir = -1;
	}
	this.zoomCenter(dir, {"x":this.width/2, "y":this.height/2});
}

/**
 * @public
 * @function
 * @name		pictureWidth
 * @memberOf	PanoJS
 * @description	calculates how the tilesize has to be so that the picture fully fits into the viewer Area, tiles used are the nearest zoomlevel to the available viewerwidth which is smaller than the viewerwidth
 * @param 		{boolean} [preventLooping] optional tells if the function is called from the Zoombar or any Function which is connected to it or not and prevents infite loop
 */
PanoJS.prototype.pictureWidth = function(preventLooping){
	var bool = (typeof (preventLooping) != undefined)? preventLooping:false;
	this.iview.currentImage.zoomInfo.zoomWidth = this.switchDisplayMode(false, this.iview.currentImage.zoomInfo.zoomWidth, bool);
}

/**
 * @public
 * @function
 * @name		pictureScreen
 * @memberOf	PanoJS
 * @description	calculates how the tilesize has to be so that the picture fully fits into the viewer Area, tiles used are the nearest zoomlevel to the available viewerspace which is smaller than the viewerspace
 * @param 		{boolean} [preventLooping] optional tells if the function is called from the Zoombar or any Function which is connected to it or not and prevents infite loop
 */
PanoJS.prototype.pictureScreen = function(preventLooping){
	var bool = (typeof (preventLooping) != undefined)? preventLooping:false;
	this.iview.currentImage.zoomInfo.zoomScreen = this.switchDisplayMode(true, this.iview.currentImage.zoomInfo.zoomScreen, bool);
}

/**
 * @public
 * @function
 * @name		switchDisplayMode
 * @memberOf	PanoJS
 * @description	calculates how the picture needs to be scaled so that it can be displayed within the display-area as the mode requires it
 * @param		{boolean} screenZoom defines which displaymode will be calculated
 * @param		{boolean} statebool holds the value which defines if the current mode is set or needs to be set
 * @param 		{boolean} [preventLooping] optional tells if the function is called from the Zoombar or any Function which is connected to it or not and prevents infite loop
 * @return		boolean which holds the new StateBool value, so it can be saved back into the correct variable
 */
PanoJS.prototype.switchDisplayMode = function(screenZoom, stateBool, preventLooping) {
	//this is viewerBean
	if (screenZoom) {
		this.iview.currentImage.zoomInfo.zoomWidth = false;
	} else {
		this.iview.currentImage.zoomInfo.zoomScreen = false;
	}
	stateBool = (stateBool)? false: true;
	this.clear();
	this.removeScaling();
	if (stateBool) {
		for (var i = 0; i <= this.iview.currentImage.zoomInfo.maxZoom; i++) {
			if(this.iview.currentImage.width/this.width > this.iview.currentImage.height/this.iview.context.viewer.outerHeight(true) || (stateBool && !screenZoom)){
			//Width > Height Or ZoomWidth is true
				if (this.calculateZoomProp(i, this.iview.currentImage.width, this.width, 0)) {
					break;
				}
			} else {
				if (this.calculateZoomProp(i, this.iview.currentImage.height, this.height, 0)) {
					break;
				}
			}
		}
		this.init();
	} else {
		this.iview.currentImage.zoomInfo.scale = 1;
		this.tileSize = this.iview.properties.tileSize;
		this.init();
		
		//an infinite loop would arise if the repeal of the zoombar comes
		if (typeof (preventLooping) == "undefined" || preventLooping == false) {
			this.zoom(this.iview.currentImage.zoomInfo.zoomBack - this.zoomLevel);
		}
	}

	return stateBool;
}

/**
 * @public
 * @function
 * @name		calculateZoomProp
 * @memberOf	PanoJS
 * @description	calculates how the TileSize and the zoomvalue needs to be if the given zoomlevel fits into the viewer
 * @param		{integer} level the zoomlevel which is used for testing
 * @param		{integer} totalSize the total size of the Picture Dimension X or Y
 * @param		{integer} viewerSize the Size of the Viewer Dimension X or Y
 * @param		{integer} scrollBarSize the Height or Width of the ScrollBar which needs to be dropped from the ViewerSize
 * @return		boolean which tells if it was successfull to scale the picture in the current zoomlevel to the viewer Size
 */
PanoJS.prototype.calculateZoomProp = function(level, totalSize, viewerSize, scrollBarSize) {
	if ((totalSize / Math.pow(2, level)) <= viewerSize) {
		if (level != 0) {
			level--;
		}
		var currentWidth = totalSize / Math.pow(2, level);
		var viewerRatio = viewerSize / currentWidth;
		var fullTileCount = Math.floor( currentWidth / this.iview.properties.tileSize);
		var lastTileWidth = currentWidth - fullTileCount * this.iview.properties.tileSize;
		this.iview.currentImage.zoomInfo.scale = viewerRatio; //determine the scaling ratio
		level = this.iview.currentImage.zoomInfo.maxZoom - level;
		this.tileSize = Math.floor((viewerSize - viewerRatio * lastTileWidth) / fullTileCount);
		this.iview.currentImage.zoomInfo.zoomBack = this.zoomLevel;
		this.zoom(level - this.zoomLevel);
		return true;
	}
	return false;
}

/**
 * @public
 * @function
 * @name		removeScaling
 * @memberOf	PanoJS
 * @description	saves the scaling of loaded tiles if picture fits to height or to width (for IE)
 */
PanoJS.prototype.removeScaling = function() {
	for (var img in this.images) {
		this.images[img]["scaled"] = false;
	}
}

/**
 * @public
 * @function
 * @name		isloaded
 * @memberOf	PanoJS
 * @description	checks if the picture is loaded
 * @param		{object} img
 */
PanoJS.prototype.isloaded = function(img) {
	/*
	NOTE tiles are not displayed correctly in Opera, because the used accuracy for pixel values only has 
	2 decimal places, however 3 are necessary for the correct representation as in FF
	*/
	if (!this.images[img.src]) {
		this.images[img.src] = new Object();
		this.images[img.src]["scaled"] = false;
		img.style.display = "none";
	}
	if (((img.naturalWidth == 0 && img.naturalHeight == 0)  && !isBrowser(["IE", "Opera"])) || (!img.complete && isBrowser(["IE", "Opera"]))) {
		if (img.src.indexOf("blank.gif") == -1) {//change
			var that = this;
			window.setTimeout(function(image) { return function(){that.isloaded(image);} }(img), 100);
		}
	} else if (img.src.indexOf("blank.gif") == -1) {
		if (this.images[img.src]["scaled"] != true) {
			img.style.display = "inline";
			this.images[img.src]["scaled"] = true;//notice that this picture already was scaled
			//TODO math Floor rein bauen bei Höhe und Breite
		  var zoomScale=this.iview.currentImage.zoomInfo.scale;
			if (!isBrowser(["IE","Opera"])) {
				img.style.width = zoomScale * img.naturalWidth + "px";
				img.style.height = zoomScale * img.naturalHeight + "px";
			} else {
				if (!this.images[img.src]["once"]) {
					this.images[img.src]["once"] = true;
					this.images[img.src]["naturalheight"] = img.clientHeight;
					this.images[img.src]["naturalwidth"] = img.clientWidth;
				}
				img.style.width = zoomScale * this.images[img.src]["naturalwidth"] + "px";
				img.style.height = zoomScale * this.images[img.src]["naturalheight"] + "px";
			}
		}
	}
	img = null;
}

PanoJS.prototype.blank = function() {
	var iterator = this.cache.iterator();
	var entry;
	while (iterator.hasNext()) {
		entry = iterator.next();
		if (entry.value.parentNode != null) {
			entry.value.parentNode.removeChild(entry.value);
			entry.value.parentNode = null;
		}
	}
	for (imgId in this.cche) {
		var img = this.cche[imgId];
		img.onload = function() {};
		if (img.image) {
			img.image.onload = function() {};
		}

		if (img.parentNode != null) {
			this.well.removeChild(img);
		}
	}
}

PanoJS.prototype.zoom = function (direction) {
	// ensure we are not zooming out of range
	if (this.zoomLevel + direction < 0) {
		if (PanoJS.MSG_BEYOND_MIN_ZOOM) {
			alert(i18n.translate(PanoJS.MSG_BEYOND_MIN_ZOOM));
		}
		return;
	}
	else if (this.zoomLevel + direction > this.maxZoomLevel) {
		if (PanoJS.MSG_BEYOND_MAX_ZOOM) {
			alert(i18n.translate(PanoJS.MSG_BEYOND_MAX_ZOOM));
		}
		return;
	}

	this.blank();

	var coords = { 'x' : Math.floor(this.width / 2), 'y' : Math.floor(this.height / 2) };

	var before = {
		'x' : (coords.x - this.x),
		'y' : (coords.y - this.y)
	};

	var after = {
		'x' : Math.floor(before.x * Math.pow(2, direction)),
		'y' : Math.floor(before.y * Math.pow(2, direction))
	};

	this.x = coords.x - after.x;
	this.y = coords.y - after.y;
	this.zoomLevel += direction;

	this.notifyViewerZoomed();
	this.positionTiles();
};

//IE and Opera doesn't accept our TileUrlProvider Instance as one of PanoJS
PanoJS.isInstance = function () {return true;};

PanoJS.mousePressedHandler = function(e) {
  var that = this.backingBean.iview;
  if (this.backingBean.isInputHandlerEnabled()){
  	e = getEvent(e);
  	if (that.viewerContainer.isMax()) {
  		// only grab on left-click
  		if (e.button < 2) {
  			var self = this.backingBean;
  			var coords = self.resolveCoordinates(e);
  			self.press(coords);
  		}
  	} else {
  		that.toggleViewerMode();
  	}
  	// NOTE: MANDATORY! must return false so event does not propagate to well!
  	return false;
  }
}

PanoJS.doubleClickHandler = function(e) {
	var iview = this.backingBean.iview;
	if (iview.viewerContainer.isMax() && this.backingBean.isInputHandlerEnabled()) {
		e = getEvent(e);
		var self = this.backingBean;
		coords = self.resolveCoordinates(e);
		if (self.zoomLevel < self.maxZoomLevel) {
			this.backingBean.zoomCenter(1,coords);
		} else {
			self.resetSlideMotion();
			self.recenter(coords);
		}
	}
}

PanoJS.keyboardHandler = function(e) {
	e = getEvent(e);
	if (iview.credits)
		iview.credits(e);
	for (var i in PanoJS.VIEWERS){
		var viewer = PanoJS.VIEWERS[i];
		if (this.backingBean.isInputHandlerEnabled()){
		if (e.keyCode >= 37 && e.keyCode <=40) {
			//cursorkey movement
			var motion = {
			'x': PanoJS.MOVE_THROTTLE * (e.keyCode % 2) * (38 - e.keyCode),
			'y': PanoJS.MOVE_THROTTLE * ((39 - e.keyCode) % 2)};
		  	if (viewer.iview.viewerContainer.isMax()){
				viewer.positionTiles(motion, true);
				viewer.notifyViewerMoved(motion);
		  	}
			preventDefault(e);
			return false;
		} else if ([109,45,189,107,61,187,144,27].indexOf(e.keyCode)>=0) {
			var dir = 0;
			//+/- Buttons for Zooming
			//107 and 109 NumPad +/- supported by all, other keys are standard keypad codes of the given Browser
			if (e.keyCode == 109 || (e.keyCode == 45 && isBrowser("opera")) || e.keyCode == 189) {
				dir = -1;
			} else if (e.keyCode == 107 || e.keyCode == 61 || (isBrowser(["Chrome", "IE"]) && e.keyCode == 187) || (isBrowser("Safari") && e.keyCode == 144)) {
				dir = 1;
			} else if (e.keyCode == 27) {
				if (viewer.iview.viewerContainer.isMax()){
					viewer.iview.maximizeHandler();
				}
			}
			
			if (dir != 0 && viewer.iview.viewerContainer.isMax()) {
				viewer.iview.viewerBean.zoomCenter(dir,{"x":viewer.width/2, "y":viewer.height/2}); 
				preventDefault(e);
				e.cancelBubble = true;
				return false;
			}
		  }//zoom
		}//input events enabled
	}//every viewer
}

/*
 * calculate simple image name hash value to spread request over different servers
 * but allow browser cache to be used by allways return the same value for a given name 
 */
PanoJS.TileUrlProvider.prototype.imageHashes = [];
PanoJS.TileUrlProvider.prototype.getImageHash = function(image){
	if (this.imageHashes[image]){
		return this.imageHashes[image];
	}
	var hash=0;
	var pos=image.lastIndexOf(".");
	if (pos < 0)
		pos=image.length;
	for (var i=0;i<pos;i++){
		hash += 3 * hash + (image.charCodeAt(i)-48);
	}
	this.imageHashes[image]=hash;
	return hash;
};
/*
 * returns the URL of all tileimages
 */
PanoJS.TileUrlProvider.prototype.assembleUrl = function(xIndex, yIndex, zoom, image){
	image=(image == null)? this.getCurrentImage().name : image;
    return this.baseUri[(this.getImageHash(image)+xIndex+yIndex) % this.baseUri.length] + '/'+ this.derivate+'/' + 
        image + '/' + zoom + '/' + yIndex + '/' + xIndex + '.' + this.extension +
        (PanoJS.REVISION_FLAG ? '?r=' + PanoJS.REVISION_FLAG : '');
};