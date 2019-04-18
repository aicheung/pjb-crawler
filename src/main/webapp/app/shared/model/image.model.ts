import { ITag } from 'app/shared/model/tag.model';

export interface IImage {
    id?: string;
    imageId?: number;
    origFileName?: string;
    uploadedBy?: string;
    views?: number;
    favorites?: number;
    tags?: ITag[];
}

export class Image implements IImage {
    constructor(
        public id?: string,
        public imageId?: number,
        public origFileName?: string,
        public uploadedBy?: string,
        public views?: number,
        public favorites?: number,
        public tags?: ITag[]
    ) {}
}
