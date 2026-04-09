import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AprobacionBaja, Baja } from '../../shared/models/baja.model';

@Injectable({ providedIn: 'root' })
export class BajaService {
  private apiUrl = `${environment.apiUrl}/bajas`;
  constructor(private http: HttpClient) {}

  solicitarBaja(payload: Partial<Baja>): Observable<Baja> {
    return this.http.post<Baja>(`${this.apiUrl}/solicitar`, payload);
  }

  misAprobacionesPendientes(): Observable<AprobacionBaja[]> {
    return this.http.get<AprobacionBaja[]>(`${this.apiUrl}/mis-aprobaciones-pendientes`);
  }

  aprobarBaja(bajaId: number, nivel: number, comentarios?: string): Observable<Baja> {
    let params = new HttpParams();
    if (comentarios) params = params.set('comentarios', comentarios);
    return this.http.post<Baja>(`${this.apiUrl}/${bajaId}/aprobar/${nivel}`, null, { params });
  }

  rechazarBaja(bajaId: number, nivel: number, motivo: string): Observable<Baja> {
    const params = new HttpParams().set('motivo', motivo);
    return this.http.post<Baja>(`${this.apiUrl}/${bajaId}/rechazar/${nivel}`, null, { params });
  }
}
