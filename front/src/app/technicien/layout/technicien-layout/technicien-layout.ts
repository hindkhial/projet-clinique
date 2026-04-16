import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../sidebar/sidebar';

@Component({
  selector: 'app-technicien-layout',
  standalone: true,
  imports: [RouterOutlet, Sidebar],
  templateUrl: './technicien-layout.html',
})
export class TechnicienLayout {}
