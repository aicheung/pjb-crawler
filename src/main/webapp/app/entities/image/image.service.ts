import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IImage } from 'app/shared/model/image.model';

type EntityResponseType = HttpResponse<IImage>;
type EntityArrayResponseType = HttpResponse<IImage[]>;

@Injectable({ providedIn: 'root' })
export class ImageService {
    public resourceUrl = SERVER_API_URL + 'api/images';

    constructor(protected http: HttpClient) {}

    create(image: IImage): Observable<EntityResponseType> {
        return this.http.post<IImage>(this.resourceUrl, image, { observe: 'response' });
    }

    update(image: IImage): Observable<EntityResponseType> {
        return this.http.put<IImage>(this.resourceUrl, image, { observe: 'response' });
    }

    find(id: string): Observable<EntityResponseType> {
        return this.http.get<IImage>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IImage[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: string): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
