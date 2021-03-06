# Java Streams and Functional Programming Exercise

This java functional programming exercise implements a food ingredients API. 

An ingredient list is a list of product-ingredient relations, with a multiplier describing how many units of each ingredient is needed for making the product. A good example for an ingredient list is a sandwich. A sandwich is made from multiple ingredients (bread, mayonnaise, eggs, various cold cuts, etc.) which may themselves be intermediate products that are made of other intermediate ingredients (such as mayonnaise) or basic ingredients that do not have further ingredients (e.g. oil, yeast).

Ingredient list is stored in a tree structure, where the top-level product (such as sandwich or fried eggs) is the root and ingredients are either intermediate items (like mayo) or leaves (such as yeast). An item can only be once in the tree. For example imaginary product SALT can not be both child of BREAD and MAYO. However, an ingredient can be used several times in different trees: it's valid to have EGG as child of fried eggs and sandwich.  

All ingredient lists are supplied in a CSV file.

## Interfaces
### #1 Ingredients Deconstruction Interface
Given an input with the following structure:

code|ingredient_code|start_date|end_date|multiplier
--- | --- | --- | --- | ---
CLUB_SANDWICH|BREAD|||1
BREAD|FLOUR|||1
BREAD|YEAST|||1
CLUB_SANDWICH|MAYO|||1
MAYO|EGG|||1
EGG|EGG_WHITE|||1
EGG|EGG_YOLK|||1
MAYO|OIL|||1
MAYO|MUSTARD|||1
MUSTARD|GARLIC_POWDER|||1
GARLIC_POWDER|GARLIC|||1
CLUB_SANDWICH|BACON|||1
FRIED_EGGS|EGG|||1
FRIED_EGGS|OIL|||1

#### Methods:
It constructs a tree structure that implements the given methods:

`Set<String> getNamesForProducts(String productCode)`

For a given product code, anywhere in the tree, it returns the values of ancestor products in an unordered collection, like Set.

`Set<String> getNamesForIngredients(String productCode)`

For a given product code, anywhere in the tree, it returns the values of child products in an unordered collection, like Set.

`List<Map> getIngredients(String productCode)`

For a given product code, anywhere in the tree, it returns every ingredient (children) for all levels. Returned value is an ordered list of key-value pairs (Maps) containing following information: product code, depth in the current tree, multiplier.

#### Sample Output:
```
getNamesForProducts("GARLIC_POWDER") => Set("MUSTARD", "MAYO", "CLUB_SANDWICH")
getNamesForIngredients("CLUB_SANDWICH") => Set("BREAD", "MAYO", "BACON", "FLOUR", "YEAST", "EGG", "EGG_WHITE", "EGG_YOLK" "OIL", "MUSTARD", "GARLIC_POWDER", "GARLIC")

getIngredients("CLUB_SANDWICH") => [
    {code: "BREAD", depth: 1, multiplier 1},
    {code: "FLOUR", depth: 2, multiplier 1},
    {code: "YEAST", depth: 2, multiplier 1},
    {code: "MAYO", depth: 1, multiplier 1},
    {code: "EGG", depth: 2, multiplier 1},
    {code: "EGG_WHITE", depth: 3, multiplier 1},
    {code: "EGG_YOLK", depth: 3, multiplier 1},
    {code: "OIL", depth: 2, multiplier 1},
    {code: "MUSTARD", depth: 2, multiplier 1},
    {code: "GARLIC_POWDER", depth: 4, multiplier 1},
    {code: "GARLIC", depth: 3, multiplier 1},
    {code: "BACON", depth: 1, multiplier 1},
]

getIngredients("GARLIC_POWDER") => [
    {code: "GARLIC", depth: 1, multiplier 1},
]

getIngredients("GARLIC") => []

getIngredients("FRIED_EGGS") = [
    {code: "EGG", depth: 2, multiplier 1},
    {code: "EGG_WHITE", depth: 3, multiplier 1},
    {code: "EGG_YOLK", depth: 3, multiplier 1},
    {code: "OIL", depth: 2, multiplier 1}
]
```

### #2 Ingredient Availability Interface
Ingredients may have a start date and an end date defined. When an ingredient has a start and end date defined and its multiplier set to zero, it’s considered inactive for that period. If an ingredient has only start date defined it means that it's active from that date to eternity. In similar fashion having only end date defined means that the ingredient is active from beginning of time up to the given date.

It's possible to substitute an inactive ingredient with other ingredients. Thus there may be other, similar ingredients listed as active during that time. If you have a an ingredient replacing another for any time range (e.g. HYPER_YEAST replaces SUPER_YEAST on 2016-12-24) you also need to explicitly set the replaced ingredient inactive (zero multiplier) for the given period or otherwise the both ingredients would be considered active for the time range. 

Active/inactive ingredients appear in the data as follows:

code|ingredient_code|start_date|end_date|multiplier
--- | --- | --- | --- | ---
CLUB_SANDWICH|BREAD|||1
BREAD|FLOUR|||1
BREAD|YEAST|||1
BREAD|YEAST|2016-12-10|2016-12-26|0
BREAD|SUPER_YEAST|2016-12-10|2016-12-26|1
BREAD|SUPER_YEAST|2016-12-24|2016-12-24|0
BREAD|HYPER_YEAST|2016-12-24|2016-12-24|1
CLUB_SANDWICH|MAYO|||1
MAYO|EGG|||1
MAYO|EGG|2016-12-06|2017-01-01|0
MAYO|EGGS_ORGANIC|2016-12-06|2017-01-01|1
MAYO|OIL|||1
MAYO|DIJON_MUSTARD||2016-11-20|1
MAYO|FRENCHS_MUSTARD|2016-11-21||1
MUSTARD|GARLIC_POWDER|||1
GARLIC_POWDER|GARLIC|||1
CLUB_SANDWICH|BACON|||1
CLUB_SANDWICH|BACON|2016-12-31|2016-12-31|0
CLUB_SANDWICH|FAKE_BACON|2016-12-31|2016-12-31|1
FAKE_BACON|HAM|||1
FAKE_BACON|BACON_SPICE|||1

#### Methods:
This overloads the getIngredients interface to support date-specific queries for ingredients as follows: 

`List<Map> getIngredients(String productCode, LocalDate date)` 

For a given product code and date, anywhere in the tree, it returns every ingredient (children) for all levels including or excluding ingredients that are active and inactive for the given date. As before, the returned value is an ordered list of maps.

#### Sample Output:
```
getIngredients("BREAD", "2000-01-01") => [
    { code: "FLOUR", depth: 1, multiplier: 1 },
    { code: "YEAST", depth: 1, multiplier: 1 }
]

getIngredients("BREAD", "2016-12-24") => [
    { code: "FLOUR", depth: 1, multiplier: 1 },
    { code: "YEAST", depth: 1, multiplier: 0 },
    { code: "SUPER_YEAST", depth: 1, multiplier: 0 },
    { code: "HYPER_YEAST", depth: 1, multiplier: 1 }
]

getIngredients("BREAD", "2016-12-25") => [
    { code: "FLOUR", depth: 1, multiplier: 1 },
    { code: "YEAST", depth: 1, multiplier: 0 },
    { code: "SUPER_YEAST", depth: 1, multiplier: 1 }
]
```

### #3 Ingredients Forecasting Interface
Ordering basic ingredients is based on forecasts. Forecasts are supplied as a separate CSV, with a separate ingredient for each sandwich type and date. The data is provided by `DataProvider`.

code|ingredient_code|start_date|end_date|multiplier
--- | --- | --- | --- | ---
CLUB_SANDWICH|BREAD|||1
BREAD|FLOUR|||2
BREAD|YEAST|||1
BREAD|YEAST|2016-12-10|2016-12-26|0
BREAD|SUPER_YEAST|2016-12-10|2016-12-26|1
BREAD|SUPER_YEAST|2016-12-24|2016-12-24|0
BREAD|HYPER_YEAST|2016-12-24|2016-12-24|0.1
CLUB_SANDWICH|MAYO|||3
MAYO|EGG|||2
EGG|EGG_WHITE|||1
EGG|EGG_YOLK|||1
MAYO|OIL|||2.5
MAYO|MUSTARD|||1
MUSTARD|GARLIC_POWDER|||0
MUSTARD|MUSTARD_SEEDS|||5
GARLIC_POWDER|GARLIC|||1
CLUB_SANDWICH|BACON|||4
FRIED_EGGS|EGG|||1
FRIED_EGGS|OIL|||1

code|forecast|date
--- | --- | ---
CLUB_SANDWICH|2.0|2017-01-01
FRIED_EGGS|5.0|2017-01-01
CLUB_SANDWICH|4.0|2016-12-24

#### Methods:
`getForecastsFor()` 

Calculates the need for specific basic ingredients for the given date. The output is the orders sent to suppliers for restocking the inventory for the given date. In reality there can be millions of forecasts for different products in different dates so performance can be considered in the future as well.

`List<Map> getForecastsFor(LocalDate date)` 

For the given date, it calculates the demand of basic ingredients (i.e. tree leaves) for all active products. Returned value is a list of maps containing the ingredient code (String), amount to order (double) and date (LocalDate).

#### Sample Output:
```
getForecastsFor("2017-01-01") => [
    { code: "FLOUR", amount: 2.0 * 2, date: "2017-01-01" },
    { code: "YEAST", amount: 2.0 * 1.0, date: "2017-01-01" },
    { code: "EGG_WHITE", amount: (2.0 * 3 * 2 * 1) + (5.0 * 1), date: "2017-01-01" },
    { code: "EGG_YOLK", amount: (2.0 * 3 * 2 * 1) + (5.0 * 1), date: "2017-01-01" },
    { code: "OIL", amount: (2.0 * 3 * 2.5) + (5.0 * 1), date: "2017-01-01" },
    { code: "MUSTARD_SEEDS", amount: 2.0 * 3 * 1 * 5, date: "2017-01-01" },
    { code: "BACON", amount: 2.0 * 4, date: "2017-01-01" }
]

getForecastsFor("2016-12-24") => [
    { code: "FLOUR", amount: 2.0 * 2, date: "2016-12-24" },
    { code: "HYPER_YEAST", amount: 4.0 * 0.1, date: "2016-12-24" },
    { code: "EGG_WHITE", amount: 2.0 * 3 * 2 * 1, date: "2016-12-24" },
    { code: "EGG_YOLK", amount: 2.0 * 3 * 2 * 1, date: "2016-12-24" },
    { code: "OIL", amount: 4.0 * 3 * 2.5, date: "2016-12-24" },
    { code: "MUSTARD_SEEDS", amount: 4.0 * 3 * 1 * 5, date: "2016-12-24" },
    { code: "BACON", amount: 4.0 * 4, date: "2016-12-24" }
]
```
