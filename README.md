# Smart Pantry Manager

A Java Android application that reduces household food waste by tracking pantry ingredients and suggesting recipes only when the user already has every required item (strict matching).

## Technical overview
- **Language:** Java (no Kotlin)
- **Storage:** SQLite with `SQLiteOpenHelper` — full Create, Read, Update, Delete for pantry items; data persists after the app is closed
- **Matching:** Strict ingredient matching (name + sufficient quantity)
- **UI:** Multiple Activities, RecyclerView adapters, input validation, bottom navigation

## Features
1. Add, edit and delete pantry ingredients (name, quantity, unit, optional expiry)
2. Suggested recipes filtered by current stock
3. Recipe detail (ingredients and method)
4. Settings preferences (SharedPreferences)
5. Empty-state messages when no items or no matching recipes

## How to run
1. Open the `SmartPantryManager` folder in Android Studio.
2. Use Gradle JVM 17 or 21 if prompted.
3. Create an Android App run configuration (module: `app`).
4. Run on a local emulator or device.

## Strict matching example
Add `egg` (2 pcs), `oil` (1 tbsp), `salt` (1 tsp) → **Herb Scrambled Eggs** appears.  
Reduce salt quantity below the recipe requirement, or delete salt → the recipe is removed from suggestions.

## Design
UI theme: warm kitchen palette (cream, cocoa, gold).  
Designed by Emily Mamabolo.
