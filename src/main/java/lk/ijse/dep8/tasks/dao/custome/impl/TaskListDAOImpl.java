package lk.ijse.dep8.tasks.dao.custome.impl;

import lk.ijse.dep8.tasks.dao.custome.TaskListDAO;
import lk.ijse.dep8.tasks.dao.exception.DataAccessException;
import lk.ijse.dep8.tasks.dto.TaskListDTO;
import lk.ijse.dep8.tasks.entity.TaskList;
import lk.ijse.dep8.tasks.util.ResponseStatusException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskListDAOImpl implements TaskListDAO {
    private Connection connection;

    public TaskListDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public TaskList save(TaskList list) {
        try {
            if (!existsById(list.getId())) {
                PreparedStatement stm = connection.prepareStatement("INSERT INTO task_list (name, user_id) VALUES (?,?)",Statement.RETURN_GENERATED_KEYS);
                stm.setString(1, list.getName());
                stm.setString(2, list.getUserId());
                if (stm.executeUpdate()==1){
                    ResultSet rst = stm.getGeneratedKeys();
                    rst.next();
                    list.setId(rst.getInt(1));
                    if (stm.executeUpdate() != 1) {
                        throw new SQLException("Failed to save the user");
                    }
                }
            } else {
                PreparedStatement stm = connection.prepareStatement("UPDATE task_list SET name=?, user_id=? WHERE id=?");
                stm.setString(1, list.getName());
                System.out.println(list.getUserId());
                stm.setString(2, list.getUserId());
                stm.setInt(3, list.getId());
                if (stm.executeUpdate() != 1) {
                    throw new SQLException("Failed to update the user");
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Integer listId) {
        try {
            if (!existsById(listId)) {
                throw new DataAccessException("No user Found!");
            }
            PreparedStatement stm = connection.prepareStatement("DELETE FROM task_list WHERE id=?");
            stm.setInt(1, listId);
            if (stm.executeUpdate() != 1) {
                throw new SQLException("Failed to delete the user");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<TaskList> findById(Integer listId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM task_list WHERE id=?");
            stm.setInt(1, listId);
            ResultSet rst = stm.executeQuery();
            if (rst.next()) {
                return Optional.of(new TaskList(rst.getInt("id"),
                        rst.getString("name"),
                        rst.getString("user_id")));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(Integer listId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT id FROM task_list WHERE id=?");
            stm.setInt(1, listId);
            return stm.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TaskList> findAll() {
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM task_list");
            List<TaskList> tasks = new ArrayList<>();
            while (rst.next()) {
                tasks.add(new TaskList(rst.getInt("id"),
                        rst.getString("name"),
                        rst.getString("user_id")));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT COUNT(*) AS count FROM task_list");
            if (rst.next()) {
                return rst.getLong("count");
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean existTaskListByIdAndUserId(int taskListId, String userId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM task_list t WHERE t.id=? AND t.user_id=?");
            stm.setInt(1, taskListId);
            stm.setString(2, userId);
            return stm.executeQuery().next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<TaskList> getTaskListByIdAndUserId(int taskListId, String userId) {
        try {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM task_list t WHERE t.id=? AND t.user_id=?");
            stm.setInt(1, taskListId);
            stm.setString(2, userId);
            ResultSet rst = stm.executeQuery();
            if (rst.next()){
                return Optional.of(new TaskList(rst.getInt("id"),rst.getString("name"),
                        rst.getString("user_id")));
            }else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<List<TaskList>> findByUserId(String userId) {
        ArrayList<TaskList> taskLists;
        try {
            PreparedStatement stm = connection.
                    prepareStatement("SELECT * FROM task_list t WHERE t.user_id=?");
            stm.setString(1, userId);
            ResultSet rst = stm.executeQuery();
            taskLists = new ArrayList<>();
            while (rst.next()) {
                int id = rst.getInt("id");
                String title = rst.getString("name");
                taskLists.add(new TaskList(id, title, userId));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch the TaskLists");
        }
        return Optional.of(taskLists);
    }


}
