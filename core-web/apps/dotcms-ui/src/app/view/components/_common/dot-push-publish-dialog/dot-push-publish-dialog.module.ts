import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
import { SelectButtonModule } from 'primeng/selectbutton';

import { DotPushPublishDialogComponent } from '@components/_common/dot-push-publish-dialog/dot-push-publish-dialog.component';
import { DotPushPublishFormModule } from '@components/_common/forms/dot-push-publish-form/dot-push-publish-form.module';
import { DotDialogModule } from '@components/dot-dialog/dot-dialog.module';
import { DotPushPublishFiltersService } from '@dotcms/data-access';
import { DotFieldValidationMessageComponent } from '@dotcms/ui';
import { DotPipesModule } from '@pipes/dot-pipes.module';

import { PushPublishEnvSelectorModule } from '../dot-push-publish-env-selector/dot-push-publish-env-selector.module';

@NgModule({
    declarations: [DotPushPublishDialogComponent],
    exports: [DotPushPublishDialogComponent],
    providers: [DotPushPublishFiltersService],
    imports: [
        CommonModule,
        FormsModule,
        CalendarModule,
        DotDialogModule,
        PushPublishEnvSelectorModule,
        ReactiveFormsModule,
        DropdownModule,
        DotFieldValidationMessageComponent,
        SelectButtonModule,
        DotPipesModule,
        DotPushPublishFormModule
    ]
})
export class DotPushPublishDialogModule {}
