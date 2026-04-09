export interface Baja {
  id: number;
  activoId: number;
  tipoBaja: TipoBaja;
  motivoBaja: MotivoBaja;
  justificacion: string;
  estado: string;
}

export interface AprobacionBaja {
  id: number;
  baja: { id: number };
  nivelJerarquico: number;
  decision: string;
}

export enum TipoBaja {
  OBSOLESCENCIA = 'OBSOLESCENCIA',
  DAÑO_IRREPARABLE = 'DAÑO_IRREPARABLE',
  VENTA = 'VENTA'
}

export enum MotivoBaja {
  FIN_VIDA_UTIL = 'FIN_VIDA_UTIL',
  FALLA_TECNICA = 'FALLA_TECNICA',
  ACTUALIZACION_TECNOLOGICA = 'ACTUALIZACION_TECNOLOGICA'
}
