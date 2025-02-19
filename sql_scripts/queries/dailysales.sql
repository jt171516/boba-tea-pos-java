/*
Daily Sales
*/
SELECT SUM(totalprice) AS daySales, COUNT(id) AS numSales 
FROM orders 
WHERE DATE(timestamp) = :'date';
