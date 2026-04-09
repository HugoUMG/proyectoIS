import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Activo } from '../../shared/models/activo.model';

@Injectable({ providedIn: 'root' })
export class ActivoService {
  private apiUrl = `${environment.apiUrl}/inventario/activos`;
  constructor(private http: HttpClient) {}

  obtenerActivosPorEstado(estados: string[]): Observable<Activo[]> {
    return this.http.get<Activo[]>(this.apiUrl);
  }
}
