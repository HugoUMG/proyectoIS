import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <div class="container py-3">
      <h1>Sistema de Gestión de Activos</h1>
      <nav class="mb-3 d-flex gap-2">
        <a routerLink="/inventario" class="btn btn-outline-primary btn-sm">Inventario</a>
        <a routerLink="/reportes" class="btn btn-outline-primary btn-sm">Reportes</a>
        <a routerLink="/bajas" class="btn btn-outline-primary btn-sm">Solicitar baja</a>
        <a routerLink="/aprobaciones" class="btn btn-outline-primary btn-sm">Aprobaciones</a>
      </nav>
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {}
