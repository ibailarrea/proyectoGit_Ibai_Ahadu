
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CategoryList {

    private List<Category> categories;

    public CategoryList() {
        categories = new ArrayList<>();
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public Category getCategoryByName(String name) {
        for (Category category : categories) {
            if (category.getName().equals(name)) {
                return category;
            }
        }
        return null;
    }

    public boolean addCategory(Category category) {
        if (getCategoryByName(category.getName()) != null) {
            return false;
        }

        categories.add(category);
        return true;
    }

    public boolean deleteCategory(String name) {
        Category category = getCategoryByName(name);

        if (category == null) {
            return false;
        }

        categories.remove(category);
        return true;
    }

    public boolean addUserToCategory(String categoryName, int userId) {
        Category category = getCategoryByName(categoryName);

        if (category == null) {
            return false;
        }

        return category.addUserId(userId);
    }

    public boolean removeUserFromCategory(String categoryName, int userId) {
        Category category = getCategoryByName(categoryName);

        if (category == null) {
            return false;
        }

        return category.removeUserId(userId);
    }

    public void saveToFile(String filePath) throws IOException {
        try (ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(filePath))) {

            output.writeObject(categories);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filePath)
            throws IOException, ClassNotFoundException {

        try (ObjectInputStream input =
                new ObjectInputStream(new FileInputStream(filePath))) {

            categories = (ArrayList<Category>) input.readObject();
        }
    }
}