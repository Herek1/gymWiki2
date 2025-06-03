package database.dao;

import database.handlers.ErrorHandler;
import database.utils.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public abstract class AbstractDAO {
    protected final Connection conn;
    protected final Message message;
    protected final ErrorHandler errorHandler;

    public AbstractDAO(Connection conn) {
        this.conn = conn;
        this.message = new Message();
        this.errorHandler = new ErrorHandler();
    }

}
