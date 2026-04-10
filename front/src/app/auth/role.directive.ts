import { Directive, Input, TemplateRef, ViewContainerRef, inject, OnInit } from '@angular/core';
import { AuthService } from './auth.service';

@Directive({
  selector: '[hasRole]',
  standalone: true
})
export class HasRoleDirective implements OnInit {

  private auth     = inject(AuthService);
  private template = inject(TemplateRef<unknown>);
  private container = inject(ViewContainerRef);

  @Input() hasRole: string | string[] = [];

  ngOnInit(): void {
    const roles = Array.isArray(this.hasRole) ? this.hasRole : [this.hasRole];
    const hasAccess = roles.some(r => this.auth.hasRole(r));

    if (hasAccess) {
      this.container.createEmbeddedView(this.template);
    } else {
      this.container.clear();
    }
  }
}



// <!-- single role -->
// <button *hasRole="'MEDECIN'">Generate Report</button>

// <!-- multiple roles — shows if user has ANY of them -->
// <nav *hasRole="['MEDECIN', 'RADIOLOGUE']">Medical Staff Menu</nav>

// <!-- practical example in navbar -->
// <ul class="navbar-nav">
//   <li *hasRole="'PATIENT'">
//     <a routerLink="/patient/dashboard">My Dashboard</a>
//   </li>
//   <li *hasRole="'MEDECIN'">
//     <a routerLink="/medecin/dashboard">Consultations</a>
//   </li>
//   <li *hasRole="'TECHNICIEN'">
//     <a routerLink="/technicien/radios/upload">Upload Radio</a>
//   </li>
// </ul>