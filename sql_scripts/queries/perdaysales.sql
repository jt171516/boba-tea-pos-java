/*
Per Day Sales, High to Low
*/
SELECT DATE(timestamp) AS date,
SUM(totalprice) AS daysales 
FROM orders 
GROUP BY date 
ORDER BY daysales DESC;
