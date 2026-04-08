package com.empresa.gestionactivos.service;

import com.empresa.gestionactivos.model.*;
import com.empresa.gestionactivos.repository.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService {
    
    private final ActivoRepository activoRepository;
    private final AsignacionRepository asignacionRepository;
    private final AdquisicionRepository adquisicionRepository;
    private final UsuarioRepository usuarioRepository;
    
    // ============ REPORTE 1: BIENES INVERTIDOS EN LA EMPRESA ============
    public byte[] generarReporteBienesInvertidos(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            List<Adquisicion> adquisiciones = adquisicionRepository
                .findByFechaFacturaBetween(fechaInicio, fechaFin);
            
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Bienes Invertidos");
            
            // Estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            
            // Encabezados
            String[] headers = {"Fecha Factura", "N° Factura", "Proveedor", "Partida", 
                               "Subtotal", "IVA", "Total", "Estado", "Cant. Activos"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Datos
            int rowNum = 1;
            BigDecimal totalInversion = BigDecimal.ZERO;
            
            for (Adquisicion adq : adquisiciones) {
                Row row = sheet.createRow(rowNum++);
                
                row.createCell(0).setCellValue(adq.getFechaFactura().toString());
                row.getCell(0).setCellStyle(dateStyle);
                
                row.createCell(1).setCellValue(adq.getNumeroFactura());
                row.createCell(2).setCellValue(adq.getProveedor().getRazonSocial());
                row.createCell(3).setCellValue(adq.getPartidaPresupuestaria());
                
                Cell subtotalCell = row.createCell(4);
                subtotalCell.setCellValue(adq.getSubtotal().doubleValue());
                subtotalCell.setCellStyle(currencyStyle);
                
                Cell ivaCell = row.createCell(5);
                ivaCell.setCellValue(adq.getIva() != null ? adq.getIva().doubleValue() : 0);
                ivaCell.setCellStyle(currencyStyle);
                
                Cell totalCell = row.createCell(6);
                totalCell.setCellValue(adq.getTotal().doubleValue());
                totalCell.setCellStyle(currencyStyle);
                
                row.createCell(7).setCellValue(adq.getEstado().toString());
                row.createCell(8).setCellValue(adq.getActivos().size());
                
                if (adq.getEstado() == Adquisicion.EstadoAdquisicion.COMPLETADA) {
                    totalInversion = totalInversion.add(adq.getTotal());
                }
            }
            
            // Fila de totales
            Row totalRow = sheet.createRow(rowNum + 1);
            Cell totalLabel = totalRow.createCell(5);
            totalLabel.setCellValue("TOTAL INVERTIDO:");
            totalLabel.setCellStyle(headerStyle);
            
            Cell totalValue = totalRow.createCell(6);
            totalValue.setCellValue(totalInversion.doubleValue());
            totalValue.setCellStyle(currencyStyle);
            
            // Auto-ajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error generando reporte de bienes invertidos", e);
            throw new RuntimeException("Error generando reporte: " + e.getMessage());
        }
    }
    
    // ============ REPORTE 2: BIENES ASIGNADOS A EMPLEADOS ============
    public byte[] generarReporteBienesAsignadosEmpleados(String formato) {
        try {
            List<Asignacion> asignacionesActivas = asignacionRepository
                .findByEstado(Asignacion.EstadoAsignacion.ACTIVA);
            
            if ("PDF".equalsIgnoreCase(formato)) {
                return generarPDFBienesAsignados(asignacionesActivas);
            } else {
                return generarExcelBienesAsignados(asignacionesActivas);
            }
        } catch (Exception e) {
            log.error("Error generando reporte de bienes asignados", e);
            throw new RuntimeException("Error generando reporte: " + e.getMessage());
        }
    }
    
    private byte[] generarPDFBienesAsignados(List<Asignacion> asignaciones) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Título
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("REPORTE DE BIENES ASIGNADOS A EMPLEADOS", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));
        
        // Fecha del reporte
        Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC);
        Paragraph date = new Paragraph("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), dateFont);
        document.add(date);
        document.add(new Paragraph("\n"));
        
        // Tabla
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        
        // Encabezados
        String[] headers = {"Empleado", "Departamento", "Código Activo", "Descripción", 
                           "Fecha Asignación", "Valor", "Ubicación", "Días en Posesión"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD)));
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Datos
        BigDecimal valorTotalAsignado = BigDecimal.ZERO;
        
        for (Asignacion asig : asignaciones) {
            Activo activo = asig.getActivo();
            Usuario empleado = asig.getUsuarioResponsable();
            
            table.addCell(empleado.getNombreCompleto());
            table.addCell(empleado.getDepartamento() != null ? empleado.getDepartamento() : "N/A");
            table.addCell(activo.getCodigoIdentificacion());
            table.addCell(activo.getNombre() + " - " + activo.getMarca());
            table.addCell(asig.getFechaAsignacion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            
            PdfPCell valorCell = new PdfPCell(new Phrase(String.format("$%,.2f", activo.getValorCompra())));
            valorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(valorCell);
            
            table.addCell(activo.getUbicacionFisica() != null ? activo.getUbicacionFisica() : "N/A");
            
            long diasPosesion = java.time.temporal.ChronoUnit.DAYS.between(asig.getFechaAsignacion(), LocalDate.now());
            table.addCell(String.valueOf(diasPosesion));
            
            valorTotalAsignado = valorTotalAsignado.add(activo.getValorCompra());
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
        
        // Resumen
        Font summaryFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Paragraph summary = new Paragraph(
            String.format("Total de activos asignados: %d | Valor total asignado: $%,.2f", 
                         asignaciones.size(), valorTotalAsignado), 
            summaryFont);
        document.add(summary);
        
        document.close();
        return baos.toByteArray();
    }
    
    private byte[] generarExcelBienesAsignados(List<Asignacion> asignaciones) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Bienes Asignados");
        
        // Estilos
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle currencyStyle = createCurrencyStyle(workbook);
        
        // Encabezados
        String[] headers = {"Empleado", "Departamento", "Centro Costo", "Código Activo", 
                           "Descripción", "Categoría", "Fecha Asignación", "Valor", "Ubicación"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Datos
        int rowNum = 1;
        for (Asignacion asig : asignaciones) {
            Row row = sheet.createRow(rowNum++);
            Activo activo = asig.getActivo();
            Usuario empleado = asig.getUsuarioResponsable();
            
            row.createCell(0).setCellValue(empleado.getNombreCompleto());
            row.createCell(1).setCellValue(empleado.getDepartamento());
            row.createCell(2).setCellValue(empleado.getCentroCosto());
            row.createCell(3).setCellValue(activo.getCodigoIdentificacion());
            row.createCell(4).setCellValue(activo.getNombre());
            row.createCell(5).setCellValue(activo.getCategoria());
            row.createCell(6).setCellValue(asig.getFechaAsignacion().toString());
            
            Cell valorCell = row.createCell(7);
            valorCell.setCellValue(activo.getValorCompra().doubleValue());
            valorCell.setCellStyle(currencyStyle);
            
            row.createCell(8).setCellValue(activo.getUbicacionFisica());
        }
        
        // Auto-ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos.toByteArray();
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }
    
    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }
}