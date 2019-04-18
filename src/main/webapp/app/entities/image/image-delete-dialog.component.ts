import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IImage } from 'app/shared/model/image.model';
import { ImageService } from './image.service';

@Component({
    selector: 'jhi-image-delete-dialog',
    templateUrl: './image-delete-dialog.component.html'
})
export class ImageDeleteDialogComponent {
    image: IImage;

    constructor(protected imageService: ImageService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: string) {
        this.imageService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'imageListModification',
                content: 'Deleted an image'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-image-delete-popup',
    template: ''
})
export class ImageDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ image }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(ImageDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.image = image;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/image', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/image', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
