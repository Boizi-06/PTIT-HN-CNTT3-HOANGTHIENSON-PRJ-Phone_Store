package entity;

public class Product {
    private int id;
    private String name;
    private int categoryId;
    private String storage;
    private String color;
    private double price;
    private int stock;
    private String description;

    public Product() {}

    public Product(int id, String name, int categoryId, String storage,
                   String color, double price, int stock, String description) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.storage = storage;
        this.color = color;
        this.price = price;
        this.stock = stock;
        this.description = description;
    }

    // Getter - Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}