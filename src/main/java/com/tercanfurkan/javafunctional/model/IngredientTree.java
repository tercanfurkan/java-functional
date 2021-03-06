package com.tercanfurkan.javafunctional.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

public class IngredientTree {
	public static int INITIAL_DEPTH = 1;

	private Ingredient ingredient;
	private Set<IngredientTree> children = new HashSet<>();
	private IngredientTree parent;
	private int depth;

	private IngredientTree(Ingredient ingredient, @Nullable IngredientTree parent) {
		createBillTree(ingredient, parent);
	}

	private void createBillTree(Ingredient bill, @Nullable IngredientTree parent) {
		this.ingredient = bill;
		if (parent != null) {
			this.parent = parent;
			this.parent.addChild(this);
		}
	}

	private boolean isLeaf() {
		return this.children == null || this.children.isEmpty();
	}

	/**
	 * If the date does not match the condition ( not between start and end date), the multiplier must be updated:
	 * <br/> set multiplier active (1) if marked inactive (0)
	 * <br/> set it inactive (0) if marked active (1)
	 *
	 * @param forDate the date to consider the multiplier value
	 * @param multiplier the value to be updated
	 * @return updated multiplier value
	 */
	private double getUpdatedBillMultiplier(LocalDate forDate, double multiplier) {
		// only active or inactive after, or;
		// only active or inactive before
		double updatedMultiplier = multiplier;

		if ((ingredient.startDate != null && ingredient.startDate.isAfter(forDate))
			|| (ingredient.endDate != null && ingredient.endDate.isBefore(forDate)))
			updatedMultiplier = updatedMultiplier != 0 ? 0 : 1;
		return updatedMultiplier;
	}

	public static IngredientTree initBillTreeRoot() {
		// the root tree is useless (no bill, no parent) other than storing all the bills as its children.
		return new IngredientTree(Ingredient.NOOP(), null);
	}

	public Ingredient getIngredient() {
		return this.ingredient;
	}

	public void addChild(IngredientTree child) {
		this. children.add(child);
	}
	
	public int getDepth() {
		return depth;
	}

	public Stream<IngredientTree> streamChildren() {
		// if its useless root, ignore. Otherwise add to stream
		Stream<IngredientTree> streamOfThis = ingredient != null ? Stream.of(this) : Stream.empty();
		if (children == null)
			return streamOfThis;
		else {
			return Stream.concat(streamOfThis, children.stream().flatMap(IngredientTree::streamChildren));
		}
	}
	
	/**
	 * Streams all the children recursively and assigns their depth
	 * 
	 * @param depth to be set for the level
	 * @return stream trees having their depths assigned
	 */
	public Stream<IngredientTree> assignDepth(int depth) {
		this.depth = depth;
		// if its useless root, ignore. Otherwise add to stream
		Stream<IngredientTree> streamOfThis = ingredient != null ? Stream.of(this) : Stream.empty();
		if (children == null)
			return streamOfThis;
		else {
			return Stream.concat(streamOfThis, 
					children.stream().flatMap(child -> child.assignDepth(depth +1)));
		}
	}

	public Stream<Map<String, Object>> streamChildren(int depth, LocalDate forDate) {
		final double multiplier = getUpdatedBillMultiplier(forDate, ingredient.multiplier);
		
		Map<String, Object> map = new HashMap<>();
		map.put("name", ingredient.ingredientCode);
		map.put("depth", depth);
		map.put("multiplier", multiplier);
		
		if (children == null)
			return Stream.of(map);
		else {
			return Stream.concat(Stream.of(map),
					children.stream().flatMap(c -> c.streamChildren(depth + 1, forDate)));
		}
	}

	public Stream<Map<String, Object>> streamLeafs(LocalDate forDate, double ancestorMultiplier) {
		// if not leaf, pass ancestor multiplier times my multiplier
		if (!this.isLeaf())
			return children.stream().flatMap(c -> c.streamLeafs(forDate, ingredient.multiplier * ancestorMultiplier));

		final double multiplier = getUpdatedBillMultiplier(forDate, ingredient.multiplier * ancestorMultiplier);
		
		Map<String, Object> map = new HashMap<>();
		map.put("name", ingredient.ingredientCode);
		map.put("date", forDate);
		map.put("multiplier", multiplier);
		return Stream.of(map);
	}

	public Stream<IngredientTree> getIngredientTreeStream(String ingredientCode) {
		return streamChildren()
				.filter(child -> child.ingredient.ingredientCode.equals(ingredientCode));
	}

	public Stream<IngredientTree> getProductTreeStream(String productCode) {
		return streamChildren()
				.filter(child -> child.ingredient.code.equals(productCode));
	}

	public void add(Ingredient bill) {
		Set<IngredientTree> parentTrees = getIngredientTreeStream(bill.code)
				.collect(Collectors.toSet());
		
		if (parentTrees.isEmpty()) {
			createBillTree(bill, this);
		} else {
			parentTrees.forEach(parentTree -> createBillTree(bill, parentTree));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ingredient == null) ? 0 : ingredient.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IngredientTree other = (IngredientTree) obj;
		if (ingredient == null) {
			return other.ingredient == null;
		} else return ingredient.equals(other.ingredient);
	}

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("depth", depth)
            .add("bill", ingredient)
            .add("parent", parent)
            .toString();
    }
}
