package edu.siue.accountingbootcamp.models;

public enum StarType {
    BLANK,
    BRONZE("Bronze", 70),
    SILVER("Silver", 85),
    GOLD("Gold", 100);

    private String stringValue;
    private double doubleValue;

    StarType(){
    }

    StarType(String toString, double value) {
        stringValue = toString;
        doubleValue = value;
    }

    public double getMinimumValue() { return this.doubleValue; }

    public static StarType getStarTypeFromPercentage(double percentage) {
        if (percentage < BRONZE.getMinimumValue()) {
            return BLANK;
        } else if (percentage < SILVER.getMinimumValue()) {
            return BRONZE;
        } else if (percentage < GOLD.getMinimumValue()) {
            return SILVER;
        } else {
            return GOLD;
        }
    }

    @Override
    public String toString() {
        return stringValue;
    }

}
