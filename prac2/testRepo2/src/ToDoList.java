import java.util.Iterator;
import java.util.Scanner;

public class ToDoList {
    private User user;
    private boolean isRunning;

    /**
     * Initializes the to-do list with the specified user.
     * @param user The user of the to-do list.
     */
    public ToDoList(User user) {
        this.user = user;
        this.isRunning = true;
    }

    /**
     * Displays the main menu to the user.
     */
    private void showMenu() {
        System.out.println("1. Show all items");
        System.out.println("2. Add an item");
        System.out.println("3. Mark an item as done");
        System.out.println("4. Mark an item as undone");
        System.out.println("5. Delete an item");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        handleMenu(choice);
    }

    /**
     * Loops the showMenu method based on the `isRunning` flag.
     */
    public void run() {
        while (isRunning) {
            showMenu();
        }
    }

    /**
     * Handles the user's choice, mapping the menu options to the corresponding methods.
     * @param choice The user's choice.
     */
    public void handleMenu(int choice) {
        switch (choice) {
            case 1:
                onShowAllItems();
                break;
            case 2:
                onAddItem();
                break;
            case 3:
                onMarkAsDone();
                break;
            case 4:
                onMarkAsUndone();
                break;
            case 5:
                onDeleteItem();
                break;
            case 6:
                onExit();
                break;
            default:
                System.out.println("Invalid choice!");
                showMenu();
        }
    }

    /**
     * Displays all the items in the list.
     */
    public void onShowAllItems() {
        for (int i = 0; i < user.getToDoItems().size(); i++) {
            ToDoItem item = user.getToDoItems().get(i);
            if (item.getIsDone()) {
                System.out.println(i + ". [X] " + item.getDescription());
            } else {
                System.out.println(i + ". [ ] " + item.getDescription());
            }
        }
    }

    /**
     * Requests the user for a new item description and adds it to the list.
     */
    public void onAddItem() {
        System.out.print("Enter the description of the item: ");
        Scanner scanner = new Scanner(System.in);
        String description = scanner.nextLine();
        // TODO Now: Call the add(ToDoItem item) method of the user's to-do items list to add a new item
        user.getToDoItems().add(new ToDoItem(description));
    }

    /**
     * Requests the user for an item number and marks it as done.
     */
    public void onMarkAsDone() {
        onShowAllItems();
        System.out.print("Enter the number of the item to mark as done: ");
        Scanner scanner = new Scanner(System.in);
        int itemNumber = scanner.nextInt();
        // TODO Now: Set the isDone field of the item at the specified index to true
        Iterator i = user.getToDoItems().iterator();
        while (i.hasNext()) {
            int loopCount = 0;
            if (itemNumber == loopCount) {
                ToDoItem toDo = (ToDoItem) i.next();
                toDo.setIsDone(true);
            }
            loopCount++;
        }

    }

    /**
     * Requests the user for an item number and marks it as undone.
     */
    public void onMarkAsUndone() {
        onShowAllItems();
        System.out.print("Enter the number of the item to mark as undone: ");
        Scanner scanner = new Scanner(System.in);
        int itemNumber = scanner.nextInt();
        // TODO Now: Set the isDone field of the item at the specified index to false
        Iterator i = user.getToDoItems().iterator();
        while (i.hasNext()) {
            int loopCount = 0;
            if (itemNumber == loopCount) {
                ToDoItem toDo = (ToDoItem) i.next();
                toDo.setIsDone(false);
            }
            loopCount++;
        }
    }

    /**
     * Requests the user for an item number and deletes it from the list.
     */
    public void onDeleteItem() {
        onShowAllItems();
        System.out.print("Enter the number of the item to delete: ");
        Scanner scanner = new Scanner(System.in);
        int itemNumber = scanner.nextInt();
        // TODO Now: Remove the item at the specified index from the user's to-do items list
        user.getToDoItems().remove(itemNumber);
    }

    /**
     * Exits the application, which sets the `isRunning` flag to false.
     */
    public void onExit() {
        isRunning = false;
    }
}