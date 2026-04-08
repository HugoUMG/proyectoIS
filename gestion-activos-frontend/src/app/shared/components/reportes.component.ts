import { Component } from '@angular/core';
import { ReporteService } from '../../core/services/reporte.service';
import { ToastrService } from 'ngx-toastr';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-reportes',
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.scss']
})
export class ReportesComponent {
  fechaInicio: string;
  fechaFin: string;
  empleadoId: number;
  formatoProximosBaja: 'PDF' | 'EXCEL' = 'PDF';
  
  loadingHojaVida = false;
  loadingCentroCosto = false;
  loadingDepreciacion = false;
  loadingProximosBaja = false;

  constructor(
    private reporteService: ReporteService,
    private toastr: ToastrService
  ) {
    const hoy = new Date();
    this.fechaFin = hoy.toISOString().split('T')[0];
    
    const haceUnMes = new Date();
    haceUnMes.setMonth(haceUnMes.getMonth() - 1);
    this.fechaInicio = haceUnMes.toISOString().split('T')[0];
  }

  generarHojaVidaEmpleado(): void {
    if (!this.empleadoId) {
      this.toastr.warning('Ingrese el ID del empleado', 'Advertencia');
      return;
    }
    
    this.loadingHojaVida = true;
    this.reporteService.generarHojaVidaEmpleado(this.empleadoId).subscribe({
      next: (blob) => {
        saveAs(blob, `hoja-vida-empleado-${this.empleadoId}.pdf`);
        this.toastr.success('Hoja de vida generada exitosamente', 'Éxito');
        this.loadingHojaVida = false;
      },
      error: (error) => {
        console.error('Error generando hoja de vida:', error);
        this.toastr.error('Error al generar la hoja de vida', 'Error');
        this.loadingHojaVida = false;
      }
    });
  }

  generarReporteCentroCosto(): void {
    this.loadingCentroCosto = true;
    this.reporteService.generarReporteCentroCosto(this.fechaInicio, this.fechaFin).subscribe({
      next: (blob) => {
        saveAs(blob, `reporte-centro-costo-${this.fechaInicio}_${this.fechaFin}.xlsx`);
        this.toastr.success('Reporte por centro de costo generado exitosamente', 'Éxito');
        this.loadingCentroCosto = false;
      },
      error: (error) => {
        console.error('Error generando reporte:', error);
        this.toastr.error('Error al generar el reporte', 'Error');
        this.loadingCentroCosto = false;
      }
    });
  }

  generarReporteDepreciacion(): void {
    this.loadingDepreciacion = true;
    this.reporteService.generarReporteDepreciacion().subscribe({
      next: (blob) => {
        saveAs(blob, 'reporte-depreciacion.xlsx');
        this.toastr.success('Reporte de depreciación generado exitosamente', 'Éxito');
        this.loadingDepreciacion = false;
      },
      error: (error) => {
        console.error('Error generando reporte de depreciación:', error);
        this.toastr.error('Error al generar el reporte', 'Error');
        this.loadingDepreciacion = false;
      }
    });
  }

  generarReporteProximosBaja(): void {
    this.loadingProximosBaja = true;
    this.reporteService.generarReporteProximosBaja(this.formatoProximosBaja).subscribe({
      next: (blob) => {
        const extension = this.formatoProximosBaja === 'PDF' ? 'pdf' : 'xlsx';
        saveAs(blob, `proximos-baja.${extension}`);
        this.toastr.success('Reporte de próximos a baja generado exitosamente', 'Éxito');
        this.loadingProximosBaja = false;
      },
      error: (error) => {
        console.error('Error generando reporte:', error);
        this.toastr.error('Error al generar el reporte', 'Error');
        this.loadingProximosBaja = false;
      }
    });
  }
}