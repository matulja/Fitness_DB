package _GuttenBase_Examples._copyDatabeses.Mysql;

import _GuttenBase_Examples.connInfo.OracleConnetionsInfo;
import _GuttenBase_Examples.connInfo.SqlConnectionsInfo;
import de.akquinet.jbosscc.guttenbase.hints.ColumnTypeMapperHint;

import de.akquinet.jbosscc.guttenbase.hints.NumberOfRowsPerBatchHint;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnTypeMapper;

import de.akquinet.jbosscc.guttenbase.mapping.DefaultColumnTypeMapper;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;
import de.akquinet.jbosscc.guttenbase.repository.impl.ConnectorRepositoryImpl;
import de.akquinet.jbosscc.guttenbase.tools.DefaultTableCopyTool;
import de.akquinet.jbosscc.guttenbase.tools.DropTablesTool;
import de.akquinet.jbosscc.guttenbase.tools.NumberOfRowsPerBatch;
import de.akquinet.jbosscc.guttenbase.tools.schema.CopySchemaTool;


import java.util.List;


/**
 * Created by mfehler on 21.03.17.
 */
public class CopySchemaFromMysqlToOracle {


    public static final String SOURCE = "source";
    public static final String TARGET = "target";

    public static void main(final String[] args) throws Exception {

        final ConnectorRepository connectorRepository = new ConnectorRepositoryImpl();
        connectorRepository.addConnectionInfo(SOURCE, new SqlConnectionsInfo());
        connectorRepository.addConnectionInfo(TARGET, new OracleConnetionsInfo());


        DropTablesTool dropTablesTool = new DropTablesTool(connectorRepository);
        dropTablesTool.dropIndexes(TARGET);
        dropTablesTool.dropForeignKeys(TARGET);
        dropTablesTool.dropTables(TARGET);

      connectorRepository.addConnectorHint(TARGET, new NumberOfRowsPerBatchHint() {
        @Override
        public NumberOfRowsPerBatch getValue() {
          return new NumberOfRowsPerBatch() {
            @Override
            public int getNumberOfRowsPerBatch(TableMetaData targetTableMetaData) {
              return 1000;
            }

            @Override
            public boolean useMultipleValuesClauses(TableMetaData targetTableMetaData) {
              return false;
            }
          };
        }
      });



        //add _Mapping TableFilter
     /*  connectorRepository.addConnectorHint(SOURCE,new CustomTableNameFilterShop());
       connectorRepository.addConnectorHint(TARGET,new CustomTableNameFilterShop());

        //add _Mapping ColumnFilter
       connectorRepository.addConnectorHint(SOURCE,new CustomColumnNameFilterShop());
       connectorRepository.addConnectorHint(TARGET,new CustomColumnNameFilterShop());

        //add MappingColumn  --> rename columns
        connectorRepository.addConnectorHint(TARGET, new ColumnMapperHint() {
            @Override
            public ColumnMapper getValue() {
                return new CustomColumnRenameName()
                        .addReplacement("phone", "id_phone")
                        .addReplacement("city", "id_city");
                        // .addReplacement("salesrepemployeenumber", "sem");
            }
        });


        //add MappingTable  --> rename tables
        connectorRepository.addConnectorHint(TARGET, new TableMapperHint() {
            @Override
            public TableMapper getValue() {
                return new CustomTableRenameName()
                        .addReplacement("offices", "tab_offices")
                        .addReplacement("orders", "tab_orders");
            }
        });*/


        //add  ColumnType  --> replace columnType
        connectorRepository.addConnectorHint(SOURCE, new ColumnTypeMapperHint() {
                    @Override
                    public ColumnTypeMapper getValue() {
                         return new DefaultColumnTypeMapper();
                    }
                });


        List<String> script = new CopySchemaTool(connectorRepository).createDDLScript(SOURCE, TARGET);
        for (String s : script) {System.out.println(s);}


        new CopySchemaTool(connectorRepository).copySchema(SOURCE, TARGET);
        System.out.println("SCHEMA DONE !!!");

        new DefaultTableCopyTool(connectorRepository).copyTables(SOURCE, TARGET);
        System.out.println("CopyData Done !!!");
    }
}




