package ru.mirea.pois.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Credit {

    private Client client;

    private Client guarantor;

    private int sum;

    private double percent;

    private LocalDate dueTime;
}
