import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Navbar} from './navbar/navbar';
import {PatientsPage} from './patients-page/patients-page';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Navbar, PatientsPage],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('ui-angular');
}
