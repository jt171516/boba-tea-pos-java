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