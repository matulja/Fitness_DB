package _GuttenBase_Examples._copyDatabeses.Oracle;

import _GuttenBase_Examples._copyDatabeses._Mapping.CustomColumnNameFilterShop;
import _GuttenBase_Examples._copyDatabeses._Mapping.CustomTableNameFilterShop;
import _GuttenBase_Examples.connInfo.OracleConnetionsInfo;
import _GuttenBase_Examples.connInfo.SqlConnectionsInfo;
import _GuttenBase_Examples.mapping.CustomColumnRenameName;
import _GuttenBase_Examples.mapping.CustomTableRenameName;
import de.akquinet.jbosscc.guttenbase.hints.ColumnMapperHint;
import de.akquinet.jbosscc.guttenbase.hints.ColumnTypeMapperHint;

import de.akquinet.jbosscc.guttenbase.hints.TableMapperHint;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnMapper;
import de.akquinet.jbosscc.guttenbase.mapping.ColumnTypeMapper;

import de.akquinet.jbosscc.guttenbase.mapping.DefaultColumnTypeMapper;
import de.akquinet.jbosscc.guttenbase.mapping.TableMapper;
import de.akquinet.jbosscc.guttenbase.repository.ConnectorRepository;
import de.akquinet.jbosscc.guttenbase.repository.impl.ConnectorRepositoryImpl;
import de.akquinet.jbosscc.guttenbase.tools.DefaultTableCopyTool;
import de.akquinet.jbosscc.guttenbase.tools.DropTablesTool;
import de.akquinet.jbosscc.guttenbase.tools.schema.CopySchemaTool;

import de.akquinet.jbosscc.guttenbase.tools.schema.comparison.SchemaComparatorTool;
import de.akquinet.jbosscc.guttenbase.tools.schema.comparison.SchemaCompatibilityIssues;

import java.util.List;


/**
 * Created by mfehler on 23.05.17.
 */
public class CopySchemaFromOracleToMysql {


    public static final String SOURCE = "source";
    public static final String TARGET = "target";

    public static void main(final String[] args) throws Exception {

        final ConnectorRepository connectorRepository = new ConnectorRepositoryImpl();
        connectorRepository.addConnectionInfo(SOURCE, new OracleConnetionsInfo());
        connectorRepository.addConnectionInfo(TARGET, new SqlConnectionsInfo());

        DropTablesTool dropTablesTool = new DropTablesTool(connectorRepository);
        dropTablesTool.dropIndexes(TARGET);
        dropTablesTool.dropForeignKeys(TARGET);
        dropTablesTool.dropTables(TARGET);

        //add _Mapping TableFilter
        connectorRepository.addConnectorHint(SOURCE,new CustomTableNameFilterShop());
        connectorRepository.addConnectorHint(TARGET,new CustomTableNameFilterShop());

        //add _Mapping ColumnFilter
        connectorRepository.addConnectorHint(SOURCE,new CustomColumnNameFilterShop());
        connectorRepository.addConnectorHint(TARGET,new CustomColumnNameFilterShop());

        //add MappingColumn  --> rename columns
        connectorRepository.addConnectorHint(TARGET, new ColumnMapperHint() {
            @Override
            public ColumnMapper getValue() {
                return new CustomColumnRenameName()
                        .addReplacement("OFFICECODE", "ID_OFFICECODE")
                        .addReplacement("ORDERNUMBER", "ID_ORDERNUMBER")
                        .addReplacement("PHONE", "ID_PHONE")
                        .addReplacement("CITY", "ID_CITY");
            }
        });


        //add MappingTable  --> rename tables
        connectorRepository.addConnectorHint(TARGET, new TableMapperHint() {
            @Override
            public TableMapper getValue() {
                return new CustomTableRenameName()
                        .addReplacement("OFFICES", "TAB_OFFICES")
                        .addReplacement("ORDERS", "TAB_ORDERS")
                        .addReplacement("ORDERDETAILS", "TAB_ORDERDETAILS");
            }
        });



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
        System.out.println("Schema Done");


        SchemaCompatibilityIssues issues = new SchemaComparatorTool(connectorRepository).check(SOURCE, TARGET);
        System.out.println("Issues: "+ issues);
        if(!issues.isSevere()) {

            new DefaultTableCopyTool(connectorRepository).copyTables(SOURCE, TARGET);

        }

    }

}


