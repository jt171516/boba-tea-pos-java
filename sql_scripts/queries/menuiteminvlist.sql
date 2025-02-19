/*
Menu Item Inventory Listed
*/
SELECT
    i.name AS "Item Name",
    STRING_AGG (inv.name, ',') AS "Inventory Item Name"
FROM
    item i
JOIN
    iteminventoryjunction item_inv ON i.id = item_inv.itemid
JOIN
    inventory inv ON item_inv.inventoryid = inv.id
WHERE
    i.name = :'itemName'
GROUP BY
    i.name;