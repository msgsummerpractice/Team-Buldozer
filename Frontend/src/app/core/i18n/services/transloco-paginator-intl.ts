import { DestroyRef, Injectable, inject, Service } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslocoService } from '@jsverse/transloco';

@Service()
export class TranslocoPaginatorIntl extends MatPaginatorIntl {
  private readonly translocoService = inject(TranslocoService);
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    super();

    this.translocoService.langChanges$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.translateLabels());
  }

  override getRangeLabel = (page: number, pageSize: number, length: number): string => {
    const separator = this.translocoService.translate('paginator.range');

    if (length === 0 || pageSize === 0) {
      return `0 ${separator} ${length}`;
    }

    const safeLength = Math.max(length, 0);
    const startIndex = page * pageSize;
    const endIndex =
      startIndex < safeLength ? Math.min(startIndex + pageSize, safeLength) : startIndex + pageSize;

    return `${startIndex + 1} – ${endIndex} ${separator} ${safeLength}`;
  };

  private translateLabels(): void {
    this.itemsPerPageLabel = this.translocoService.translate('paginator.itemsPerPage');
    this.nextPageLabel = this.translocoService.translate('paginator.nextPage');
    this.previousPageLabel = this.translocoService.translate('paginator.previousPage');
    this.firstPageLabel = this.translocoService.translate('paginator.firstPage');
    this.lastPageLabel = this.translocoService.translate('paginator.lastPage');

    this.changes.next();
  }
}
