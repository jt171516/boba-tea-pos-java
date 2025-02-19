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