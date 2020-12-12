package ru.mirea.pois.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    private String name;

    private int salary;

    private List<Property> clientProperties;

    private int creditRate;

    private String materialStatus;

    private ClientType clientType;

    public boolean isAllMatches(int salary, int rate, int propertySum) {
        return salaryMatches(salary) && isRateMathes(rate) && sumProperties() > propertySum;
    }

    public boolean salaryMatches(int salary) {
        return this.salary > salary;
    }

    public boolean isRateMathes(int rate) {
        return creditRate > rate;
    }

    public boolean isMainClient() {
        return clientType == ClientType.MAIN;
    }

    public int sumProperties() {
        return clientProperties.stream().map(Property::getPrice).mapToInt(s -> s.intValue()).sum();
    }

}
