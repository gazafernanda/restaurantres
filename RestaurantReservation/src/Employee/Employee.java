package Employee;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import Main.DatabaseManager;

public class Employee {
    private int employeeId;
    private String employeeName;
    private String employeeBranch;

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeBranch() {
        return employeeBranch;
    }

    public void setEmployeeBranch(String employeeBranch) {
        this.employeeBranch = employeeBranch;
    }

    public Employee(int employeeId, String employeeName, String employeeBranch) {
        super();
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeBranch = employeeBranch;
    }
}
