import { Component, OnInit } from '@angular/core';
import { BajaService } from '../../core/services/baja.service';
import { ToastrService } from 'ngx-toastr';
import { AprobacionBaja, Baja } from '../../shared/models/baja.model';

@Component({
  selector: 'app-aprobacion-baja',
  templateUrl: './aprobacion-baja.component.html'
})
export class AprobacionBajaComponent implements OnInit {
  aprobacionesPendientes: AprobacionBaja[] = [];
  loading = false;

  constructor(
    private bajaService: BajaService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.cargarAprobacionesPendientes();
  }

  cargarAprobacionesPendientes(): void {
    this.loading = true;
    this.bajaService.misAprobacionesPendientes().subscribe({
      next: (aprobaciones) => {
        this.aprobacionesPendientes = aprobaciones;
        this.loading = false;
      },
      error: (error) => {
        this.toastr.error('Error cargando aprobaciones pendientes', 'Error');
        this.loading = false;
      }
    });
  }

  aprobar(aprobacion: AprobacionBaja, comentarios?: string): void {
    this.bajaService.aprobarBaja(
      aprobacion.baja.id, 
      aprobacion.nivelJerarquico, 
      comentarios
    ).subscribe({
      next: () => {
        this.toastr.success('Baja aprobada correctamente');
        this.cargarAprobacionesPendientes();
      },
      error: (error) => this.toastr.error('Error al aprobar', 'Error')
    });
  }

  rechazar(aprobacion: AprobacionBaja, motivo: string): void {
    if (!motivo) {
      this.toastr.warning('Debe especificar un motivo de rechazo');
      return;
    }
    
    this.bajaService.rechazarBaja(
      aprobacion.baja.id, 
      aprobacion.nivelJerarquico, 
      motivo
    ).subscribe({
      next: () => {
        this.toastr.warning('Baja rechazada');
        this.cargarAprobacionesPendientes();
      },
      error: (error) => this.toastr.error('Error al rechazar', 'Error')
    });
  }

  getEstadoBadge(estado: string): string {
    const badges: Record<string, string> = {
      'PENDIENTE': 'bg-warning',
      'APROBADA': 'bg-success',
      'RECHAZADA': 'bg-danger',
      'EJECUTADA': 'bg-info'
    };
    return badges[estado] || 'bg-secondary';
  }
}