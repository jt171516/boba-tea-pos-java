CREATE TABLE ordersItemJunction (
    orderItemID INT PRIMARY KEY,
    orderID INT,
    itemID INT,
    FOREIGN KEY (orderID) REFERENCES orders(id),
    FOREIGN KEY (itemID) REFERENCES item(id)
);

CREATE TABLE ordersItemModifierJunction (
    orderItemID INT,
    modifierID TEXT,
    qty INT,
    FOREIGN KEY (orderItemID) REFERENCES ordersItemJunction(orderItemID),
    FOREIGN KEY (modifierID) REFERENCES modifier(id),
    PRIMARY KEY (orderItemID, modifierID)
);