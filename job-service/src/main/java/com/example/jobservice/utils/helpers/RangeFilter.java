package com.example.jobservice.utils.helpers;

public class RangeFilter {
    public static boolean matchesFilter(String dbValue, String filterValue) {
        if (filterValue == null || filterValue.isEmpty()) {
            return true;
        }

        return matchesRange(dbValue, filterValue);
    }

    private static boolean matchesRange(String dbValue, String filterValue) {
        if (filterValue.startsWith(">=")) {
            int filterMin = Integer.parseInt(filterValue.substring(2));
            return checkMinimumMatch(dbValue, filterMin);
        } else if (filterValue.startsWith("<=")) {
            int filterMax = Integer.parseInt(filterValue.substring(2));
            return checkMaximumMatch(dbValue, filterMax);
        } else if (filterValue.contains("-")) {
            String[] parts = filterValue.split("-");
            int filterMin = Integer.parseInt(parts[0]);
            int filterMax = Integer.parseInt(parts[1]);
            return checkRangeMatch(dbValue, filterMin, filterMax);
        } else {
            try {
                int filterExact = Integer.parseInt(filterValue);
                return checkExactMatch(dbValue, filterExact);
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private static boolean checkMinimumMatch(String dbValue, int filterMin) {
        if (dbValue.startsWith(">=")) {
            int dbMin = Integer.parseInt(dbValue.substring(2));
            return dbMin >= filterMin;
        } else if (dbValue.startsWith("<=")) {
            int dbMax = Integer.parseInt(dbValue.substring(2));
            return dbMax >= filterMin;
        } else if (dbValue.contains("-")) {
            String[] parts = dbValue.split("-");
            int dbMax = Integer.parseInt(parts[1]);
            return dbMax >= filterMin;
        } else {
            try {
                int dbExact = Integer.parseInt(dbValue);
                return dbExact >= filterMin;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private static boolean checkMaximumMatch(String dbValue, int filterMax) {
        if (dbValue.startsWith(">=")) {
            int dbMin = Integer.parseInt(dbValue.substring(2));
            return dbMin <= filterMax;
        } else if (dbValue.startsWith("<=")) {
            int dbMax = Integer.parseInt(dbValue.substring(2));
            return dbMax <= filterMax;
        } else if (dbValue.contains("-")) {
            String[] parts = dbValue.split("-");
            int dbMin = Integer.parseInt(parts[0]);
            return dbMin <= filterMax;
        } else {
            // Exact number in database
            try {
                int dbExact = Integer.parseInt(dbValue);
                return dbExact <= filterMax;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private static boolean checkRangeMatch(String dbValue, int filterMin, int filterMax) {
        if (dbValue.startsWith(">=")) {
            int dbMin = Integer.parseInt(dbValue.substring(2));
            return dbMin >= filterMin && dbMin <= filterMax;
        } else if (dbValue.startsWith("<=")) {
            int dbMax = Integer.parseInt(dbValue.substring(2));
            return dbMax >= filterMin && dbMax <= filterMax;
        } else if (dbValue.contains("-")) {
            String[] parts = dbValue.split("-");
            int dbMin = Integer.parseInt(parts[0]);
            int dbMax = Integer.parseInt(parts[1]);
            return !(dbMax < filterMin || dbMin > filterMax);
        } else {
            try {
                int dbExact = Integer.parseInt(dbValue);
                return dbExact >= filterMin && dbExact <= filterMax;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    private static boolean checkExactMatch(String dbValue, int filterExact) {
        if (dbValue.startsWith(">=")) {
            int dbMin = Integer.parseInt(dbValue.substring(2));
            return filterExact >= dbMin;
        } else if (dbValue.startsWith("<=")) {
            int dbMax = Integer.parseInt(dbValue.substring(2));
            return filterExact <= dbMax;
        } else if (dbValue.contains("-")) {
            String[] parts = dbValue.split("-");
            int dbMin = Integer.parseInt(parts[0]);
            int dbMax = Integer.parseInt(parts[1]);
            return filterExact >= dbMin && filterExact <= dbMax;
        } else {
            // Both are exact numbers
            try {
                int dbExact = Integer.parseInt(dbValue);
                return dbExact == filterExact;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}
