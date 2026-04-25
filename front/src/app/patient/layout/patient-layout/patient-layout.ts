import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../sidebar/sidebar';

@Component({
  selector: 'app-patient-layout',
  standalone: true,
  imports: [RouterOutlet, Sidebar],
  templateUrl: './patient-layout.html',
})
export class PatientLayout {}
