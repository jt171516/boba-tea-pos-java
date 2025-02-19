/*
Peak Hour
*/
WITH HourlySales AS (
    SELECT 
        EXTRACT(DOW FROM timestamp) AS dayOfWeek, 
        TO_CHAR(timestamp, 'FMDay') AS dayName,
        EXTRACT(HOUR FROM timestamp) AS hourOfDay,
        SUM(totalprice) AS totalSales
    FROM orders
    GROUP BY dayOfWeek, dayName, hourOfDay
)
SELECT dayOfWeek, dayName, hourOfDay, totalSales
FROM HourlySales
WHERE (dayOfWeek, totalSales) IN (
    SELECT dayOfWeek, MAX(totalSales)
    FROM HourlySales
    GROUP BY dayOfWeek
)
ORDER BY dayOfWeek, hourOfDay;
