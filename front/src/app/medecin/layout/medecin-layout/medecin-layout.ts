import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../sidebar/sidebar';

@Component({
  selector: 'app-medecin-layout',
  standalone: true,
  imports: [RouterOutlet, Sidebar],
  templateUrl: './medecin-layout.html',
})
export class MedecinLayout {}
