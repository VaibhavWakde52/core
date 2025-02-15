import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { TooltipModule } from 'primeng/tooltip';

import { DotPipesModule } from '@pipes/dot-pipes.module';

import { DotPagesCardComponent } from './dot-pages-card.component';

import { DotPagesFavoritePageEmptySkeletonComponent } from '../../dot-pages-favorite-page-empty-skeleton/dot-pages-favorite-page-empty-skeleton.component';

@NgModule({
    imports: [
        CommonModule,
        CardModule,
        DotPagesFavoritePageEmptySkeletonComponent,
        ButtonModule,
        TooltipModule,
        DotPipesModule
    ],
    declarations: [DotPagesCardComponent],
    exports: [DotPagesCardComponent]
})
export class DotPagesCardModule {}
