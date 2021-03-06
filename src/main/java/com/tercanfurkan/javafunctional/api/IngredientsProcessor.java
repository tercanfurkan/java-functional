package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.tercanfurkan.javafunctional.model.Ingredient;
import com.tercanfurkan.javafunctional.provider.DataProvider;
import com.tercanfurkan.javafunctional.model.Forecast;
import com.tercanfurkan.javafunctional.model.IngredientTree;

import static com.tercanfurkan.javafunctional.model.IngredientTree.initBillTreeRoot;


public class IngredientsProcessor implements DeconstructionInterface, AvailabilityInterface, ForecastInterface {
    
	private final IngredientTree rootTree;
	
    public IngredientsProcessor(List<Ingredient> bills) {
    	this.rootTree = Utils.parseBillTree(bills);
    }
    
	/**
	 * Recursively streams code of the product and its parent
	 * <p>
	 * example: 
	 * <br/>EGG -> {MAYO, FRIED_EGGS}
	 * <br/>MAYO -> CLUB_SANDWICH
	 * <br/>FRIED_EGGS -> {}
	 */
    private Stream<String> streamProductAndAncestors(String productCode) {
    	Set<IngredientTree> ingredientTreeSet = rootTree.getIngredientTreeStream(productCode).collect(Collectors.toSet());
    	if (ingredientTreeSet.isEmpty())
    		return Stream.of(productCode);
    	else {
    		return Stream.concat(
    				Stream.of(productCode),
					ingredientTreeSet.stream().flatMap(t -> streamProductAndAncestors(t.getIngredient().code)));
		}
	}

    @Override
    public Set<String> getNamesForProducts(String productCode) {
    	return streamProductAndAncestors(productCode)
    			.filter(product -> !product.equals(productCode))
    			.collect(Collectors.toSet());
    }

    @Override
    public Set<String> getNamesForIngredients(String productCode) {
    	return rootTree.getProductTreeStream(productCode)
    			.flatMap(IngredientTree::streamChildren)
    			.map(children -> children.getIngredient().ingredientCode)
    			.collect(Collectors.toSet());
    }

    @Override
    public List<Map<String, Object>> getIngredients(String productCode) {
    	return rootTree.getProductTreeStream(productCode)
        		.flatMap(tree -> tree.assignDepth(IngredientTree.INITIAL_DEPTH))
        		.sorted(Comparator.comparingInt(IngredientTree::getDepth))
        		.map(child -> {
        			Map<String, Object> childAsMap = new HashMap<>();
        			childAsMap.put("name", child.getIngredient().getIngredientCode());
        			childAsMap.put("depth", child.getDepth());
        			childAsMap.put("multiplier", (int) child.getIngredient().multiplier);
        			return childAsMap;
        		})
        		.collect(Collectors.toList());
     }

	@Override
	public List<Map<String, Object>> getIngredients(String productCode, LocalDate date) {
		Stream<Map<String, Object>> activeAndInactiveIngredientsOfProductForDate = rootTree.getProductTreeStream(productCode)
	    		.flatMap(tree -> tree.streamChildren(IngredientTree.INITIAL_DEPTH, date));
		Stream<List<Map<String, Object>>> groupedIngredientsOfProduct = Utils.groupByIngredientName(activeAndInactiveIngredientsOfProductForDate);
		return Utils.mapReduceToSelectedMultiplier(groupedIngredientsOfProduct)
		.sorted(Comparator.comparingInt(child -> (int) child.get("depth")))
		.collect(Collectors.toList());
	}

	@Override
	public List<Map<String, Object>> getForecastsFor(LocalDate date) {
		List<Forecast> forecasts = DataProvider.getForecasts();
		Stream<Map<String, Object>> productLeafIngredientsOfForecasts = forecasts.parallelStream()
				.filter(forecast -> date.isEqual(forecast.date))
				.flatMap(forecast -> {
					Stream<Map<String, Object>> activeAndInactiveIngredientsOfProductForDate = rootTree.getProductTreeStream(forecast.productCode)
							.flatMap(ingredientTree -> ingredientTree.streamLeafs(date, forecast.quantity));
					Stream<List<Map<String, Object>>> groupedIngredientsOfProduct = Utils.groupByIngredientName(activeAndInactiveIngredientsOfProductForDate);
					return Utils.mapReduceToSelectedMultiplier(groupedIngredientsOfProduct);
				});
		Stream<List<Map<String, Object>>> groupedLeafIngredientsOfForecasts = Utils.groupByIngredientName(productLeafIngredientsOfForecasts);
		return Utils.mapAndReduceToMultiplierSum(groupedLeafIngredientsOfForecasts)
		.filter(map -> (double) map.get("multiplier") != 0) // extract inactive bills
		.collect(Collectors.toList());
	}
	
	/**
	 * Utility methods for {@link IngredientsProcessor}
	 *
	 */
	public static class Utils {
	    /**
	     * @param bills the list of entries to be processed for each exercise
	     * @return the list of bills in a tree structure
	     */
	    private static IngredientTree parseBillTree(List<Ingredient> bills) {
	    	IngredientTree root = initBillTreeRoot();
	    	bills.forEach(root::add);
	    	
	    	//adopt missing children after first parsing round
	    	root.streamChildren()
	    		.forEach(child -> {
	    			Stream<IngredientTree> prospectChildrenOfChild = root.getProductTreeStream(child.getIngredient().getIngredientCode());
	    			prospectChildrenOfChild.forEach(child::addChild);
	    		});
	    	return root;
	    }
		
		/**
		 * Groups the map by ingredientName and a stream
		 * 
		 * @return stream of a collection which contains grouped map values
		 */
		private static Stream<List<Map<String, Object>>> groupByIngredientName(Stream<Map<String, Object>> stream) {
			return stream.collect(Collectors.groupingBy(m -> m.get("name")))
			.values()
			.stream();
		}

		/**
		 * Maps and reduces a stream of bills for a ingredient (list<#map>) into a stream of one bill data (map) with the selected multiplier (active or inactive)
		 * 
		 * @param stream the list<#map>> stream which contains all bills for a ingredient - active and inactive
		 * @return mapped and reduced stream of map which contain only one bill data (name, date/depth, multiplier) with the selected multiplier (active or inactive)
		 */
		private static Stream<Map<String, Object>> mapReduceToSelectedMultiplier(Stream<List<Map<String, Object>>> stream) {
			return stream.map(list -> list.stream()
							.reduce((i1, i2) -> (double) i1.get("multiplier") == 0 ? i1 : i2)
							.orElse(list.get(0)));
		}

		/**
		 * Maps and reduces a stream of bills for a ingredient (list<#map>) into a stream of one bill data (map) with the sum of all bill multipliers
		 * 
		 * @param stream the list<#map>> stream which contains all bills for a ingredient - active and inactive
		 * @return mapped and reduced stream of map which contain only one bill data (map containing; name, date, multiplier) the sum of all bill multipliers
		 */
		private static Stream<Map<String, Object>> mapAndReduceToMultiplierSum(Stream<List<Map<String, Object>>> stream) {
			return stream.map(list -> list.stream()
							.reduce((i1, i2) -> {
								i1.put("multiplier", (double) i1.get("multiplier") + (double) i2.get("multiplier"));
								return i1;
							})
							.orElse(list.get(0)));
		}
	}
}
