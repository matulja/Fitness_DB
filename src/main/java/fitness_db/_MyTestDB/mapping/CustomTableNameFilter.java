package fitness_db._MyTestDB.mapping;

import de.akquinet.jbosscc.guttenbase.hints.RepositoryTableFilterHint;
import de.akquinet.jbosscc.guttenbase.repository.RepositoryTableFilter;

/**
 * look at table name starts with "k"
 *
 * Created by mfehler on 27.03.17.
 */
public class CustomTableNameFilter extends RepositoryTableFilterHint {


    @Override
    public RepositoryTableFilter getValue() {
        return table -> {

            final String nameCase=table.getTableName();
            return nameCase.equals("FOO_ROLE");
        };
    }
}
