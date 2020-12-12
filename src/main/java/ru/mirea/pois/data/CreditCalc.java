package ru.mirea.pois.data;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditCalc {
    private Client mainClient;

    private Client guarantor;

    public int calcSum () {
        return mainClient.getSalary() + guarantor.getSalary() + mainClient.sumProperties();
    }

    public double calcPercent () {
        return (mainClient.getSalary() + guarantor.getSalary()) / (double) mainClient.sumProperties();
    }

}
