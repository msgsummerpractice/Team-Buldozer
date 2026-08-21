import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Header } from '@features/header/components/header';
import { LoadingService } from '@core/loading/services/loading.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, MatProgressBarModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private readonly loadingService = inject(LoadingService);

  protected get isLoading() {
    return this.loadingService.isLoading();
  }
}
