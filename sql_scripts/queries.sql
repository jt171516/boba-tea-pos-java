
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

/*
3. Menu Item Inventory
*/
SELECT
    i.name,
    COUNT(item_inv.inventoryid) AS "inventory item count"
FROM
	item i
JOIN
    iteminventoryjunction item_inv ON i.id = item_inv.itemid
WHERE
	i.name = :'itemName'
GROUP BY
    i.name;

/*
4. Best of the Worst
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

/*
5. Low Inventory Level
*/
SELECT 
    (id,
    name,
    qty) AS "Low Inventory Items"
FROM
    inventory
WHERE
    qty < :'reorderLevel';

/*
6. Retrieve All Items and their Prices
*/
SELECT
		id,
		name,
		price
FROM
	item;

/*
Total Average Order Value
*/
SELECT 
	AVG(totalPrice) AS “Total Average Order Value”
FROM
	orders;

/*
Most Popular Item
*/
SELECT name, COUNT(name) as count FROM orders
GROUP BY name
ORDER BY count DESC 
LIMIT 1;


SELECT 
    i.id,
    i.name,
    COUNT(*) AS total_quantity_used
FROM 
    orders o
JOIN 
    ordersitemjunction oi ON o.id = oi.orderid
JOIN
    iteminventoryjunction ii ON oi.itemid = ii.itemid
JOIN 
    inventory i ON ii.inventoryid = i.id
GROUP BY
    i.id,
    i.name
ORDER BY
    total_quantity_used DESC;





