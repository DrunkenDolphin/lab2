package ru.mirea.pois.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    private PropertyType propertyType;

    private int price;
}
