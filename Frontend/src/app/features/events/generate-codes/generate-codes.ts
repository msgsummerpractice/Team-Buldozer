import { Component, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { TranslocoPipe } from '@jsverse/transloco';
import { catchError, EMPTY } from 'rxjs';
import { EventService } from '@features/events/services/event-service';
import { EventCodesResponse } from '@features/events/model/event-codes-response';
import { PageLayout } from '@shared/components/page-layout/page-layout';

@Component({
  selector: 'app-generate-codes',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatIconModule,
    TranslocoPipe,
    RouterLink,
    PageLayout,
  ],
  templateUrl: './generate-codes.html',
})
export class GenerateCodes {
  private readonly route = inject(ActivatedRoute);
  private readonly eventService = inject(EventService);

  readonly codes = signal<EventCodesResponse | null>(null);
  readonly error = signal(false);
  protected readonly qrCodeSrc = computed(() => {
    const qr = this.codes()?.qrCode;
    return qr ? `data:image/png;base64,${qr}` : '';
  });

  constructor() {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.eventService
      .generateCodes(id)
      .pipe(
        catchError(() => {
          this.error.set(true);
          return EMPTY;
        })
      )
      .subscribe((data) => this.codes.set(data));
  }
}
