import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private apiUrl = `${environment.apiUrl}/reportes`;
  constructor(private http: HttpClient) {}

  generarHojaVidaEmpleado(empleadoId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/empleado/${empleadoId}/hoja-vida`, { responseType: 'blob' });
  }

  generarReporteCentroCosto(fechaInicio?: string, fechaFin?: string): Observable<Blob> {
    let params = new HttpParams();
    if (fechaInicio) params = params.set('fechaInicio', fechaInicio);
    if (fechaFin) params = params.set('fechaFin', fechaFin);
    return this.http.get(`${this.apiUrl}/centro-costo`, { params, responseType: 'blob' });
  }

  generarReporteDepreciacion(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/depreciacion`, { responseType: 'blob' });
  }

  generarReporteProximosBaja(formato: string): Observable<Blob> {
    const params = new HttpParams().set('formato', formato);
    return this.http.get(`${this.apiUrl}/proximos-baja`, { params, responseType: 'blob' });
  }
}
