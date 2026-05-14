package org.mishu.DataAccess.Wrappers;

import org.mishu.DataAccess.ConnectionFactory;
import org.mishu.Utils.QuerySettings;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBWrapper<T> {

    private static final Logger LOGGER =
            Logger.getLogger(DBWrapper.class.getName());

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public DBWrapper(Class<T> type) {
        this.type = type;
    }

    private String createSelectAllQuery() {
        return "SELECT * FROM " + type.getSimpleName();
    }

    protected String createSelectQuery(String field) {
        return "SELECT * FROM " + type.getSimpleName()
                + " WHERE " + field + " = ?";
    }

    protected String createSelectLikeQuery(String field) {
        return "SELECT * FROM " + type.getSimpleName()
                + " WHERE " + field + " LIKE ?";
    }

    protected String createSelectLessThan(String field) {
        return "SELECT * FROM " + type.getSimpleName()
                + " WHERE " + field + " < ?";
    }

    protected String createSelectGreaterThan(String field) {
        return "SELECT * FROM " + type.getSimpleName()
                + " WHERE " + field + " > ?";
    }

    private String createInsertQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ").append(type.getSimpleName()).append(" (");

        Field[] fields = type.getDeclaredFields();
        for (int i = 1; i < fields.length; i++) {
            sb.append(fields[i].getName());
            if (i < fields.length - 1) sb.append(", ");
        }
        sb.append(") VALUES (");
        for (int i = 1; i < fields.length; i++) {
            sb.append("?");
            if (i < fields.length - 1) sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }

    private String createUpdateQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ").append(type.getSimpleName()).append(" SET ");

        Field[] fields = type.getDeclaredFields();
        for (int i = 1; i < fields.length; i++) {
            sb.append(fields[i].getName()).append(" = ?");
            if (i < fields.length - 1) sb.append(", ");
        }
        sb.append(" WHERE id = ?");
        return sb.toString();
    }

    protected List<T> createObjects(ResultSet rs) {
        List<T> list = new ArrayList<>();
        try {
            while (rs.next()) {
                T instance = type.getDeclaredConstructor().newInstance();
                for (Field field : type.getDeclaredFields()) {
                    Object value = rs.getObject(field.getName());
                    PropertyDescriptor pd =
                            new PropertyDescriptor(field.getName(), type);
                    pd.getWriteMethod().invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "error la create objects", e);
        }
        return list;
    }

    public T findById(int id) {
        Connection dbConnection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            dbConnection = ConnectionFactory.getConnection();
            statement = dbConnection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            return createObjects(resultSet).getFirst();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(dbConnection);
        }
        return null;
    }

    public List<T> findAll() {
        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = dbConnection.prepareStatement(createSelectAllQuery());
            rs = statement.executeQuery();
            return createObjects(rs);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + " DAO: findAll", e);
        } finally {
            ConnectionFactory.close(rs);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(dbConnection);
        }
        return new ArrayList<>();
    }

    private String createDeleteQuery() {
        return "DELETE FROM " + type.getSimpleName() + " WHERE id = ?";
    }

    public List<T> get(Object fieldValue,
                       String fieldName,
                       String query,
                       QuerySettings querySettings) {

        Connection dbConnection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            dbConnection = ConnectionFactory.getConnection();
            statement = dbConnection.prepareStatement(query);


            switch (fieldValue) {
                case String _ -> {
                    if (querySettings.useLike()) {
                        statement.setString(1, "%" + fieldValue + "%");
                    } else {
                        statement.setString(1, fieldValue.toString());
                    }
                }
                case Integer i -> {
                    statement.setInt(1, i);
                }
                case Double v -> statement.setDouble(1, v);
                default -> statement.setString(1, fieldValue.toString());
            }


            resultSet = statement.executeQuery();

            List<T> objects = createObjects(resultSet);

            if (!querySettings.all()) {
                return objects.isEmpty() ? null : List.of(objects.getFirst());
            }

            return objects;

        } catch (SQLException e) {
            LOGGER.log(Level.WARNING,
                    type.getSimpleName() + ":findBy" + fieldName + " " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(dbConnection);
        }

        return null;
    }

    public int insert(T object) {
        int insertedId = -1;
        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement insertStatement = null;
        try {
            insertStatement = dbConnection.prepareStatement(createInsertQuery(),
                    Statement.RETURN_GENERATED_KEYS);
            Field[] fields = type.getDeclaredFields();
            for (int i = 1; i < fields.length; i++) {
                fields[i].setAccessible(true);
                insertStatement.setObject(i, fields[i].get(object));
            }
            insertStatement.executeUpdate();

            ResultSet rs = insertStatement.getGeneratedKeys();
            if (rs.next()) {
                insertedId = rs.getInt(1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, type.getName() + " DAO: insert", e);
        } finally {
            ConnectionFactory.close(insertStatement);
            ConnectionFactory.close(dbConnection);
        }

        return insertedId;
    }

    public void update(T object) {
        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement updateStatement = null;
        try {
            updateStatement = dbConnection.prepareStatement(createUpdateQuery());
            Field[] fields = type.getDeclaredFields();

            for (int i = 1; i < fields.length; i++) {
                fields[i].setAccessible(true);
                updateStatement.setObject(i, fields[i].get(object));
            }

            fields[0].setAccessible(true);
            updateStatement.setObject(fields.length, fields[0].get(object));
            updateStatement.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, type.getName() + " DAO: update", e);
        } finally {
            ConnectionFactory.close(updateStatement);
            ConnectionFactory.close(dbConnection);
        }
    }

    public void delete(int id) {
        Connection dbConnection = ConnectionFactory.getConnection();
        PreparedStatement deleteStatement = null;
        try {
            deleteStatement = dbConnection.prepareStatement(createDeleteQuery());
            deleteStatement.setInt(1, id);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + " DAO: delete", e);
        } finally {
            ConnectionFactory.close(deleteStatement);
            ConnectionFactory.close(dbConnection);
        }
    }

}
