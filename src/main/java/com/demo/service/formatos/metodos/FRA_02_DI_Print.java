package com.demo.service.formatos.metodos;

import com.demo.model.operacion.metodos.fra02di.FRA_DI_001;
import com.demo.model.operacion.metodos.fra02di.datas.FRA_DI_001_DATA;
import com.demo.repository.operacion.metodos.FRA_DI_001_DATA_Repository;
import com.demo.service.operacion.metodos.FRA_DI_001_Service;
import com.demo.utils.EstructuraNombres;
import com.demo.utils.FormatoFechas;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Service
public class FRA_02_DI_Print {

    @Autowired
    private FRA_DI_001_Service fra_di_001_service;

    @Autowired
    private FRA_DI_001_DATA_Repository fra_di_001_data_repository;

    EstructuraNombres estructuraNombres = new EstructuraNombres();
    FormatoFechas formatoFechas = new FormatoFechas();

    public ResponseEntity<InputStreamResource> crearFormato(Long id, int band) throws InvalidFormatException, IOException {

        FRA_DI_001 fra_di_001;
        List<FRA_DI_001_DATA> lista;

        if (band == 1) {
            fra_di_001 = fra_di_001_service.findById(id);
            lista = fra_di_001_data_repository.buscarTodosPorEnsayo(fra_di_001.getIdFRADI());
        } else {
            fra_di_001 = fra_di_001_service.findByMuestra(id);
            lista = fra_di_001_data_repository.buscarTodosPorEnsayo(fra_di_001.getIdFRADI());
        }

        URL url = new URL("https://resources.adpmx.com/cecim/laboratorio/doc/register/methods/02-FRA-DI-001-" + fra_di_001.getTipoCamiseta() + ".docx");
        XWPFDocument doc = new XWPFDocument(url.openStream());

        XWPFTable table0 = doc.getTables().get(0);
        table0.getRow(0).getCell(1).setText(fra_di_001.getFolioTecnica());

        XWPFTable table1 = doc.getTables().get(1);
        table1.getRow(0).getCell(1).setText(fra_di_001.getFolioSolicitudServicioInterno());
        table1.getRow(0).getCell(3).setText(formatoFechas.formateadorFechas(fra_di_001.getFechaInicioAnalisis()));
        table1.getRow(1).getCell(1).setText(fra_di_001.getIdInternoMuestra());
        table1.getRow(1).getCell(3).setText(formatoFechas.formateadorFechas(fra_di_001.getFechaFinalAnalisis()));

        XWPFTable table2 = doc.getTables().get(2);
        table2.getRow(0).getCell(1).setText(fra_di_001.getTemperatura() + " ??C");
        table2.getRow(0).getCell(3).setText(fra_di_001.getHumedadRelativa() + " %");
        table2.getRow(0).getCell(5).setText(fra_di_001.getCodigoRegla());

        XWPFTable table3 = doc.getTables().get(3);
        for (int i = 0; i < lista.size(); i++) {
            table3.getRow(i + 1).getCell(1).setText(lista.get(i).getLargo());
            table3.getRow(i + 1).getCell(2).setText(lista.get(i).getAncho());
            if (fra_di_001.getCantidadModificaciones().equals("Si")) {
                table3.getRow(i + 1).getCell(3).setText(lista.get(i).getFuelleDerecho());
                table3.getRow(i + 1).getCell(4).setText(lista.get(i).getFuelleIzquierdo());
            }
        }
        table3.getRow(6).getCell(1).setText(fra_di_001.getPromedioLargo());
        table3.getRow(6).getCell(2).setText(fra_di_001.getPromedioAncho());
        if (fra_di_001.getTipoCamiseta().equals("Si")) {
            table3.getRow(6).getCell(3).setText(fra_di_001.getPromedioFuelleDerecho());
            table3.getRow(6).getCell(4).setText(fra_di_001.getPromedioFuelleIzquierdo());
        }

        XWPFTable table4 = doc.getTables().get(4);
        table4.getRow(0).getCell(1).setText(fra_di_001.getObservaciones());

        XWPFTable table5 = doc.getTables().get(5);
        table5.getRow(1).getCell(0).removeParagraph(0);
        XWPFParagraph paragraph = table5.getRow(1).getCell(0).addParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        InputStream inputStream = new URL(fra_di_001.getRubricaRealizo()).openStream();
        XWPFPicture xwpfPicture = run.addPicture(inputStream, XWPFDocument.PICTURE_TYPE_PNG, "Name", Units.pixelToEMU(110), Units.pixelToEMU(73));
        run.addBreak();
        run.setText(fra_di_001.getRealizo());
        table5.getRow(1).getCell(1).setText(fra_di_001.getSupervisor());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=FRA-DI-" + estructuraNombres.getNombre() + ".docx");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        doc.write(byteArrayOutputStream);
        doc.close();
        MediaType word = MediaType.valueOf("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(word)
                .body(new InputStreamResource(byteArrayInputStream));
    }
}
