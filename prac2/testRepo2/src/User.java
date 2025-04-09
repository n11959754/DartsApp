import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private ArrayList<ToDoItem> toDoItems;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.toDoItems = new ArrayList<>();
    }

    /**
     * Gets the username of the user.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of the user.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the username of the user.
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password of the user.
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the to-do items of the user.
     * @return A reference to the list of to-do items, which can be modified from outside the class.
     */
    public ArrayList<ToDoItem> getToDoItems() {
        return toDoItems;
    }
}