package co.com.bancolombia.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("franchises")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FranchiseEntity {

    @Id
    private String id;
    private String name;
}
