CREATE TABLE itemInventoryJunction (
    itemID INT,
    inventoryID INT,
    FOREIGN KEY (itemID) REFERENCES item(id),
    FOREIGN KEY (inventoryID) REFERENCES inventory(id),
    PRIMARY KEY (itemID, inventoryID)
);
