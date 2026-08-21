import { Service, computed, signal } from '@angular/core';

@Service()
export class LoadingService {
  private readonly activeRequests = signal(0);
  readonly isLoading = computed(() => this.activeRequests() > 0);

  increment(): void {
    this.activeRequests.update((n) => n + 1);
  }

  decrement(): void {
    this.activeRequests.update((n) => Math.max(0, n - 1));
  }
}
