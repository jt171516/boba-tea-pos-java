import csv
import random
from datetime import datetime, timedelta

def generate_sharetea_data():
    """
    Generate ~365 days of orders (52 weeks).
    The daily order counts and item prices/quantities
    are set such that we roughly hit ~$1M total 
    without needing to scale individual prices.
    All totals remain as integers.
    """
    DAYS = 52 * 7 + 1  # 365
    START_DATE = datetime(2024, 2, 16)
    
    # Two "peak" day indices (0-based from the start date)
    PEAK_DAY_1 = 30
    PEAK_DAY_2 = 240
    
    # 20 items: (id, calories, integer base_price, ..., name)
    items = [
        (1, 186, 3, 0, "Classic Black Tea"),
        (2, 186, 3, 0, "Classic Green Tea"),
        (3, 186, 3, 0, "Classic Oolong Tea"),
        (4, 288, 3, 0, "Wintermelon Tea"),
        (5, 246, 3, 0, "Honey Tea (Black Tea)"),
        (6, 246, 3, 0, "Honey Tea (Green Tea)"),
        (7, 246, 3, 0, "Honey Tea (Oolong Tea)"),
        (8, 188, 3, 0, "Ginger Tea"),
        (9, 469, 4, 0, "Classic Milk Tea (Black Tea)"),
        (10, 453, 4, 0, "Classic Milk Tea (Green Tea)"),
        (11, 453, 4, 0, "Classic Milk Tea (Oolong Tea)"),
        (12, 476, 4, 0, "Honey Milk Tea (Black Tea)"),
        (13, 476, 4, 0, "Honey Milk Tea (Green Tea)"),
        (14, 476, 4, 0, "Honey Milk Tea (Oolong Tea)"),
        (15, 479, 4, 0, "Classic Coffee"),
        (16, 410, 4, 0, "Ginger Milk Tea"),
        (17, 426, 4, 0, "Coffee Milk Tea"),
        (18, 657, 5, 0, "Classic Pearl Milk Tea (Black)"),
        (19, 646, 5, 0, "Classic Pearl Milk Tea (Green Tea)"),
        (20, 646, 5, 0, "Classic Pearl Milk Tea (Oolong Tea)"),
    ]
    
    all_orders = []
    current_order_id = 1
    
    for day_index in range(DAYS):
        # Peak days: big jump
        if day_index == PEAK_DAY_1 or day_index == PEAK_DAY_2:
            daily_order_count = random.randint(700, 900)
        else:
            # Normal days
            daily_order_count = random.randint(250, 400)
        
        for _ in range(daily_order_count):
            # Random time of day
            hour = random.randint(0, 23)
            minute = random.randint(0, 59)
            second = random.randint(0, 59)
            order_datetime = (
                (START_DATE + timedelta(days=day_index))
                .replace(hour=hour, minute=minute, second=second)
            )
            
            # Randomly choose an item
            item_id, calories, base_price, sales, item_name = random.choice(items)
            
            # 85% chance small order (1–2), 15% chance large group (3–10)
            if random.random() < 0.85:
                quantity = random.randint(1, 2)
            else:
                quantity = random.randint(3, 10)
            
            order_total = base_price * quantity  # integer
            
            all_orders.append({
                "id": current_order_id,
                "name": item_name,
                "totalprice": order_total,
                "date": order_datetime
            })
            
            current_order_id += 1
    
    return all_orders

def write_csv_file(filename, orders):
    """
    Write orders to CSV: id, name, totalprice, date
    """
    with open(filename, mode='w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(["id", "name", "totalprice", "date"])
        for o in orders:
            writer.writerow([
                o["id"],
                o["name"],
                o["totalprice"],
                o["date"].strftime("%Y-%m-%d %H:%M:%S")
            ])

def main():
    orders = generate_sharetea_data()
    write_csv_file("orders.csv", orders)

if __name__ == "__main__":
    main()