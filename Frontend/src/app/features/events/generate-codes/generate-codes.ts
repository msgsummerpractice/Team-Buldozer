import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslocoPipe } from '@jsverse/transloco';
import { EventService } from '@features/events/services/event-service';
import { EventCodesResponse } from '@features/events/model/event-codes-response';

@Component({
  selector: 'app-generate-codes',
  imports: [
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatIconModule,
    MatProgressSpinnerModule,
    TranslocoPipe,
    RouterLink,
  ],
  templateUrl: './generate-codes.html',
})
export class GenerateCodes {
  private readonly route = inject(ActivatedRoute);
  private readonly eventService = inject(EventService);

  readonly codes = signal<EventCodesResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal(false);
  protected readonly qrCodeSrc = computed(() => {
    const qr = this.codes()?.qrCode;
    return qr ? `data:image/png;base64,${qr}` : '';
  });

  constructor() {
    const id = +this.route.snapshot.paramMap.get('id')!;

    this.eventService
      .generateCodes(id)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (data) => {
          this.codes.set(data);
          this.loading.set(false);
        },
        error: () => {
          this.error.set(true);
          this.loading.set(false);
        },
      });
  }
}
