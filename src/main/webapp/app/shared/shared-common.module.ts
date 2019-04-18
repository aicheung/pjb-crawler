import { NgModule } from '@angular/core';

import { PjbCrawlerSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
    imports: [PjbCrawlerSharedLibsModule],
    declarations: [JhiAlertComponent, JhiAlertErrorComponent],
    exports: [PjbCrawlerSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class PjbCrawlerSharedCommonModule {}
