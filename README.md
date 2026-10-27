# Smart Pantry Manager

A Java Android app that cuts food waste by suggesting recipes you can cook **strictly** from the ingredients already in your pantry. A recipe is only suggested when every ingredient is present in at least the required quantity - no shopping trip needed.

## Features
- Pantry CRUD: add, edit, delete ingredients (name, quantity, unit, optional expiry date) with input validation
- 21 recipes pre-loaded into the database on first run
- **Suggested Recipes** screen using the strict-matching rule (`RecipeMatcher.java`)
  - tolerant of plurals/case/filler words ("Tomatoes" = "tomato") and unit differences (1 kg flour satisfies 500 g)
  - friendly message when nothing matches
  - optional, clearly separated "Almost There" list (missing exactly one ingredient) - toggle in Settings
- Recipe detail screen, Settings screen (expiry alerts, Almost There), toolbar menu navigation
- No maps, GPS or location features

## Database choice: Firebase Cloud Firestore
Firestore stores the pantry and the recipe collection in the cloud, with an automatic on-device cache, so data persists across app restarts, works offline, and syncs. Snapshot listeners keep the pantry and suggestion screens live, and there is no backend server to build or host.

Collections:
- `pantry` - `name`, `quantity`, `unit`, `expiry` (one document per ingredient)
- `recipes` - `name`, `steps`, `ingredients` (list of `"name|quantity|unit"` strings); seeded on first launch

## Setup / run
1. Install Android Studio (Koala or newer; bundled JDK 17).
2. In the Firebase console (project `smart-pantry-110c4`): **Project settings > Your apps > Add app > Android**, package name `com.smartpantry`. Download `google-services.json` and put it in `SmartPantryManager/app/`.
3. In **Build > Firestore Database**, make sure a database exists and the rules allow access (for development: test mode, `allow read, write: if true;`).
4. `File > Open` the `SmartPantryManager` folder and let Gradle sync.
5. Start an emulator (API 24+) or connect a device, and press **Run**. The recipes are written to Firestore on first launch.

## Project layout
`app/src/main/java/com/smartpantry/`
- `MainActivity`, `AddEditItemActivity`, `SuggestedRecipesActivity`, `RecipeDetailActivity`, `SettingsActivity`
- `PantryAdapter`, `RecipeAdapter` (RecyclerView adapters)
- `PantryRepository` (Firestore), `RecipeData` (seed data), `RecipeMatcher`, `Units` (matching logic)
