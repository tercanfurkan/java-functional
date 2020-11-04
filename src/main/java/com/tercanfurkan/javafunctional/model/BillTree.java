package com.tercanfurkan.javafunctional.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.MoreObjects;

public class BillTree {
	public static int INITIAL_DEPTH = 1;
	
	private Entry bill;
	private Set<BillTree> children = new HashSet<>();
	private BillTree parent;
	private int depth;

	public BillTree(Entry bill, BillTree parent) {
		this.bill = bill;
		if (parent != null) {
			this.parent = parent;
			this.parent.getChildren().add(this);
		}
	}

	public Entry getBill() {
		return bill;
	}

	public Set<BillTree> getChildren() {
		return children;
	}
	
	public int getDepth() {
		return depth;
	}

	public boolean isLeaf() {
		return this.getChildren() == null || this.getChildren().isEmpty();
	}

	public Stream<BillTree> streamChildren() {
		// if its useless root, ignore. Otherwise add to stream
		Stream<BillTree> streamOfThis = bill != null ? Stream.of(this) : Stream.empty();
		if (children == null)
			return streamOfThis;
		else {
			return Stream.concat(streamOfThis, children.stream().flatMap(BillTree::streamChildren));
		}
	}
	
	/**
	 * Streams all the children recursively and assigns their depth
	 * 
	 * @param depth to be set for the level
	 * @return stream trees having their depths assigned
	 */
	public Stream<BillTree> assignDepth(int depth) {
		this.depth = depth;
		// if its useless root, ignore. Otherwise add to stream
		Stream<BillTree> streamOfThis = bill != null ? Stream.of(this) : Stream.empty();
		if (children == null)
			return streamOfThis;
		else {
			return Stream.concat(streamOfThis, 
					children.stream().flatMap(child -> child.assignDepth(depth +1)));
		}
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
		if ((bill.startDate != null && bill.startDate.isAfter(forDate))
				|| (bill.endDate != null && bill.endDate.isBefore(forDate)))
			multiplier = multiplier != 0 ? 0 : 1;
		return multiplier;
	}

	public Stream<Map<String, Object>> streamChildren(int depth, LocalDate forDate) {
		final double multiplier = getUpdatedBillMultiplier(forDate, bill.multiplier);
		
		Map<String, Object> map = new HashMap<>();
		map.put("name", bill.componentCode);
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
			return children.stream().flatMap(c -> c.streamLeafs(forDate, bill.multiplier * ancestorMultiplier));

		final double multiplier = getUpdatedBillMultiplier(forDate, bill.multiplier * ancestorMultiplier);
		
		Map<String, Object> map = new HashMap<>();
		map.put("name", bill.componentCode);
		map.put("date", forDate);
		map.put("multiplier", multiplier);
		return Stream.of(map);
	}

	public Stream<BillTree> getTreesOfComponent(String componentCode) {
		return streamChildren()
				.filter(child -> child.bill.componentCode.equals(componentCode));
	}

	public Stream<BillTree> getTreesOfProduct(String productCode) {
		return streamChildren()
				.filter(child -> child.bill.code.equals(productCode));
	}

	public void add(Entry bill) {
		Set<BillTree> parentTrees = getTreesOfComponent(bill.code)
				.collect(Collectors.toSet());
		
		if (parentTrees.isEmpty()) {
			new BillTree(bill, this);
		} else {
			parentTrees.forEach(parent -> new BillTree(bill, parent));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bill == null) ? 0 : bill.hashCode());
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
		BillTree other = (BillTree) obj;
		if (bill == null) {
			if (other.bill != null)
				return false;
		} else if (!bill.equals(other.bill))
			return false;
		return true;
	}

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("depth", depth)
            .add("bill", bill)
            .add("parent", parent)
            .toString();
    }
}
