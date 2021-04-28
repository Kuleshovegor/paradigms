(defn oprForArr [operation args] {:pre [(fn? operation) (sequential? args)]
                                  :post [(vector? %)]} (apply (partial mapv operation) args))
(defn sameSize? [& args] (or (every? number? args) (and (every? vector? args) (every? (fn [x] (= (count x) (count (first args)))) args)  (first (oprForArr sameSize? args)))))
(defn vec? [vec] (and (vector? vec) (every? number? vec)))
(defn mat? [mat] (and (vector? mat) (every? vec? mat) (apply sameSize? mat)))
(defn v+ [& vecs] {:pre [(every? vec? vecs) (apply sameSize? vecs)]
                   :post [(vec? %) (sameSize? (first vecs) %)]} (oprForArr + vecs))
(defn v- [& vecs] {:pre [(every? vec? vecs) (apply sameSize? vecs)]
                   :post [(vec? %) (sameSize? (first vecs) %)]}(oprForArr - vecs))
(defn v* [& vecs] {:pre [(every? vec? vecs) (apply sameSize? vecs)]
                   :post [(vec? %) (sameSize? (first vecs) %)]}(oprForArr * vecs))
(defn v*s [vec & nums] {:pre [(vec? vec) (every? number? nums)]
                        :post [(vec? %) (sameSize? vec %)]} (mapv (partial * (apply * nums)) vec))
(defn scalar [& vecs] {:pre [(every? vec? vecs) (= (count (first vecs)) 3) (apply sameSize? vecs)]
                       :post [(number? %)]} (reduce + (reduce v* vecs)))
(defn vect [& vecs] {:pre [(every? vec? vecs) (= (count (first vecs)) 3) (apply sameSize? vecs)]
                     :post [(vec? %) (= (count %) 3)]} (reduce (fn [a, b] [(- (* (nth a 1) (nth b 2)) (* (nth a 2) (nth b 1)))
                                                                           (- (* (nth a 2) (nth b 0)) (* (nth a 0) (nth b 2)))
                                                                           (- (* (nth a 0) (nth b 1)) (* (nth a 1) (nth b 0)))]) vecs))
(defn m+ [& mats] {:pre [(every? mat? mats) (apply sameSize? mats)]
                   :post [(mat? %) (sameSize? (first mats) %)]} (oprForArr v+ mats))
(defn m- [& mats] {:pre [(every? mat? mats) (apply sameSize? mats)]
                   :post [(mat? %) (sameSize? (first mats) %)]} (oprForArr v- mats))
(defn m* [& mats] {:pre [(every? mat? mats) (apply sameSize? mats)]
                   :post [(mat? %) (sameSize? (first mats) %)]} (oprForArr v* mats))
(defn m*s [mat & nums] {:pre [(mat? mat) (every? number? nums)]
                        :post [(mat? %) (sameSize? mat %)]} (mapv (fn [x] (v*s x (apply * nums))) mat))
(defn m*v [mat vec] {:pre [(mat? mat) (vec? vec) (= (count (first mat)) (count vec))]
                     :post [(vec? %) (= (count %) (count mat))]} (mapv (fn [z] (apply + (v* z vec))) mat))
(defn transpose [mat] {:pre [(mat? mat)]
                       :post [(mat? %) (= (count mat) (count (first %))) (= (count (first mat)) (count %))]} (apply mapv vector mat))
(defn m*m [& mats] {:pre [(every? mat? mats)]
                    :post [(mat? %)]} (reduce (fn [mat1, mat2] {:pre [(= (count (first mat1)) (count mat2))]} (transpose(mapv (fn [vect] (m*v mat1 vect)) (transpose mat2)))) mats))
(defn s+ [& vecs] {:pre [(apply sameSize? vecs)]
                   :post [(sameSize? % (first vecs))]} (if (vector? (first vecs)) (oprForArr s+ vecs) (apply + vecs)))
(defn s- [& vecs] {:pre [(apply sameSize? vecs)]
                   :post [(sameSize? % (first vecs))]} (if (vector? (first vecs)) (oprForArr s- vecs) (apply - vecs)))
(defn s* [& vecs] {:pre [(apply sameSize? vecs)]
                   :post [(sameSize? % (first vecs))]} (if (vector? (first vecs)) (oprForArr s* vecs) (apply * vecs)))