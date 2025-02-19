/*
Popular Drinks By Week
*/

SELECT name, COUNT(name) AS count, EXTRACT(WEEK FROM timestamp) AS week 
FROM orders
WHERE EXTRACT(WEEK FROM timestamp) = :'week'
GROUP BY name, week
ORDER BY count DESC
LIMIT 3;