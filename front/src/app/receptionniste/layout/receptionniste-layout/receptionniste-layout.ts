import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../sidebar/sidebar';

@Component({
  selector: 'app-receptionniste-layout',
  imports: [RouterOutlet, Sidebar],
  templateUrl: './receptionniste-layout.html',
  standalone: true
})
export class ReceptionnisteLayout {}
