/*
Most Popular Item
*/
SELECT name, COUNT(name) as count FROM orders
GROUP BY name
ORDER BY count DESC 
LIMIT 1;