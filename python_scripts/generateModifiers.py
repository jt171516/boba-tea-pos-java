toppings = [
    "Pearl",
    "Mini Pearl",
    "Pudding",
    "Herb Jelly",
    "Aiyu Jelly",
    "Lychee Jelly",
    "Aloe Vera",
    "Red Bean",
    "Crystal Boba",
    "Ice Cream",
    "Creama"
]
#iceLevels = ["No Ice", "Less Ice", "Normal Ice", "Extra Ice"]
levels = [0, 25, 50, 75, 100]
sizes = ["S", "M", "L"]

newID = 1

for topping in toppings:
    for ice in levels:
        for sweet in levels:
            for size in sizes:
                price = 1.00
                print(f"INSERT INTO modifier (id, ice_level, sweetness_level, topping, price, size)")
                print(f"VALUES ({newID}, '{ice}', '{sweet}', '{topping}', {price}, '{size}');")
                print()

                newID += 1
