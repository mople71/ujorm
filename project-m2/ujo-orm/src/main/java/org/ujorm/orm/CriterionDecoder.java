/*
 *  Copyright 2009-2016 Pavel Ponec
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.ujorm.orm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ujorm.CompositeKey;
import org.ujorm.Key;
import org.ujorm.core.UjoManager;
import org.ujorm.criterion.BinaryCriterion;
import org.ujorm.criterion.Criterion;
import org.ujorm.criterion.Operator;
import org.ujorm.criterion.ValueCriterion;
import org.ujorm.orm.metaModel.MetaColumn;
import org.ujorm.orm.metaModel.MetaDatabase;
import org.ujorm.orm.metaModel.MetaParams;
import org.ujorm.orm.metaModel.MetaTable;
import org.ujorm.orm.metaModel.MoreParams;
import static org.ujorm.core.UjoTools.SPACE;

/**
 * SQL Criterion Decoder.
 * @author Pavel Ponec
 * @composed 1 - 1 SqlDialect
 */
public class CriterionDecoder {

    final protected OrmHandler handler;
    final protected SqlDialect dialect;

    final protected Criterion criterion;
    final protected List<Key> orderBy;
    final protected String sql;
    /** List of the non-null criterion values */
    final protected List<ValueCriterion> values;
    /** List of the nullable criterion values */
    final protected List<ValueCriterion> nullValues;
    /** All table set */
    final protected Set<TableWrapper> tables;
    final protected MetaTable baseTable;
    /** EFFECTIVA REQUEST: to enforce printing all Ujorm joined tables */
    final protected boolean printAllJoinedTables;

    /**
     * Constructor
     * @param criterion Criterion non-null
     * @param baseTable Base ORM table model
     */
    public CriterionDecoder(Criterion criterion, MetaTable baseTable) {
        this(criterion, baseTable, null);
    }

    /**
     * Constructor
     * @param criterion Criterion non-null
     * @param baseTable Base ORM table model
     * @param orderByItems The order item list is not mandatory (can be null).
     */
    public CriterionDecoder(Criterion criterion, MetaTable baseTable, List<Key> orderByItems) {
        final MetaDatabase database = baseTable.getDatabase();
        this.baseTable = baseTable;
        this.criterion = criterion;
        this.dialect = database.getDialect();
        this.orderBy = orderByItems;
        this.handler = database.getOrmHandler();
        this.values = new ArrayList<ValueCriterion>();
        this.nullValues = new ArrayList<ValueCriterion>();
        this.tables = new HashSet<TableWrapper>();
        this.tables.add(baseTable);
        this.printAllJoinedTables = MetaParams.MORE_PARAMS.add(MoreParams.PRINT_All_JOINED_TABLES).of(handler.getParameters());

        final StringBuilder sqlBuffer = new StringBuilder(64);
        writeRelations(sqlBuffer);
        writeConditions(sqlBuffer);
        this.sql = sqlBuffer.toString();
    }

    /**
     * Unpack criterion
     * @param c The non-null criterion
     * @param sql SQL output buffer;
     */
    protected void unpack(final Criterion c, final StringBuilder sql) {
        if (c.isBinary()) {
            unpackBinary((BinaryCriterion)c, sql);
        } else try {
            final ValueCriterion origCriterion = (ValueCriterion) c;
            final ValueCriterion valueCriterion = dialect.printCriterion(origCriterion, sql);
            if (valueCriterion != null) {
                values.add(valueCriterion);
            } else {
                nullValues.add(origCriterion);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** Unpack criterion. */
    @SuppressWarnings("fallthrough")
    private void unpackBinary(final BinaryCriterion eb, final StringBuilder sql) {

        boolean or = false;
        switch (eb.getOperator()) {
            case OR:
                or = true;
            case AND:
                if (or) sql.append(" (");
                unpack(eb.getLeftNode(), sql);
                sql.append(SPACE);
                sql.append(eb.getOperator().name());
                sql.append(SPACE);
                unpack(eb.getRightNode(), sql);
                if (or) sql.append(") ");
                break;
            case NOT:
                sql.append(SPACE);
                sql.append(eb.getOperator().name());
                sql.append(" (");
                unpack(eb.getRightNode(), sql); // same criterion in both nodes
                sql.append(") ");
                break;
            default:
                String message = "Operator is not supported in the SQL statement: " + eb.getOperator();
                throw new UnsupportedOperationException(message);
        }
    }

    /** Returns a column count */
    public int getColumnCount() {
        return values.size();
    }

    /** Returns direct column or throw an exception */
    public MetaColumn getColumn(int i) throws IllegalArgumentException {
        final Key p = values.get(i).getLeftNode();
        final MetaColumn ormColumn = (MetaColumn) handler.findColumnModel(p, true);
        return ormColumn;
    }

    /** Returns operator. */
    public Operator getOperator(int i) {
        final Operator result = values.get(i).getOperator();
        return result;
    }

    /** Returns value */
    public Object getValue(int i) {
        Object result = values.get(i).getRightNode();
        return result;
    }

    /** Returns an extended value to the SQL statement */
    public Object getValueExtended(int i) {
        ValueCriterion crit = values.get(i);
        Object value = crit.getRightNode();

        if (value==null) {
            return value;
        }
        if (crit.isInsensitive()) {
            // Note: ! "Geß".toUpperCase().equals("GES")
            value = value.toString().toLowerCase();
        }
        switch (crit.getOperator()) {
            case CONTAINS:
            case CONTAINS_CASE_INSENSITIVE:
                return "%"+value+"%";
            case STARTS:
            case STARTS_CASE_INSENSITIVE:
                return value+"%";
            case ENDS:
            case ENDS_CASE_INSENSITIVE:
                return "%"+value;
            default:
                return value;
        }
    }

    /** Returns the criterion from constructor. */
    public Criterion getCriterion() {
        return criterion;
    }

//    /** Returns the relation criterion for the INNER JOIN phase. */
//    public Criterion getCriterionForRelations() {
//        if (criterion instanceof BinaryCriterion) {
//            return ((BinaryCriterion) criterion).getLeftNode();
//        }
//        return null;
//    }
//
//    /** Returns the relation criterion for the INNER JOIN phase. */
//    public Criterion getCriterionForConditions() {
//        if (criterion instanceof BinaryCriterion) {
//            return ((BinaryCriterion) criterion).getRightNode();
//        }
//        return null;
//    }


    /** Returns a SQL WHERE 'expression' of an empty string if no condition is found. */
    public String getWhere() {
        return sql.toString();
    }

    /** Is the SQL statement empty?  */
    public boolean isEmpty() {
        return sql.length()==0;
    }

    /** Returns the first direct key. */
    public Key getBaseProperty() {
        Key result = null;
        for (ValueCriterion eval : values) {
            if (eval.getLeftNode()!=null) {
                result = eval.getLeftNode();
                break;
            }
        }
        while(UjoManager.isCompositeKey(result)) {
            result = ((CompositeKey)result).getFirstKey();
        }
        return result;
    }

   /** Write the relation conditions */
    @SuppressWarnings("unchecked")
    protected void writeRelations(final StringBuilder sql) {
        if (criterion != null) {
            unpack(criterion, sql);
        }
    }

    /** Write the value conditions */
    @SuppressWarnings("unchecked")
    protected void writeConditions(final StringBuilder sql) {
        if (criterion==null && orderBy==null) {
            return;
        }

        final Collection<AliasKey> relations = getPropertyRelations();
        final boolean parenthesis = sql.length() > 0 && !relations.isEmpty();
        if (parenthesis) {
            sql.append(" AND (");
        }

        boolean andOperator = false;
        for (AliasKey key : relations) try {
            final ColumnWrapper fk1 = key.getColumn(handler);
            final MetaTable tab1 = fk1.getModel().getTable();
            final ColumnWrapper pk2 = fk1.getModel().getForeignColumns().get(0).addTableAlias(key.aliasTo);
            final MetaTable tab2 = pk2.getModel().getTable();
            //
            tables.add(tab1.addAlias(key.getAliasFrom()));
            tables.add(tab2.addAlias(key.getAliasTo()));

            {// TODO: for all foreign columns:
                if (andOperator) {
                    sql.append(" AND ");
                } else {
                    andOperator = true;
                }

                dialect.printColumnAlias(fk1, sql);
                sql.append(" = ");
                dialect.printColumnAlias(pk2, sql);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        if (parenthesis) {
            sql.append(")");
        }
    }

    /** Returns the unique direct key relations. */
    protected Collection<AliasKey> getPropertyRelations() {
        final Set<AliasKey> result = new HashSet<AliasKey>();
        final ArrayList<ValueCriterion> allValues = new ArrayList<ValueCriterion>
                (values.size() + nullValues.size());
        allValues.addAll(values);
        allValues.addAll(nullValues);

        for (ValueCriterion<?> value : allValues) {
            final Key<?,?> p1 = value.getLeftNode();
            if (p1 != null) {
                AliasKey.addRelations(p1, result);
                final Object p2 = value.getRightNode();
                if (p2 instanceof CompositeKey) {
                    AliasKey.addRelations((CompositeKey)p2, result);
                }
            }
        }

        // Get relations from the 'order by':
        if (orderBy != null) {
            for (Key p1 : orderBy) {
                AliasKey.addRelations((CompositeKey) p1, result);
            }
        }

        return result;
    }

    /** Get Base Table */
    public MetaTable getBaseTable() {
        return baseTable;
    }

    /** Returns all participated tables include the parameter table. */
    public int getTableCount() {
        return tables.size();
    }

    /** Returns all participated tables include the parameter table. */
    public TableWrapper[] getTables() {
        if (printAllJoinedTables) {
            final Set<TableWrapper> result = new HashSet<TableWrapper>();
            result.addAll(tables);

            //EFFECTIVA REQUEST: TR-1771: to enforce printing Ujorm joined tables
            ArrayList<ValueCriterion> allValues = new ArrayList<ValueCriterion>(values.size() + nullValues.size());
            allValues.addAll(values);
            allValues.addAll(nullValues);
            for (ValueCriterion value : allValues) {
                Object o1 = value.getLeftNode();
                Object o2 = value.getRightNode();
                if (o1 instanceof Key) {
                  //final TableWrapper table = handler.findColumnModel((Key) o1).getTable();
                    final TableWrapper table = handler.findTableModel((Key) o1);
                    result.add(table);
                }
                if (o2 instanceof Key) {
                  //final TableWrapper table = handler.findColumnModel((Key) o2).getTable();
                    final TableWrapper table = handler.findTableModel((Key) o2);
                    result.add(table);
                }
            }

            return result.toArray(new TableWrapper[result.size()]);
        } else {
            // The original Ujorm code:
            return tables.toArray(new TableWrapper[tables.size()]);
        }
    }

    /** Returns all participated tables include the parameter table. The 'baseTable' is on the first position always. */
    public TableWrapper[] getTablesSorted() {
        final TableWrapper[] result = getTables();
        if (result.length > 1 && result[0] != baseTable) {
            for (int i = result.length - 1; i >= 1; i--) {
                if (result[i] == baseTable) {
                    result[i] = result[0];
                    result[0] = baseTable;
                }
            }
        }
        return result;
    }

    /** Returns handler */
    public OrmHandler getHandler() {
        return handler;
    }

    /** Returns the criterion */
    @Override
    public String toString() {
        return criterion!=null ? criterion.toString() : null ;
    }
}
