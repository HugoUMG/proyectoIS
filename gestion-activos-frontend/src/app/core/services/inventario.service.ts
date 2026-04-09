import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Activo } from '../../shared/models/activo.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class InventarioService {
  private apiUrl = `${environment.apiUrl}/inventario`;

  constructor(private http: HttpClient) { }

  obtenerTodosActivos(): Observable<Activo[]> {
    return this.http.get<Activo[]>(`${this.apiUrl}/activos`);
  }

  obtenerActivoPorId(id: number): Observable<Activo> {
    return this.http.get<Activo>(`${this.apiUrl}/activos/${id}`);
  }

  crearActivo(activo: Activo): Observable<Activo> {
    return this.http.post<Activo>(`${this.apiUrl}/activos`, activo);
  }

  actualizarActivo(id: number, activo: Activo): Observable<Activo> {
    return this.http.put<Activo>(`${this.apiUrl}/activos/${id}`, activo);
  }

  generarQR(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/activos/${id}/qr`, {
      responseType: 'blob'
    });
  }

  buscarActivos(categoria?: string, estado?: string, ubicacion?: string): Observable<Activo[]> {
    let params = new HttpParams();
    if (categoria) params = params.set('categoria', categoria);
    if (estado) params = params.set('estado', estado);
    if (ubicacion) params = params.set('ubicacion', ubicacion);
    
    return this.http.get<Activo[]>(`${this.apiUrl}/activos/buscar`, { params });
  }
}