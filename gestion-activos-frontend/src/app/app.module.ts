import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ActivoListComponent } from './shared/components/activo-list.component';
import { ReportesComponent } from './shared/components/reportes.component';
import { BajaSolicitudComponent } from './shared/components/baja-solicitud.component';
import { AprobacionBajaComponent } from './shared/components/aprobacion-baja.component';

@NgModule({
  declarations: [
    AppComponent,
    ActivoListComponent,
    ReportesComponent,
    BajaSolicitudComponent,
    AprobacionBajaComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    AppRoutingModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
