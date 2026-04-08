import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BajaService } from '../../core/services/baja.service';
import { ActivoService } from '../../core/services/activo.service';
import { ToastrService } from 'ngx-toastr';
import { Activo } from '../../shared/models/activo.model';
import { TipoBaja, MotivoBaja } from '../../shared/models/baja.model';

@Component({
  selector: 'app-baja-solicitud',
  templateUrl: './baja-solicitud.component.html'
})
export class BajaSolicitudComponent implements OnInit {
  bajaForm: FormGroup;
  activos: Activo[] = [];
  tiposBaja = Object.values(TipoBaja);
  motivosBaja = Object.values(MotivoBaja);
  loading = false;

  constructor(
    private fb: FormBuilder,
    private bajaService: BajaService,
    private activoService: ActivoService,
    private toastr: ToastrService
  ) {
    this.bajaForm = this.fb.group({
      activoId: ['', Validators.required],
      tipoBaja: ['', Validators.required],
      motivoBaja: ['', Validators.required],
      justificacion: ['', [Validators.required, Validators.minLength(20)]]
    });
  }

  ngOnInit(): void {
    this.cargarActivosDisponibles();
  }

  cargarActivosDisponibles(): void {
    this.activoService.obtenerActivosPorEstado(['DISPONIBLE', 'ASIGNADO']).subscribe({
      next: (activos) => this.activos = activos,
      error: (error) => this.toastr.error('Error cargando activos', 'Error')
    });
  }

  onSubmit(): void {
    if (this.bajaForm.invalid) {
      this.toastr.warning('Complete todos los campos requeridos', 'Formulario inválido');
      return;
    }

    this.loading = true;
    this.bajaService.solicitarBaja(this.bajaForm.value).subscribe({
      next: (baja) => {
        this.toastr.success('Solicitud de baja enviada para aprobación', 'Éxito');
        this.bajaForm.reset();
        this.loading = false;
      },
      error: (error) => {
        this.toastr.error(error.error.message || 'Error al solicitar baja', 'Error');
        this.loading = false;
      }
    });
  }
}