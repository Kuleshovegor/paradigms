node((MapKey, MapVal), DKey, LeftChild, RightChild).

splitDTree(null, DKey, null, null).
splitDTree(node((MapKey, MapVal), DKey, LeftChild, RightChild),
						SplitDKey,
						LTree,
						RTree) :- SplitDKey > MapKey, !, splitDTree(RightChild, SplitDKey, T1, T2), LTree = node((MapKey, MapVal), DKey, LeftChild, T1), RTree = T2.
splitDTree(node((MapKey, MapVal), DKey, LeftChild, RightChild),
						SplitDKey,
						LTree,
						RTree) :- \+ SplitDKey > MapKey, !, splitDTree(LeftChild, SplitDKey, T1, T2), LTree = T1, RTree = node((MapKey, MapVal), DKey, T2, RightChild).

mergeDTree(LeftTree, null, LeftTree) :- !.
mergeDTree(null, RightTree, RightTree) :- !.
mergeDTree(node(LPair, LDKey, LLeftChild, LRightChild),
		   node(RPair, RDKey, RLeftChild, RRightChild),
		   UnionTree) :- LDKey > RDKey, !, mergeDTree(LRightChild, node(RPair, RDKey, RLeftChild, RRightChild), Un),
										UnionTree = node(LPair, LDKey, LLeftChild, Un).
mergeDTree(node(LPair, LDKey, LLeftChild, LRightChild),
			node(RPair, RDKey, RLeftChild, RRightChild),
			UnionTree) :- \+ LDKey > RDKey, !,mergeDTree(node(LPair, LDKey, LLeftChild, LRightChild), RLeftChild, Un),
											UnionTree = node(RPair, RDKey, Un, RRightChild).
findDTree(null, FindMapKey, null).
findDTree(node((FindMapKey, MapVal), _, _, _), FindMapKey, MapVal).
findDTree(node((MapKey, _), _, LeftChild, _), FindMapKey, Result) :- MapKey > FindMapKey, !, findDTree(LeftChild, FindMapKey, Result).
findDTree(node((MapKey, _), _, _, RightChild), FindMapKey, Result) :- \+ MapKey > FindMapKey, !, findDTree(RightChild, FindMapKey, Result).

insertDTree(node((MapKey, MapVal), DKey, LeftChild, RightChild), Tree, NewDTree) :- splitDTree(Tree, MapKey, T1, T2),
                                                                                    MapKey1 is MapKey + 1,
                                                                                    splitDTree(T2, MapKey1, T3, T4),
																					mergeDTree(T1, node((MapKey, MapVal), DKey, LeftChild, RightChild), Tmp),
																					mergeDTree(Tmp, T4, NewDTree).

removeDTree(Tree, MapKey, NewDTree) :- splitDTree(Tree, MapKey, T1, T2),
                                       MapKey1 is MapKey + 1,
                                       splitDTree(T2, MapKey1, T3, T4),
                                       mergeDTree(T1, T4, NewDTree).

map_put (TreeMap, Key, Value, Result):- rand_int(1234, DKey), insertDTree(node((Key, Value), DKey, null, null), TreeMap, Result).
map_get(TreeMap, Key, Value) :- findDTree(TreeMap, Key, Value), \+ Value = null.
map_remove(TreeMap, Key, Result) :- removeDTree(TreeMap, Key, Result).

map_minKey(node((MapKey, _), _, null, _), MapKey).
map_minKey(node((MapKey, _), _, LeftChild, _), Result) :- map_minKey(LeftChild, Result).
map_maxKey(node((MapKey, _), _, _, null), MapKey).
map_maxKey(node((MapKey, _), _, _, RightChild), Result) :- map_maxKey(RightChild, Result).

map_build([], null).
map_build([(MapKey, MapVal)|T], TreeMap) :- map_build(T, TreeMap1), map_put(TreeMap1, MapKey, MapVal, TreeMap).