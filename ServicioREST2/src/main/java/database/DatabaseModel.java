/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.lang.NoSuchFieldException;
import java.lang.IllegalAccessException;
import java.lang.ClassCastException;
import static java.lang.System.out;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author alumne
 */
public abstract class DatabaseModel implements AutoCloseable {
    
    protected final Connection connection;
    
    private boolean __defined_options__ = false;
    
    private static final String INNER_EXCEPTION_NULL_PK_VALUE_MSG = "Search with null primary key";
    
    public DatabaseModel() throws SQLException{
        connection = DriverManager.getConnection(DBConfig.DB_URI);
        connection.setAutoCommit(true);
    }
    
    // sirve solo y unicamente para evitar tener que cambiar connection (ya que es final) en los hijos
    protected DatabaseModel(boolean connectionless) {
        connection = null;
    }
    
    public void checkAndUpdateTable() throws SQLException, ModelException {
        checkForConnection();
        
        String tablename = getTableName();
        
        HashMap<String, String> field_constraints = new HashMap<>();
        HashMap<String, String> field_pk_fk_constraints = new HashMap<>();
        ArrayList<String> drop_fields = new ArrayList<>();
        ArrayList<String> pk_fields = new ArrayList<>();
        ArrayList<ArrayList<String>> unique_together = getUniqueTogetherFields();
        
        
        boolean auto_pk_exists = false;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(TableColumn.class)) {
                String field_name = field.getName().toLowerCase();
                
                TableColumn tc = field.getAnnotation(TableColumn.class);
                String constraint = tc.dataType();
                if (tc.primaryKey()) {
                    if (tc.autoPK()) {
                        if (auto_pk_exists || pk_fields.size() > 0) throw new ModelException("Auto primary key must be the only primery key for the table");
                        constraint += " GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)";
                        field_pk_fk_constraints.put(field_name, "PRIMARY KEY(" + field_name + ")");
                        auto_pk_exists = true;
                    } 
                    else if (auto_pk_exists) throw new ModelException("Auto primary key must be the only primery key for the table");
                    else pk_fields.add(field_name);
                   
                }
                // pk ya es unica
                else  if (tc.unique() && !matrixContains(unique_together, field_name)) {
                    constraint += " UNIQUE";
                }
                // pk ya no es nula
                // columna unica no puede ser no nula (equivale a pk)
                else if (!tc.nullable()) {
                    constraint += " NOT NULL";
                }
                if (!tc.defaultValue().isEmpty()){
                    constraint += " DEFAULT " + tc.defaultValue();
                }
                if (!tc.primaryKey() && !tc.foreignKeyColumn().isEmpty() && !tc.foreignKeyTable().isEmpty()) {
                    String fk_constraint = "CONSTRAINT fk_" + field_name;
                    fk_constraint += " FOREIGN KEY(" +  field_name + ")";
                    fk_constraint += " REFERENCES " + tc.foreignKeyTable() + "(" + tc.foreignKeyColumn() + ")";
                    field_pk_fk_constraints.put(field_name, fk_constraint);
                }
                field_constraints.put(field_name, constraint);
            }
        }
        
        if (field_constraints.isEmpty()) return;
        if (!auto_pk_exists) {
            String pk_constraint = "PRIMARY KEY(";
            String key_name = null;
            for (String field_name : pk_fields) {
                if (key_name == null) key_name = field_name;
                pk_constraint += field_name + ", ";
            }
            pk_constraint = pk_constraint.substring(0, pk_constraint.length()-2) + ")";
            field_pk_fk_constraints.put(key_name, pk_constraint);
        }
        for (ArrayList<String> u_t : unique_together) {
            String unique_constraint = "UNIQUE(";
            String key_name = "";
            for (String field_name : u_t) {
                key_name += field_name;
                unique_constraint += field_name + ", ";
            }
            if (key_name != null) {
                
                unique_constraint = unique_constraint.substring(0, unique_constraint.length()-2) + ")";
                field_pk_fk_constraints.put(key_name, unique_constraint);
            }
        }
        
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getColumns(null, null, tablename, null);
        
        if (!rs.next()) {
            String query = "CREATE TABLE " + tablename + "(";
            for (String field_name : field_constraints.keySet()) {
                query += field_name + " " + field_constraints.get(field_name) + ", ";
            }
            for (String field_name : field_pk_fk_constraints.keySet()) {
                query += field_pk_fk_constraints.get(field_name) + ", ";
            }
            query = query.substring(0, query.length()-2) + ")";
           
            System.out.println(query);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.executeUpdate();
            return;
        }
        
        do {
            String column = rs.getString("COLUMN_NAME");
            if (field_constraints.containsKey(column.toLowerCase())) {
                field_constraints.remove(column.toLowerCase());
                field_pk_fk_constraints.remove(column.toLowerCase());
                Object[] keySet = field_pk_fk_constraints.keySet().toArray();
                for (int i = 0; i < keySet.length; i++) 
                    if (((String)keySet[i]).contains(column.toLowerCase())) 
                        field_pk_fk_constraints.remove((String)keySet[i]);
            }
            else {
                drop_fields.add(column.toLowerCase());
            }
        } while(rs.next());
        
        if (field_constraints.isEmpty() && drop_fields.isEmpty()) return;
        
       
        String query_base = "ALTER TABLE " + tablename + " ";
      
        connection.setAutoCommit(false);
        
        try {
            for (String field_name : field_constraints.keySet()) {
                String query = query_base + "ADD COLUMN " + field_name + " " + field_constraints.get(field_name);
                System.out.println(query);
                PreparedStatement statement = connection.prepareStatement(query);
                statement.executeUpdate();
            }

            for (String field_name : field_pk_fk_constraints.keySet()) {
                String query = query_base + "ADD " + field_pk_fk_constraints.get(field_name);
                System.out.println(query);
                PreparedStatement statement = connection.prepareStatement(query);
                statement.executeUpdate();
            }
            
            for (String field_name : drop_fields){
                String query = query_base + "DROP COLUMN " + field_name;
                System.out.println(query);
                PreparedStatement statement = connection.prepareStatement(query);
                statement.executeUpdate();
            }
            
            connection.commit();
        }
        catch (SQLException e) {
            connection.rollback();
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void recreateTable() throws SQLException, ModelException {
        checkForConnection();
        
        String tablename = getTableName();
        
        try {
            dropTable();
        }
        catch (SQLException e) {
            if (!e.getSQLState().equals("42Y55")) throw e;
        }
        
        checkAndUpdateTable();
    }

    public void dropTable() throws SQLException, ModelException {
        checkForConnection();
        
       String tablename = getTableName();
        
        String query = "DROP TABLE " + tablename;
        PreparedStatement statement = connection.prepareStatement(query);
        statement.executeUpdate();
       
    }
    
    @Override
    public void close() {
        try {
            connection.close();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    /*
        Returns current class name without the package
    */
    protected String getClassName() {
        String className = this.getClass().getName();
        String[] classNameSplit = className.split("\\.");
	return classNameSplit[classNameSplit.length-1]; 
    }
    
    
    public String getTableName() {
        String tablename;
        Class meta = getMetaClass();
        if (meta == null) return getClassName().toUpperCase();
        Boolean accessible = null;
        Field field = null;
        try {
            field = meta.getDeclaredField("tablename");
            accessible = field.isAccessible();
            field.setAccessible(true);
            tablename = (String) field.get(this);
        }
        catch (NoSuchFieldException | IllegalAccessException e){
            tablename = getClassName();
        }
        finally {
            if (field != null && accessible != null) {
                field.setAccessible((boolean) accessible);
            }
        }
                
        return tablename.toUpperCase();
    }
    
    protected void checkForConnection() throws ModelException {
        if (connection == null) {
            throw new ModelException("This instance is not suitable for making queries to the database");
        }
    }
    
    protected void checkForNoConnection() throws ModelException {
        if (connection != null) {
            throw new ModelException("This instance is for making database queries only");
        }
    }
    
    protected <Table extends DatabaseModel> Table buildTableClass(Class<Table> tabClass, ResultSet rs) throws ModelException, SQLException {
        Table tab;
        try {
            Constructor ctor = tabClass.getDeclaredConstructor(boolean.class);
            boolean accessible = ctor.isAccessible();
            ctor.setAccessible(true);
            tab = (Table) ctor.newInstance(true);
            ctor.setAccessible(accessible);
            
        }
        catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ModelException(e.getMessage());
        }
        catch (NoSuchMethodException e) {
            throw new ModelException("To use .buildTableClass the " + getClassName() + " class has to define a constructor with boolean paramether and "
                    + "call Database's Databse(boolean coneactionless) constructor");
        }
        
        for (Field field : tabClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableColumn.class)) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    if (field.getType() == String.class) {
                        field.set(tab, rs.getString(field.getName()));

                    }
                    else if (field.getType() == int.class || field.getType() == Integer.class) {
                        field.set(tab, rs.getInt(field.getName()));
                    }
                    else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.set(tab, rs.getBoolean(field.getName()));
                    }
                    else throw new ModelException("Unsuported field type");
                }
                catch(IllegalAccessException e) {
                    throw new ModelException(e.getMessage());
                }
                field.setAccessible(accessible);
            }
        }
        
        return tab;
    }
    
    protected <Table extends DatabaseModel> ArrayList<Table> getAll(Class<Table> tabClass) throws SQLException, ModelException {
        checkForConnection();
        
        String tablename = getTableName();
        
        String query = "SELECT * FROM " + tablename;
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();
        
        return buildBunchTableClass(tabClass, rs);
       
    }
     
    abstract public <Table extends DatabaseModel> ArrayList<Table> getAll() throws SQLException, ModelException;
    
    
    protected <Table extends DatabaseModel> ArrayList<Table> getFiltered(Class<Table> tabClass, String filter, Object[] values) throws SQLException, ModelException {
        checkForConnection();
        String tablename = getTableName();
        String query = "SELECT * FROM " + tablename + " WHERE " + filter;
        PreparedStatement statement = connection.prepareStatement(query);
        
        for (int i = 1; i <= values.length; i++) {
            statement.setObject(i, values[i-1]);
        }
        
        ResultSet rs = statement.executeQuery();
                
        return buildBunchTableClass(tabClass, rs);
    }
    
    /*protected <Table extends DatabaseModel> ArrayList<Table> getImages(Class<Table> tabClass, Object[] values) throws SQLException, ModelException {
       /* checkForConnection();
        String tablename = getTableName();
        String query = "SELECT * FROM " + tablename +  "WHERE " + im1 + "AND" + im2 + "AND" + im3 + "AND" +im4;
        PreparedStatement statement = connection.prepareStatement(query);
        
        for (int i = 1; i <= values.length; i++) {
            statement.setObject(i, values[i-1]);
        }
        ResultSet rs = statement.executeQuery();
        
        return buildBunchTableClass(tabClass, rs);*/
       /*checkForConnection();
       String tablename = getTableName();
       String query = "SELECT * FROM " + tablename + "WHERE file_name = ? OR title = ? OR author = ? OR storage_date = ?";
       PreparedStatement statement = connection.prepareStatement(query);
       for (int i = 1; i <= values.length; i++) {
           statement.setObject(i, values[i-1]);
       }
       out.println("Estic viu");
       ResultSet rs = statement.executeQuery();
       return buildBunchTableClass(tabClass,rs);
    } */
    
    protected <Table extends DatabaseModel> ArrayList<Table> buildBunchTableClass(Class<Table> tabClass, ResultSet rs)throws SQLException, ModelException {
        ArrayList<Table> result = new ArrayList<>();
        
        while(rs.next()) {
            result.add(buildTableClass(tabClass, rs));
        }
        return result;
    }
    
    public <Table extends DatabaseModel> void saveToDB(Table table) throws SQLException, ModelException {
        checkForConnection();
        
        if (table == null) return;
        
        if (!this.getClass().equals(table.getClass())) 
            throw new ModelException(
                    "Trying to save a " + table.getClassName() 
                    + " instance from the " + this.getClassName() + " class"
            );
        
        String tablename = getTableName();
        
        boolean isNew = true;
        String valuesNames =  "";
        String values = "";
        String setValues = "";
        Field pkField = null;
        
        ArrayList<Field> db_fields = new ArrayList<>();
       
        for (Field field : table.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(TableColumn.class)) {
                TableColumn tc = field.getAnnotation(TableColumn.class);
                if (tc.primaryKey()) {
                    pkField = field;
                    boolean accessible = field.isAccessible();
                    field.setAccessible(true);
                    isNew = !recordExists(field, table);
                    field.setAccessible(accessible);
                    continue;
                }
                
                db_fields.add(field);
                valuesNames += field.getName() + ", ";
                values += "?, ";
                setValues += field.getName() + " = ?, ";
                        
            }
        }
        
        if (db_fields.isEmpty()) return;
        valuesNames = valuesNames.substring(0, valuesNames.length()-2);
        values = values.substring(0, values.length()-2);
        setValues = setValues.substring(0, setValues.length()-2);
        
        if (pkField == null) throw new ModelException("Class " + getClassName() + "has not defined a primary key field");
        
        
        String query;
        PreparedStatement statement;
        if (isNew && !pkField.getAnnotation(TableColumn.class).autoPK())  {
            query = "INSERT INTO " + tablename + "(" + valuesNames + ", " + pkField.getName() + ") VALUES(" + values + ", ?)";
            db_fields.add(pkField);
            statement = connection.prepareStatement(query);
        }
        else if (isNew) {
            query = "INSERT INTO " + tablename + "(" + valuesNames + ") VALUES(" + values + ")";
            statement = connection.prepareStatement(query);
        }
        else {
            query = "UPDATE " + tablename + " SET " + setValues + " WHERE " + pkField.getName() + " = ?";
            statement = connection.prepareStatement(query);
            statement = DatabaseModel.setPKFilter(pkField, table, statement, db_fields.size() + 1);
        }
        
        
        for (int i = 1; i <= db_fields.size(); i++){
            Field field = db_fields.get(i-1);
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                if (field.getType() == String.class) {
                    String value = (String) field.get(table);
                    statement.setString(i, value);
                }
                else if (field.getType() == int.class || field.getType() == Integer.class) {
                    Integer value = (Integer) pkField.get(table);
                    int i_value = (value == null) ? 0 : value.intValue();
                    statement.setInt(i, i_value);
                }
                else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                    Boolean value = (Boolean) pkField.get(table);
                    boolean b_value = (value == null) ? false : value.booleanValue();
                    statement.setBoolean(i, b_value);
                }
                else throw new ModelException("Unsuported field type");
            }
            catch(IllegalAccessException e) {
                throw new ModelException(e.getMessage());
            }

            field.setAccessible(accessible);
        }
        System.out.println("SAVE: " + query);
        statement.executeUpdate();
        
    }
    
    public <Table extends DatabaseModel> void deleteFromDB(Table tableObj) throws SQLException, ModelException {
        checkForConnection();
        
        if (tableObj == null) return;
        
        String tableName = getTableName();
        
        Field pkField = null;
        
        String query = "DELETE FROM " + tableName + " WHERE ";
        for (Field field : tableObj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(TableColumn.class)) {
                TableColumn tc = field.getAnnotation(TableColumn.class);
                if (tc.primaryKey()) {
                    pkField = field;
                    query += field.getName().toLowerCase()  + " = ?";
                }
            }
        }
        
        
        if (pkField == null) throw new ModelException("No primary key field found");
        
        PreparedStatement statement = connection.prepareStatement(query);
        
        boolean accessible = pkField.isAccessible();
        pkField.setAccessible(true);
        try {
            if (pkField.getType() == int.class | pkField.getType() == Integer.class) {
                Integer value = (Integer) pkField.get(tableObj);
                int i_value = (value == null) ? 0 : value.intValue();
                statement.setInt(1, i_value);
            }
            if (pkField.getType() == String.class) {
                statement.setString(1, (String) pkField.get(tableObj));
            }
        }
        catch (IllegalAccessException e) {
            throw new ModelException(e.getMessage());
        }
        pkField.setAccessible(accessible);
        
        statement.executeUpdate();
        
    }
    
    private <Table extends DatabaseModel> boolean recordExists(Field pkField, Table object) throws SQLException, ModelException {
        String tablename = object.getTableName();
        TableColumn tc = pkField.getAnnotation(TableColumn.class);
        if (!tc.primaryKey()) throw new ModelException(pkField.getName() + " is not a primary key on " + tablename);
        
        String query = "SELECT * FROM " + tablename + " WHERE " + pkField.getName() + " = ? ";
        PreparedStatement statement = connection.prepareStatement(query);
       
        try {
            statement = DatabaseModel.setPKFilter(pkField, object, statement, 1);
        }
        catch (ModelException e) {
            if (e.getMessage().equals(INNER_EXCEPTION_NULL_PK_VALUE_MSG))
                return false;
            throw e;
        }
        
        ResultSet rs = statement.executeQuery();
       
        boolean res = rs.next();
        
        
        return res;
    }
    
    private static <Table extends DatabaseModel> PreparedStatement setPKFilter(Field pkField, Table object, PreparedStatement statement, int pos) throws SQLException, ModelException {
        String tablename = object.getTableName();
        TableColumn tc = pkField.getAnnotation(TableColumn.class);
        if (!tc.primaryKey()) throw new ModelException(pkField.getName() + " is not a primary key on " + tablename);
        
        boolean accessible = pkField.isAccessible();
        pkField.setAccessible(true);
        try {
            if (pkField.getType() == String.class) {
                String value = (String) pkField.get(object);
                if (value == null || value.isEmpty()) throw new ModelException(INNER_EXCEPTION_NULL_PK_VALUE_MSG);
                statement.setString(pos, value);
            }
            else if (pkField.getType() == int.class || pkField.getType() == Integer.class) {
                Integer value = (Integer) pkField.get(object);
                int i_value = (value == null) ? 0 : value.intValue();
                if (i_value == 0) throw new ModelException(INNER_EXCEPTION_NULL_PK_VALUE_MSG);
                statement.setInt(pos, i_value);
            }
            else throw new ModelException("Unsuported field type");
        }
        catch (IllegalAccessException e) {
            throw new ModelException(e.toString());
        }
        finally {
            pkField.setAccessible(accessible);
        }
        
        return statement;
       
        
        
        
    }
    
    private Class<?> getMetaClass(){
        for (Class klass : this.getClass().getDeclaredClasses()) {
            String klass_name = klass.getName();
            String[] klass_name_split = klass_name.split("\\$");
            String klass_name_clean = klass_name_split[klass_name_split.length-1];
            if (klass_name_clean.equals("Meta")) return klass;
        }
        return null;
    }
    
    private boolean matrixContains(ArrayList<ArrayList<String>> arrmtx, String to_search) {
        for (ArrayList<String> arrlist : arrmtx) {
            if (arrlist.contains(to_search)) return true;
        }
        return false;
    }
    
    private ArrayList<ArrayList<String>> getUniqueTogetherFields() {
        Class meta = getMetaClass();
        ArrayList<ArrayList<String>> unique_together = new ArrayList<>();
        if (meta == null) return unique_together;
        Field field;
        try {
            field = meta.getDeclaredField("unique_together");
        }
        catch (NoSuchFieldException e) {
            return unique_together;
        }
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        if (field.getType() == String[].class) {
            ArrayList<String> u_t = new ArrayList<>();
            try {
                for (String s : (String []) field.get(this))
                    u_t.add(s);
            }
            catch (IllegalAccessException e) {
                throw new ModelException(e.getMessage());
            }
            unique_together.add(u_t);
        }
        else if (field.getType() == String[][].class) {
            try {
                for (String[] ss : (String [][]) field.get(this)) {
                    ArrayList<String> u_t = new ArrayList<>();
                    for (String s : ss) {
                        u_t.add(s);
                    }
                    unique_together.add(u_t);
                }
            }
            catch (IllegalAccessException e) {
                throw new ModelException(e.getMessage());
            }
        }
        field.setAccessible(accessible);
                
        return unique_together;
    }
}
