inventory_items = [
  "cups",
  "lids",
  "straws",
  "napkins",
  "creamer",
  "milk",
  "sugar",
  "black tea",
  "green tea",
  "oolong tea",
  "honey",
  "wintermelon",
  "ginger",
  "powdered coffee",
  "pearls",
  "mini pearls",
  "pudding",
  "herb jelly",
  "aiyu jelly",
  "lychee jelly",
  "aloe vera",
  "red bean",
  "cystal boba",
  "ice cream",
  "creama"
]

newID = 1

for item in inventory_items:
    print(f"INSERT INTO inventory (id, name, qty)")
    print(f"VALUES ({newID}, '{item}', 1);")
    print()

    newID += 1