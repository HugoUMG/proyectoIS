export interface Activo {
  id: number;
  codigoIdentificacion: string;
  nombre: string;
  descripcion: string;
  categoria: string;
  marca: string;
  modelo: string;
  numeroSerie: string;
  valorCompra: number;
  valorActual: number;
  fechaAdquisicion: Date;
  vidaUtilMeses: number;
  estado: EstadoActivo;
  ubicacionFisica: string;
  adquisicion?: Adquisicion;
  codigoQR?: string;
}

export enum EstadoActivo {
  DISPONIBLE = 'DISPONIBLE',
  ASIGNADO = 'ASIGNADO',
  EN_REPARACION = 'EN_REPARACION',
  DADO_DE_BAJA = 'DADO_DE_BAJA'
}

export interface Asignacion {
  id: number;
  activo: Activo;
  usuarioResponsable: Usuario;
  asignadoPor: Usuario;
  fechaAsignacion: Date;
  fechaDevolucion?: Date;
  estado: EstadoAsignacion;
  observaciones: string;
  firmaDigital?: string;
}

export enum EstadoAsignacion {
  ACTIVA = 'ACTIVA',
  DEVUELTA = 'DEVUELTA',
  TRANSFERIDA = 'TRANSFERIDA'
}

export interface Usuario {
  id: number;
  username: string;
  nombreCompleto: string;
  email: string;
  departamento: string;
  centroCosto: string;
  rol: RolUsuario;
}

export enum RolUsuario {
  ADMINISTRADOR = 'ADMINISTRADOR',
  ENCARGADO_INVENTARIO = 'ENCARGADO_INVENTARIO',
  DEPARTAMENTO_COMPRAS = 'DEPARTAMENTO_COMPRAS',
  EMPLEADO = 'EMPLEADO',
  GERENCIA_FINANZAS = 'GERENCIA_FINANZAS'
}