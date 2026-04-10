package com.tendanz.pricing.enums;

/**
 * Enum representing age categories for pricing purposes.
 */
public enum AgeCategory {
    YOUNG(18, 24),
    ADULT(25, 45),
    SENIOR(46, 65),
    ELDERLY(66, 99);

    private final int minAge;
    private final int maxAge;

    AgeCategory(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    public int getMinAge() {
        return minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    /**
     * Determine the age category for a given age.
     *
     * @param age the age to categorize
     * @return the corresponding AgeCategory
     */
    public static AgeCategory fromAge(int age) {
        for (AgeCategory category : AgeCategory.values()) {
            if (age >= category.minAge && age <= category.maxAge) {
                return category;
            }
        }
        throw new IllegalArgumentException("Age " + age + " does not fit any category");
    }
}
