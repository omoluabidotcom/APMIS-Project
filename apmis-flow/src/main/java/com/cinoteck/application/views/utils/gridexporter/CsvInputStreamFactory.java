/*-
 * #%L
 * Grid Exporter Add-on
 * %%
 * Copyright (C) 2022 - 2023 Flowing Code
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/**
 * 
 */
package com.cinoteck.application.views.utils.gridexporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opencsv.CSVWriter;
import com.vaadin.flow.data.binder.BeanPropertySet;
import com.vaadin.flow.data.binder.PropertySet;

/**
 * @author mlope
 *
 */
@SuppressWarnings("serial")
class CsvInputStreamFactory<T> extends BaseInputStreamFactory<T> {

  private final static Logger LOGGER = LoggerFactory.getLogger(CsvInputStreamFactory.class);
  
  public CsvInputStreamFactory(GridExporter<T> exporter) {
    super(exporter, null, null);
  }

  @Override
  public InputStream createInputStream() {
    PipedInputStream in = new PipedInputStream();
    LOGGER.error("Pr+++++++++++++++++++rt headerrrrrrrrrrrr");
    try {
      exporter.columns = exporter.grid.getColumns().stream().filter(this::isExportable)
          .collect(Collectors.toList());

      String[] headers =
          getGridHeaders(exporter.grid).stream().map(Pair::getLeft).toArray(String[]::new);
      
      String[] footers = getGridFooters(exporter.grid).stream()
              .filter(pair -> StringUtils.isNotBlank(pair.getKey()))
              .map(Pair::getLeft).toArray(String[]::new);
      
      List<String[]> data = obtainDataStream(exporter.grid.getDataProvider())
          .map(this::buildRow).collect(Collectors.toList());
     

      PipedOutputStream out = new PipedOutputStream(in);
      new Thread(() -> {
        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(out))) {
        	LOGGER.error("Problem generating export headerrrrrrrrrrrr");
          writer.writeNext(headers);
          
          if (footers.length > 0) {
        	  LOGGER.error("Problem generating export footerrrrrrrrrrr");
              writer.writeNext(footers);
            }
          LOGGER.error("Problem generating export data");
          writer.writeAll(data);
         
        } catch (IOException e) {
          LOGGER.error("Problem generating export", e);
        } finally {
          IOUtils.closeQuietly(out);
        }
      }).start();
    } catch (IOException e) {
      LOGGER.error("Problem generating export", e);
    }
    return in;
  }

  @SuppressWarnings("unchecked")
  private String[] buildRow(T item) {
    if (exporter.propertySet == null) {
      exporter.propertySet = (PropertySet<T>) BeanPropertySet.get(item.getClass());
    }
    if (exporter.columns.isEmpty())
      throw new IllegalStateException("Grid has no columns");

    String[] result = new String[exporter.columns.size()];
    int[] currentColumn = new int[1];
    exporter.columns.forEach(column -> {
      Object value = exporter.extractValueFromColumn(item, column);

      result[currentColumn[0]] = "" + value;
      currentColumn[0] = currentColumn[0] + 1;
    });
    return result;
  }

}