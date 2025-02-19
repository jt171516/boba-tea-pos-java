/*
2. Peak Sales Day
*/
SELECT SUM(totalprice)
FROM(
SELECT totalprice
FROM orders
WHERE date(timestamp) = :'day'
ORDER BY totalprice DESC
LIMIT 10
) AS totalDaySales;
