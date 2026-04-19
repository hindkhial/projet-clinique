import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from '../sidebar/sidebar';

@Component({
  selector: 'app-radiologue-layout',
  standalone: true,
  imports: [RouterOutlet, Sidebar],
  templateUrl: './radiologue-layout.html',
})
export class RadiologueLayout {}
