for items in range(20):
    for inventory in range(4):
      print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
      print(f"VALUES ({items+1}, {inventory+1});")
      print()

creamerItems = [9, 10, 11, 12, 13, 14, 16, 17, 18, 19, 20]

for items in creamerItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 5);")
    print()

for items in range(20):
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items+1}, 7);")
    print()

blackTeaItems = [1,5,9,12,18]

for items in blackTeaItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 8);")
    print()


greenTeaItems = [2,6,10,13,19]

for items in greenTeaItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 9);")
    print()


oolongTeaItems = [3,7,11,14,20]

for items in oolongTeaItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 10);")
    print()


honeyItems = [5,6,7,12,13,14]

for items in honeyItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 11);")
    print()


wintermelonItems = [4]

for items in wintermelonItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 12);")
    print()


gingerItems = [8,16]

for items in gingerItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 13);")
    print()


powderedCoffeeItems = [15,17]

for items in powderedCoffeeItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 14);")
    print()


hotWaterItems = [15]

for items in hotWaterItems:
    print(f"INSERT INTO itemInventoryJunction (itemID, inventoryID)")
    print(f"VALUES ({items}, 26);")
    print()