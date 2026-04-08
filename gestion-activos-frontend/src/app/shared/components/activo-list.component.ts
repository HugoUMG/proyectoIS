import { Component, OnInit } from '@angular/core';
import { InventarioService } from '../../core/services/inventario.service';
import { Activo, EstadoActivo } from '../../shared/models/activo.model';
import { ToastrService } from 'ngx-toastr';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-activo-list',
  templateUrl: './activo-list.component.html',
  styleUrls: ['./activo-list.component.scss']
})
export class ActivoListComponent implements OnInit {
  activos: Activo[] = [];
  activosFiltrados: Activo[] = [];
  loading = false;
  
  // Filtros
  filtroCategoria = '';
  filtroEstado = '';
  filtroUbicacion = '';
  
  categorias = ['EQUIPO_COMPUTO', 'MOBILIARIO', 'VEHICULO', 'MAQUINARIA', 'SOFTWARE'];
  estados = Object.values(EstadoActivo);

  constructor(
    private inventarioService: InventarioService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.cargarActivos();
  }

  cargarActivos(): void {
    this.loading = true;
    this.inventarioService.obtenerTodosActivos().subscribe({
      next: (data) => {
        this.activos = data;
        this.activosFiltrados = data;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error cargando activos:', error);
        this.toastr.error('Error al cargar los activos', 'Error');
        this.loading = false;
      }
    });
  }

  aplicarFiltros(): void {
    this.activosFiltrados = this.activos.filter(activo => {
      const coincideCategoria = !this.filtroCategoria || activo.categoria === this.filtroCategoria;
      const coincideEstado = !this.filtroEstado || activo.estado === this.filtroEstado;
      const coincideUbicacion = !this.filtroUbicacion || 
        activo.ubicacionFisica?.toLowerCase().includes(this.filtroUbicacion.toLowerCase());
      
      return coincideCategoria && coincideEstado && coincideUbicacion;
    });
  }

  descargarQR(activoId: number): void {
    this.inventarioService.generarQR(activoId).subscribe({
      next: (blob) => {
        saveAs(blob, `qr-activo-${activoId}.png`);
        this.toastr.success('Código QR descargado exitosamente', 'Éxito');
      },
      error: (error) => {
        console.error('Error generando QR:', error);
        this.toastr.error('Error al generar código QR', 'Error');
      }
    });
  }

  getEstadoClass(estado: EstadoActivo): string {
    const classes: Record<EstadoActivo, string> = {
      [EstadoActivo.DISPONIBLE]: 'badge bg-success',
      [EstadoActivo.ASIGNADO]: 'badge bg-primary',
      [EstadoActivo.EN_REPARACION]: 'badge bg-warning',
      [EstadoActivo.DADO_DE_BAJA]: 'badge bg-danger'
    };
    return classes[estado] || 'badge bg-secondary';
  }

  formatearMoneda(valor: number): string {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN'
    }).format(valor);
  }
}