import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from '@features/header/components/header';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
