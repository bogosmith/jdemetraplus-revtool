/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ra.providers.spreadsheets.output;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ec.satoolkit.ISaSpecification;
import ec.tss.sa.documents.SaDocument;
import ec.tss.sa.output.BasicConfiguration;
import ec.tss.sa.output.DefaultSummary;
import ec.tstoolkit.algorithm.IOutput;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsDataTable;
import ec.tstoolkit.utilities.NamedObject;
import ec.tstoolkit.utilities.Paths;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author bennouha
 */
public class VintagesSpreadsheetOutput implements IOutput<SaDocument<ISaSpecification>> {

    VintagesSpreadsheetOutputConfiguration config_;
    List<DefaultSummary> summaries_;
    File folder_;

    public VintagesSpreadsheetOutput(VintagesSpreadsheetOutputConfiguration config) {
        summaries_ = Lists.newArrayList();
        config_ = (VintagesSpreadsheetOutputConfiguration) config.clone();
    }

    @Override
    public void process(SaDocument<ISaSpecification> document) {
        DefaultSummary summary = new DefaultSummary(document.getTs().getName(), document.getResults(), config_.getSeries());
        if (config_.isSaveModel()) {
            summary.setModel(document.getSpecification());
        }
        summaries_.add(summary);
    }

    @Override
    public void start(Object context) {
        summaries_.clear();
        folder_ = BasicConfiguration.folderFromContext(config_.getFolder(), context);
    }

    @Override
    public void end(Object context) throws Exception {
        String file = new File(folder_, config_.getFileName()).getAbsolutePath();
        file = Paths.changeExtension(file, "xlsx");
        File ssfile = new File(file);
        XSSFWorkbook workbook = new XSSFWorkbook();

        try {
            FileOutputStream stream = new FileOutputStream(ssfile);
            try {
                switch (config_.getLayout()) {
                    case ByComponent: {
                        HashMap<String, List<NamedObject<TsData>>> allData = new HashMap<String, List<NamedObject<TsData>>>();
                        for (DefaultSummary summary : summaries_) {
                            for (Entry<String, TsData> keyValue : summary.getAllSeries().entrySet()) {
                                List<NamedObject<TsData>> list = null;
                                if (!allData.containsKey(keyValue.getKey())) {
                                    list = Lists.newArrayList();
                                    allData.put(keyValue.getKey(), list);
                                } else {
                                    list = allData.get(keyValue.getKey());
                                }
                                list.add(new NamedObject<TsData>(summary.getName(), keyValue.getValue()));
                            }
                        }
                        for (Entry<String, List<NamedObject<TsData>>> keyValue : allData.entrySet()) {
                            TsDataTable byComponentTable = new TsDataTable();
                            List<NamedObject<TsData>> value = keyValue.getValue();
                            String[] headers = new String[value.size()];
                            for (int i = 0; i < headers.length; i++) {
                                NamedObject<TsData> data = value.get(i);
                                headers[i] = data.name;
                                byComponentTable.insert(-1, data.object);
                            }
                            //ADD SHEET
                            VintagesXSSFHelper.addSheet(workbook, keyValue.getKey(), new String[]{keyValue.getKey()}, headers, byComponentTable, config_.isVerticalOrientation());
                        }
                        break;
                    }
                    case BySeries: {
                        for (int i = 0; i < summaries_.size(); i++) {
                            DefaultSummary summary = summaries_.get(i);
                            Set<Entry<String, TsData>> tmp = summary.getAllSeries().entrySet();
                            TsDataTable bySeriesTable = new TsDataTable();
                            String[] componentHeaders = new String[tmp.size()];
                            int j = 0;
                            for (Entry<String, TsData> keyValue : tmp) {
                                componentHeaders[j++] = keyValue.getKey();
                                bySeriesTable.insert(-1, keyValue.getValue());
                            }
                            //ADD SHEET
                            VintagesXSSFHelper.addSheet(workbook, "Series" + Integer.toString(i), new String[]{summary.getName()}, componentHeaders, bySeriesTable, config_.isVerticalOrientation());
                        }
                        break;
                    }
                    case OneSheet: {
                        List<String> headers0 = Lists.newArrayList();
                        List<String> headers1 = Lists.newArrayList();
                        TsDataTable oneSheetTable = new TsDataTable();

                        for (DefaultSummary summary : summaries_) {
                            headers0.add(summary.getName());
                            Map<String, TsData> data = summary.getAllSeries();
                            for (Entry<String, TsData> keyValue : data.entrySet()) {
                                headers1.add(keyValue.getKey());
                                oneSheetTable.insert(-1, keyValue.getValue());
                            }
                            for (int i = 1; i < data.size(); i++) {
                                headers0.add("");
                            }
                        }
                        //ADD SHEET
                        VintagesXSSFHelper.addSheet(workbook, "Series", Iterables.toArray(headers0, String.class), Iterables.toArray(headers1, String.class), oneSheetTable, config_.isVerticalOrientation());
                        break;
                    }
                }
                workbook.write(stream);
            } finally {
                stream.close();
            }
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public String getName() {
        return "Spreadsheet";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
