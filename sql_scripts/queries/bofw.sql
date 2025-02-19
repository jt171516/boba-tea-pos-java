/*
Best of the Worst
*/
SELECT 
    DATE(timestamp) AS lowSaleDate, 
    SUM(totalprice) AS totalDaySales,
    (SELECT name 
     FROM orders 
     WHERE DATE(timestamp) = (
         SELECT DATE(timestamp) 
         FROM orders 
         WHERE EXTRACT(WEEK FROM timestamp) = :'weekToCheck'
         GROUP BY DATE(timestamp)
         ORDER BY SUM(totalprice) ASC
         LIMIT 1
     )
     GROUP BY name
     ORDER BY SUM(totalprice) DESC
     LIMIT 1
    ) AS bestselling

FROM orders
WHERE DATE(timestamp) = (
    SELECT DATE(timestamp) 
    FROM orders 
    WHERE EXTRACT(WEEK FROM timestamp) = :'weekToCheck'
    GROUP BY DATE(timestamp)
    ORDER BY SUM(totalprice) ASC
    LIMIT 1
)
GROUP BY lowSaleDate
ORDER BY totalDaySales ASC
LIMIT 1;