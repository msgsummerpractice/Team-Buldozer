import { Service, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { map, of, switchMap, timer } from 'rxjs';

// Only show the indicator once a request has been pending longer than this, to avoid flicker on fast requests.
const SHOW_DELAY_MS = 300;

@Service()
export class LoadingService {
  private readonly activeRequests = signal(0);

  private readonly isLoading$ = toObservable(this.activeRequests).pipe(
    switchMap((count) => (count > 0 ? timer(SHOW_DELAY_MS).pipe(map(() => true)) : of(false)))
  );

  readonly isLoading = toSignal(this.isLoading$, { initialValue: false });

  increment(): void {
    this.activeRequests.update((n) => n + 1);
  }

  decrement(): void {
    this.activeRequests.update((n) => Math.max(0, n - 1));
  }
}
