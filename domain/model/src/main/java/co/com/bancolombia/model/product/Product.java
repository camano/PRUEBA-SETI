package co.com.bancolombia.model.product;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Product {
    private final String id;
    private final String name;
    private int stock;

    public Product(String id, String name, int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("Stock no puede se menor a 0");
        }
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public void updateStock(int stock) {
        this.stock = stock;
    }
}
