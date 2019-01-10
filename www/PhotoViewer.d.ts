declare var exec: any;
interface IPhotoViewerOptions {
    share?: boolean;
    closeButton?: boolean;
    copyToReference?: boolean;
    headers?: string;
    piccasoOptions?: {
        fit?: boolean;
        centerInside?: boolean;
        centerCrop?: boolean;
    };
}
declare class PhotoViewer {
    static show(url: string, title?: string, options?: IPhotoViewerOptions): void;
}
//# sourceMappingURL=PhotoViewer.d.ts.map