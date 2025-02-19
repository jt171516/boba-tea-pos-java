/*
1. Weekly Sales History
*/
SELECT orderCount
FROM(
SELECT COUNT(id) AS orderCount, EXTRACT(WEEK FROM timestamp) AS week
FROM orders
GROUP BY week
)
AS ordersInWeek
WHERE week = :'week';
