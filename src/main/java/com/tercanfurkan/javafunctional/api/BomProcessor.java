package com.tercanfurkan.javafunctional.api;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.tercanfurkan.javafunctional.provider.DataProvider;
import com.tercanfurkan.javafunctional.model.Entry;
import com.tercanfurkan.javafunctional.model.Forecast;
import com.tercanfurkan.javafunctional.model.BillTree;


public class BomProcessor implements DeconstructionInterface, AvailabilityInterface, ForecastInterface {
    
	private final BillTree rootTree;
	
    public BomProcessor(List<Entry> bills) {
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
    	Set<BillTree> treesOfComponent = rootTree.getTreesOfComponent(productCode).collect(Collectors.toSet()); 
    	if (treesOfComponent.isEmpty())
    		return Stream.of(productCode);
    	else {
    		return Stream.concat(
    				Stream.of(productCode),
    				treesOfComponent.stream().flatMap(t -> streamProductAndAncestors(t.getBill().code)));
		}
	}

    @Override
    public Set<String> getNamesForProducts(String productCode) {
    	return streamProductAndAncestors(productCode)
    			.filter(product -> !product.equals(productCode))
    			.collect(Collectors.toSet());
    }

    @Override
    public Set<String> getNamesForComponents(String productCode) {
    	return rootTree.getTreesOfProduct(productCode)
    			.flatMap(BillTree::streamChildren)
    			.map(children -> children.getBill().componentCode)
    			.collect(Collectors.toSet());
    }

    @Override
    public List<Map<String, Object>> getComponents(String productCode) {
    	return rootTree.getTreesOfProduct(productCode)
        		.flatMap(tree -> tree.assignDepth(BillTree.INITIAL_DEPTH))
        		.sorted(Comparator.comparingInt(BillTree::getDepth))
        		.map(child -> {
        			Map<String, Object> childAsMap = new HashMap<>();
        			childAsMap.put("name", child.getBill().getComponentCode());
        			childAsMap.put("depth", child.getDepth());
        			childAsMap.put("multiplier", (int) child.getBill().multiplier);
        			return childAsMap;
        		})
        		.collect(Collectors.toList());
     }

	@Override
	public List<Map<String, Object>> getComponents(String productCode, LocalDate date) {
		Stream<Map<String, Object>> activeAndInactiveComponentsOfProductForDate = rootTree.getTreesOfProduct(productCode)
	    		.flatMap(tree -> tree.streamChildren(BillTree.INITIAL_DEPTH, date));
		Stream<List<Map<String, Object>>> componentsOfProductGrouped = Utils.groupByComponentName(activeAndInactiveComponentsOfProductForDate);
		return Utils.mapReduceToSelectedMultiplier(componentsOfProductGrouped)
		.sorted(Comparator.comparingInt(child -> (int) child.get("depth")))
		.collect(Collectors.toList());
	}

	@Override
	public List<Map<String, Object>> getForecastsFor(LocalDate date) {
		List<Forecast> forecasts = DataProvider.getForecasts();
		Stream<Map<String, Object>> productLeafComponentsOfForecasts = forecasts.parallelStream()
				.filter(forecast -> date.isEqual(forecast.date))
				.flatMap(forecast -> {
					Stream<Map<String, Object>> activeAndInactiveComponentsOfProductForDate = rootTree.getTreesOfProduct(forecast.productCode)
							.flatMap(billTree -> billTree.streamLeafs(date, forecast.quantity));
					Stream<List<Map<String, Object>>> componentsOfProductGrouped = Utils.groupByComponentName(activeAndInactiveComponentsOfProductForDate);
					return Utils.mapReduceToSelectedMultiplier(componentsOfProductGrouped);
				});
		Stream<List<Map<String, Object>>> leafComponentsOfForecastsGrouped = Utils.groupByComponentName(productLeafComponentsOfForecasts);
		return Utils.mapAndReduceToMultiplierSum(leafComponentsOfForecastsGrouped)
		.filter(map -> (double) map.get("multiplier") != 0) // extract inactive bills
		.collect(Collectors.toList());
	}
	
	/**
	 * Utility methods for {@link BomProcessor}
	 *
	 */
	public static class Utils {
	    /**
	     * @param bills the list of entries to be processed for each exercise
	     * @return the list of bills in a tree structure
	     */
	    private static BillTree parseBillTree(List<Entry> bills) {
	    	// the root tree is useless (no bill, no parent) other than storing all the bills as its children.
	    	BillTree root = new BillTree(null, null);
	    	bills.forEach(root::add);
	    	
	    	//adopt missing children after first parsing round
	    	root.streamChildren()
	    		.forEach(child -> {
	    			Stream<BillTree> prospectChildrenOfChild = root.getTreesOfProduct(child.getBill().getComponentCode());
	    			prospectChildrenOfChild.forEach(c -> {
						child.getChildren().add(c);
	    			});
	    		});
	    	return root;
	    }
		
		/**
		 * Groups the map by componentName and a stream 
		 * 
		 * @return stream of a collection which contains grouped map values
		 */
		private static Stream<List<Map<String, Object>>> groupByComponentName(Stream<Map<String, Object>> stream) {
			return stream.collect(Collectors.groupingBy(m -> m.get("name")))
			.values()
			.stream();
		}

		/**
		 * Maps and reduces a stream of bills for a component (list<#map>) into a stream of one bill data (map) with the selected multiplier (active or inactive)
		 * 
		 * @param stream the list<#map>> stream which contains all bills for a component - active and inactive
		 * @return mapped and reduced stream of map which contain only one bill data (name, date/depth, multiplier) with the selected multiplier (active or inactive)
		 */
		private static Stream<Map<String, Object>> mapReduceToSelectedMultiplier(Stream<List<Map<String, Object>>> stream) {
			return stream.map(list -> list.stream()
							.reduce((i1, i2) -> (double) i1.get("multiplier") == 0 ? i1 : i2)
							.orElse(list.get(0)));
		}

		/**
		 * Maps and reduces a stream of bills for a component (list<#map>) into a stream of one bill data (map) with the sum of all bill multipliers
		 * 
		 * @param stream the list<#map>> stream which contains all bills for a component - active and inactive
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
