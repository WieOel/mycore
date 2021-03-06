/// <reference path="MyCoReImageViewerEvent.ts" />
/// <reference path="../ViewerComponent.ts" />
/// <reference path="../../widgets/events/ViewerEvent.ts" />
/// <reference path="../model/StructureImage.ts" />

module mycore.viewer.components.events {
    export class ImageChangedEvent extends MyCoReImageViewerEvent{
        constructor(component: ViewerComponent,private _image:model.StructureImage) {
            super(component, ImageChangedEvent.TYPE);
        }

        public get image() {
            return this._image;
        }

        public static TYPE:string = "ImageChangedEvent";

    }
}