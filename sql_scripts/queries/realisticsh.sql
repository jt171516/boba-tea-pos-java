/*
Realistic Sales History
*/
SELECT EXTRACT(HOUR FROM timestamp) AS hourOfDay,
	  COUNT(totalprice) AS numSales,
        SUM(totalprice) AS totalSales
FROM orders
WHERE EXTRACT(HOUR FROM timestamp) = :'hour'
GROUP BY EXTRACT(HOUR FROM timestamp);
