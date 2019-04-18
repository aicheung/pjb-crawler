import { IImage } from 'app/shared/model/image.model';

export interface ITag {
    id?: string;
    name?: string;
    images?: IImage[];
}

export class Tag implements ITag {
    constructor(public id?: string, public name?: string, public images?: IImage[]) {}
}
