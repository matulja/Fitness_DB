package deva_db.copy_db.Mssql;


import _GuttenBase_Examples.mapping.CustomColumnRenameName;
import _GuttenBase_Examples.mapping.CustomTableRenameName;
import de.akquinet.jbosscc.guttenbase.hints.ColumnMapperHint;
import de.akquinet.jbosscc.guttenbase.hints.ColumnTypeMapperHint;
import de.akquinet.jbosscc.guttenbase.hints.NumberOfRowsPerBatchHint;
import de.akquinet.jbosscc.guttenbase.hints.TableMapperHint;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnMapper;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnTypeMapper;
import de.akquinet.jbosscc.guttenbase.mapping.DefaultColumnTypeMapper;
import de.akquinet.jbosscc.guttenbase.mapping.TableMapper;
import de.akquinet.jbosscc.guttenbase.meta.TableMetaData;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;
import de.akquinet.jbosscc.guttenbase.repository.impl.ConnectorRepositoryImpl;
import de.akquinet.jbosscc.guttenbase.tools.DefaultTableCopyTool;
import de.akquinet.jbosscc.guttenbase.tools.DropTablesTool;
import de.akquinet.jbosscc.guttenbase.tools.NumberOfRowsPerBatch;
import de.akquinet.jbosscc.guttenbase.tools.schema.CopySchemaTool;
import deva_db.connInfo.MSSQLConnectionsInfoDeva;
import deva_db.connInfo.MysqlConnetionsInfoDeva;
import deva_db.mapping.CustomTableNameFilterDeva;
import ubi_db.connInfo.MSSQLConnectionsInfoUbi;

import java.util.List;


/**
 * Created by mfehler on 06.06.17.
 */
public class CopySchemaFromMssqlToMysqlDeva {


    public static final String SOURCE = "source";
    public static final String TARGET = "target";


    public static void main(final String[] args) throws Exception {

        final ConnectorRepository connectorRepository = new ConnectorRepositoryImpl();

        connectorRepository.addConnectionInfo(SOURCE, new MSSQLConnectionsInfoDeva());
        connectorRepository.addConnectionInfo(TARGET, new MysqlConnetionsInfoDeva());


        DropTablesTool dropTablesTool = new DropTablesTool(connectorRepository);
        dropTablesTool.dropIndexes(TARGET);
        dropTablesTool.dropForeignKeys(TARGET);
        dropTablesTool.dropTables(TARGET);

        //add _Mapping TableFilter
        connectorRepository.addConnectorHint(SOURCE, new CustomTableNameFilterDeva());
        connectorRepository.addConnectorHint(TARGET, new CustomTableNameFilterDeva());

        //add MappingTable  --> rename tables
        connectorRepository.addConnectorHint(TARGET, new TableMapperHint() {
            @Override
            public TableMapper getValue() {
                return new CustomTableRenameName()
                        .addReplacement("deva_artikel","abc_deva_artikel")
                        .addReplacement("deva_benutzer", "abc_deva_benutzer")
                        .addReplacement("deva_firma", "abc_deva_firma");
            }
        });

        //add MappingColumn  --> rename columns
        connectorRepository.addConnectorHint(TARGET, new ColumnMapperHint() {
            @Override
            public ColumnMapper getValue() {
                return new CustomColumnRenameName()
                        .addReplacement("name", "id_name")
                        .addReplacement("username", "id_username")
                        .addReplacement("ampel_status", "id_ampel_status")
                        .addReplacement("artikelprozess_status", "id_artikelprozess_status");
            }
        });


        connectorRepository.addConnectorHint(SOURCE, new ColumnTypeMapperHint() {
                    @Override
                    public ColumnTypeMapper getValue() {
                         return new DefaultColumnTypeMapper();
                    }
                });


        List<String> script = new CopySchemaTool(connectorRepository).createDDLScript(SOURCE, TARGET);
        for (String s : script) {System.out.println(s);}

        new CopySchemaTool(connectorRepository).copySchema(SOURCE, TARGET);
        System.out.println("------------------------------------ SCHEMA DONE ------------------------------------ ");

        new DefaultTableCopyTool(connectorRepository).copyTables(SOURCE, TARGET);
        System.out.println("------------------------------------ COPY DATA DONE ------------------------------------ ");
    }
}




