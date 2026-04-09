import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ActivoListComponent } from './shared/components/activo-list.component';
import { ReportesComponent } from './shared/components/reportes.component';
import { BajaSolicitudComponent } from './shared/components/baja-solicitud.component';
import { AprobacionBajaComponent } from './shared/components/aprobacion-baja.component';

const routes: Routes = [
  { path: '', redirectTo: 'inventario', pathMatch: 'full' },
  { path: 'inventario', component: ActivoListComponent },
  { path: 'reportes', component: ReportesComponent },
  { path: 'bajas', component: BajaSolicitudComponent },
  { path: 'aprobaciones', component: AprobacionBajaComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
