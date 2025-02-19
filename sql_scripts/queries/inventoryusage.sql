/*
Inventory Usage Report
*/
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