module mycore.viewer.widgets.canvas {

    export class TileImagePage implements model.AbstractPage {

        constructor(public id:string, private _width:number, private _height:number, _tilePaths:Array<string>) {
            this._tilePath = _tilePaths;
            this.loadTile(new Position3D(0, 0, 0));

        }

        private static TILE_SIZE = 256;
        private _tilePath: string[];
        private _tiles: MyCoReMap<Position3D, HTMLImageElement> = new MyCoReMap<Position3D, HTMLImageElement>();
        private _loadingTiles: MyCoReMap<Position3D, HTMLImageElement> = new MyCoReMap<Position3D, HTMLImageElement>();


        private _backBuffer: HTMLCanvasElement = document.createElement("canvas");
        private _backBufferArea: Rect = null;
        private _backBufferAreaZoom: number = null;

        private _previewBackBuffer: HTMLCanvasElement = document.createElement("canvas");
        private _previewBackBufferArea: Rect = null;
        private _previewBackBufferAreaZoom: number = null;



        private _imgPreviewLoaded: boolean = false;
        private _imgNotPreviewLoaded: boolean = false;

        private _altoContent:HTMLElement;
        private _rootElem:HTMLElement;


        public get size(): Size2D {
            return new Size2D(this._width, this._height);
        }

        public refreshCallback: () => void;

        public draw(ctx: CanvasRenderingContext2D, rect: Rect, scale, overview: boolean): void {
            if (rect.pos.x < 0 || rect.pos.y < 0) {
                rect = new Rect(rect.pos.max(0, 0), rect.size);
            }

            var zoomLevel = Math.min(this.getZoomLevel(scale), this.maxZoomLevel());
            var zoomLevelScale = this.scaleForLevel(zoomLevel);

            var diff = scale / zoomLevelScale;

            var tileSizeInZoomLevel = TileImagePage.TILE_SIZE / zoomLevelScale;

            var startX = Math.floor(rect.pos.x / tileSizeInZoomLevel);
            var startY = Math.floor(rect.pos.y / tileSizeInZoomLevel);
            var endX = Math.ceil(Math.min(rect.pos.x + rect.size.width, this.size.width) / tileSizeInZoomLevel);
            var endY = Math.ceil(Math.min(rect.pos.y + rect.size.height, this.size.height) / tileSizeInZoomLevel);

            this._updateBackbuffer(startX, startY, endX, endY, zoomLevel, overview);

            ctx.save();
            {
                var xBase = (startX * tileSizeInZoomLevel - rect.pos.x) * scale;
                var yBase = (startY * tileSizeInZoomLevel - rect.pos.y) * scale;
                ctx.translate(xBase, yBase);
                ctx.scale(diff, diff);
                if (overview) {
                    ctx.drawImage(this._previewBackBuffer, 0, 0);
                } else {
                    ctx.drawImage(this._backBuffer, 0, 0);
                }
            }
            ctx.restore();

        }


        public setAltoContent(value:HTMLElement) {
            if (value != this._altoContent) {
                this._altoContent = value;
                this.updateHTML();
            }
        }

        public registerHTMLPage(elem:HTMLElement) {
            this._rootElem = elem;
            this.updateHTML();
        }

        private updateHTML() {
            if (this._altoContent != null && this._rootElem != null) {
                while (this._rootElem.children.length > 0) {
                    this._rootElem.removeChild(this._rootElem.children.item(0));
                }
                this._rootElem.appendChild(this._altoContent);
            }
            if (typeof this.refreshCallback != "undefined" && this.refreshCallback != null) {
                this.refreshCallback();
            }
        }

        public clear() {
            this._abortLoadingTiles();
            this._backBuffer.width = 1;
            this._backBuffer.height = 1;
            this._backBufferAreaZoom = null;

            var previewTilePos = new Position3D(0, 0, 0);
            var hasPreview = this._tiles.has(previewTilePos);

            if (hasPreview) {
                var tile = this._tiles.get(previewTilePos);
            }

            this._tiles.clear();

            if (hasPreview) {
                this._tiles.set(previewTilePos, tile);
            }


            this._loadingTiles.clear();
        }


        private _updateBackbuffer(startX, startY, endX, endY, zoomLevel, overview: boolean) {
            var newBackBuffer = new Rect(new Position2D(startX, startY), new Size2D(endX - startX, endY - startY));
            if (overview) {
                if (this._previewBackBufferArea !== null && !this._imgPreviewLoaded && this._previewBackBufferArea.equals(newBackBuffer) && zoomLevel == this._previewBackBufferAreaZoom) {
                    return;
                } else {
                    this._previewBackBuffer.width = newBackBuffer.size.width * 256;
                    this._previewBackBuffer.height = newBackBuffer.size.height * 256;
                    this._drawToBackbuffer(startX, startY, endX, endY, zoomLevel, true);
                }
                this._previewBackBufferArea = newBackBuffer;
                this._previewBackBufferAreaZoom = zoomLevel;
                this._imgPreviewLoaded = false;
            } else {
                if (this._backBufferArea !== null && !this._imgNotPreviewLoaded && this._backBufferArea.equals(newBackBuffer) && zoomLevel == this._backBufferAreaZoom) {
                    // backbuffer content is the same
                    return;
                } else {
                    this._abortLoadingTiles();
                    // need to draw the full buffer, because zoom level changed or never drawed before
                    this._backBuffer.width = newBackBuffer.size.width * 256;
                    this._backBuffer.height = newBackBuffer.size.height * 256;
                    this._drawToBackbuffer(startX, startY, endX, endY, zoomLevel, false);
                }
                this._backBufferArea = newBackBuffer;
                this._backBufferAreaZoom = zoomLevel;
                this._imgNotPreviewLoaded = false;
            }


            /*
             else {
             // zoom level is the same, so look for copy old contents
             var reusableContent = this._backBufferArea.getIntersection(newBackBuffer);
             if (reusableContent == null) {
             // content complete changed :(
             this._drawToBackbuffer(startX, startY, endX, endY, zoomLevel);
             } else {
             // we can copy old content \o/
             // calculate were the old content is in the new backbuffer (in px)
             var xTranslate = reusableContent.pos.x - newBackBuffer.pos.x * 256;
             var yTranslate = reusableContent.pos.y - newBackBuffer.pos.y * 256;

             var ctx = this._backBuffer.getContext("2d");
             ctx.save();
             ctx.translate(xTranslate, yTranslate);
             this._drawToBackbuffer(reusableContent.pos.x, reusableContent.pos.y, reusableContent.pos.x + reusableContent.size.width, reusableContent.pos.y + reusableContent.size.height, zoomLevel);
             ctx.restore();
             }
             }         */


        }

        private static EMPTY_FUNCTION = ()=> {
        };

        private _abortLoadingTiles() {
            this._loadingTiles.forEach((k, v) => {
                v.onerror = TileImagePage.EMPTY_FUNCTION;
                v.onload = TileImagePage.EMPTY_FUNCTION;
                v.src = "#";
            });
            this._loadingTiles.clear();
        }


        private _drawToBackbuffer(startX, startY, endX, endY, zoomLevel, _overview: boolean) {
            var ctx: CanvasRenderingContext2D;
            if (_overview) {
                ctx = <CanvasRenderingContext2D> this._previewBackBuffer.getContext("2d");
            } else {
                ctx = <CanvasRenderingContext2D> this._backBuffer.getContext("2d");
            }

            for (var x = startX; x < endX; x++) {
                for (var y = startY; y < endY; y++) {
                    var tilePosition = new Position3D(x, y, zoomLevel);
                    var tile = this.loadTile(tilePosition);
                    var rasterPositionX = (x - startX) * 256;
                    var rasterPositionY = (y - startY) * 256;

                    if (tile != null) {
                        ctx.drawImage(tile, Math.floor(rasterPositionX), rasterPositionY, tile.naturalWidth, tile.naturalHeight);
                    } else {
                        var preview = this.getPreview(tilePosition);
                        if (preview != null) {
                            this.drawPreview(ctx, new Position2D(rasterPositionX, rasterPositionY), preview);
                        }
                    }
                }
            }
        }

        private drawPreview(ctx: CanvasRenderingContext2D, targetPosition: Position2D, tile: PreviewTile) {
            tile.areaToDraw.size.width = Math.min(tile.areaToDraw.pos.x + tile.areaToDraw.size.width, tile.tile.naturalWidth) - tile.areaToDraw.pos.x;
            tile.areaToDraw.size.height = Math.min(tile.areaToDraw.pos.y + tile.areaToDraw.size.height, tile.tile.naturalHeight) - tile.areaToDraw.pos.y;

            ctx.drawImage(tile.tile,
                tile.areaToDraw.pos.x,
                tile.areaToDraw.pos.y,
                tile.areaToDraw.size.width,
                tile.areaToDraw.size.height,
                targetPosition.x,
                targetPosition.y,
                tile.areaToDraw.size.width * tile.scale,
                tile.areaToDraw.size.height * tile.scale
                );
        }

        private loadTile(tilePos: Position3D) {
            if (this._tiles.has(tilePos)) {
                return this._tiles.get(tilePos);
            } else {
                if (!this._loadingTiles.has(tilePos)) {
                    this._loadTile(tilePos, (img: HTMLImageElement) => {
                        this._tiles.set(tilePos, img);
                        if (typeof this.refreshCallback != "undefined" && this.refreshCallback != null) {
                            this._imgPreviewLoaded = true;
                            this._imgNotPreviewLoaded = true;
                            this.refreshCallback();
                        }
                    }, () => {
                            console.error("Could not load tile : " + tilePos.toString());
                        });
                }

            }

            return null;
        }

        /**
         * Gets a preview draw instruction for a specific tile.
         * @param tilePos the tile
         * @returns { tile:HTMLImageElement; areaToDraw: Rect } tile contains the Image to draw and areaToDraw contains the coordinates in the Image.
         */
        private getPreview(tilePos: Position3D, scale: number = 1): PreviewTile {
            if (this._tiles.has(tilePos)) {
                var tile = this._tiles.get(tilePos);
                return { tile: tile, areaToDraw: new Rect(new Position2D(0, 0), new Size2D(256, 256)), scale: scale };
            } else {
                var newZoom = tilePos.z - 1;

                if (newZoom < 0) {
                    return null;
                }

                var newPos = new Position2D(Math.floor(tilePos.x / 2), Math.floor(tilePos.y / 2));
                var xGridPos = tilePos.x % 2;
                var yGridPos = tilePos.y % 2;

                var prev = this.getPreview(new Position3D(newPos.x, newPos.y, newZoom), scale * 2);
                if (prev != null) {
                    var newAreaSize = new Size2D(prev.areaToDraw.size.width / 2, prev.areaToDraw.size.height / 2);
                    var newAreaPos = new Position2D(
                        prev.areaToDraw.pos.x + (newAreaSize.width * xGridPos),
                        prev.areaToDraw.pos.y + (newAreaSize.height * yGridPos)
                        );

                    return {
                        tile: prev.tile,
                        areaToDraw: new Rect(newAreaPos, newAreaSize),
                        scale: prev.scale
                    };
                } else {
                    return null;
                }
            }
        }

        private maxZoomLevel(): number {
            return Math.ceil(Math.log(Math.max(this._width, this._height) / TileImagePage.TILE_SIZE) / Math.LN2);
        }

        private getZoomLevel(scale: number): number {
            return Math.max(0, Math.ceil(this.maxZoomLevel() - Math.log(scale) / Utils.LOG_HALF));
        }

        private scaleForLevel(level: number): number {
            return Math.pow(0.5, this.maxZoomLevel() - level);
        }

        private _loadTile(tilePos: Position3D, okCallback: (image: HTMLImageElement) => void, errorCallback: () => void): void {
            var pathSelect = Utils.hash(tilePos.toString()) % this._tilePath.length;

            var path = this._tilePath[pathSelect];
            var image = new Image();


            image.onload = () => {
                this._loadingTiles.remove(tilePos);
                okCallback(image);
            };

            image.onerror = () => {
                errorCallback();
            };

            image.src = ViewerFormatString(path, tilePos);
            this._loadingTiles.set(tilePos, image);
        }

        toString(): string {
            return this._tilePath[0];
        }
    }

    interface PreviewTile {
        tile: HTMLImageElement;
        areaToDraw: Rect;
        scale: number;
    }


}


var json = {
    "resources" : {
        "script" : [ "http://archive.thulb.uni-jena.de/hisbest/modules/iview2/js/iview-client-base.js", "http://archive.thulb.uni-jena.de/hisbest/modules/iview2/js/iview-client-desktop.js", "http://archive.thulb.uni-jena.de/hisbest/modules/iview2/js/iview-client-mets.js", "http://archive.thulb.uni-jena.de/hisbest/modules/iview2/js/iview-client-logo.js", "http://archive.thulb.uni-jena.de/hisbest/modules/iview2/js/iview-client-metadata.js" ],
        "css" : [ "http://archive.thulb.uni-jena.de/hisbest/modules/iview2/css/default.css", "http://archive.thulb.uni-jena.de/hisbest/css/urmelLogo.css" ]
    },
    "properties" : {
        "derivateURL" : "http://archive.thulb.uni-jena.de/hisbest/servlets/MCRFileNodeServlet/HisBest_derivate_00016280/",
        "metsURL" : "http://archive.thulb.uni-jena.de/hisbest/servlets/MCRMETSServlet/HisBest_derivate_00016280",
        "i18nURL" : "http://archive.thulb.uni-jena.de/hisbest/servlets/MCRLocaleServlet/{lang}/component.iview2.*",
        "derivate" : "HisBest_derivate_00016280",
        "filePath" : "/2_8_30.tif",
        "mobile" : false,
        "tileProviderPath" : "http://archive.thulb.uni-jena.de/hisbest/servlets/MCRTileServlet/",
        "imageXmlPath" : "http://archive.thulb.uni-jena.de/hisbest/servlets/MCRTileServlet/",
        "pdfCreatorURI" : "http://wrackdm17.thulb.uni-jena.de/mets-printer/pdf",
        "text.enabled" : "false",
        "logoURL" : "http://archive.thulb.uni-jena.de/hisbest/images/Urmel_Logo_leicht_grau.svg",
        "doctype" : "mets",
        "webApplicationBaseURL" : "http://archive.thulb.uni-jena.de/hisbest/",
        "metadataURL" : "http://archive.thulb.uni-jena.de/hisbest/receive/HisBest_cbu_00029645?XSL.Transformer\u003dmycoreobject-viewer",
        "pdfCreatorStyle" : "pdf",
        "objId" : "HisBest_cbu_00029645",
        "lang" : "de"
    }
};

// http://www.../serlvet/{derivate}/path/image.tif/z/x/y.jpg

// http://www.../servlet/tiles?derivate=[derivate]&path=..
