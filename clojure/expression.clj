(defn proto-get [obj key]
      (if (contains? obj key)
        (obj key)
        (proto-get (obj :proto) key)))
(defn proto-call [obj key & args]
      (apply (partial (proto-get obj key) obj) args))

(defn constructor [cons proto]
      (fn [& args] (apply cons {:proto proto} args)))

(defn field [key]
      (fn [obj]
          (proto-get obj key)))

(defn method [key]
      (fn [obj & args]
          (apply proto-call obj key args)))

(declare ZERO)
(declare Const)
(declare Constant)
(defn ConstantCons [this, val]
      (assoc this
             :value val))

(def ConstantProto
  {
   :evaluate (fn [this values] (proto-get this :value))
   :toString (fn [this] (str (format "%.1f" (proto-get this :value))))
   :toStringInfix (fn [this] (str (format "%.1f" (proto-get this :value))))
   :diff     (fn [this nameVar] ZERO)
   })

(def Constant (constructor ConstantCons ConstantProto))
(def Const (constructor ConstantCons ConstantProto))

(def ZERO (Const 0))
(def ONE (Const 1))

(declare Variable)
(def VariableProto
  {
   :evaluate (fn [this values] (values (proto-get this :name)))
   :toString (fn [this] (proto-get this :name))
   :toStringInfix (fn [this] (proto-get this :name))
   :diff     (fn [this nameVar] (if (= (proto-get this :name) nameVar)
                                  ONE
                                  ZERO))
   })

(defn VariableCons [this, name]
      (assoc this
             :name name))

(def Variable (constructor VariableCons VariableProto))

(def arguments (field :arguments))
(def func (field :func))
(def symbolOperation (field :symbolOperation))
(def evaluate (method :evaluate))
(def diff (method :diff))
(def toString (method :toString))
(def toStringInfix (method :toStringInfix))

(def OperationProto
  {
   :evaluate (fn [this values] (apply (func this) (mapv (fn [x] (evaluate x values)) (arguments this))))
   :toString (fn [this] (str "(" (symbolOperation this) " " (clojure.string/join " " (mapv toString (arguments this))) ")"))
   :toStringInfix (fn [this] (if (= 2 (count (arguments this)))
                               (str "(" (toStringInfix (first (arguments this))) " " (symbolOperation this) " " (toStringInfix (second (arguments this))) ")")
                               (str (symbolOperation this) "(" (toStringInfix (first (arguments this))) ")")))
   })

(defn OperationConstructor [this func symbolOperation & args]
      (assoc this
             :arguments args
             :func func
             :symbolOperation symbolOperation))

(def Operation (constructor OperationConstructor OperationProto))



(declare Add)
(defn AddConstructor [this & args] (apply (partial OperationConstructor this + "+") args))
(def AddProto (assoc OperationProto
                     :diff (fn [this nameVar] (apply Add (mapv (fn [x] (diff x nameVar)) (arguments this))))))
(def Add (constructor AddConstructor AddProto))

(declare Subtract)
(defn SubtractConstructor [this & args] (apply (partial OperationConstructor this - "-") args))
(def SubtractProto (assoc OperationProto
                          :diff (fn [this nameVar] (apply Subtract (mapv (fn [x] (diff x nameVar)) (arguments this))))))
(def Subtract (constructor SubtractConstructor SubtractProto))

(declare Multiply)
(defn MultiplyConstructor [this & args] (apply (partial OperationConstructor this * "*") args))
(def MultiplyProto (assoc OperationProto
                          :diff (fn [this nameVar] (reduce (fn [f, s] (Add (Multiply (diff f nameVar) s) (Multiply f (diff s nameVar)))) (arguments this)))))
(def Multiply (constructor MultiplyConstructor MultiplyProto))

(declare Divide)
(defn DivideConstructor [this & args] (apply (partial OperationConstructor this (fn [& x] (reduce (fn [a, b] (/ (double a) (double b))) x)) "/") args))
(def DivideProto (assoc OperationProto
                        :diff (fn [this nameVar] (reduce (fn [f, s] (Divide (Subtract (Multiply (diff f nameVar) s) (Multiply f (diff s nameVar))) (Multiply s s))) (arguments this)))))
(def Divide (constructor DivideConstructor DivideProto))

(declare NatureLog)
(defn NatureLogConstructor [this arg] ((partial OperationConstructor this (fn [arg] (Math/log (Math/abs (double arg)))) "ln") arg))
(def NatureLogProto (assoc OperationProto
                           :diff (fn [this nameVar] (Multiply (Divide ONE (first (arguments this))) (diff (first (arguments this)) nameVar)))))
(def NatureLog (constructor NatureLogConstructor NatureLogProto))

(declare Lg)
(defn LogConstructor [this bs ar] (assoc ((partial OperationConstructor this (fn [bs, ar] (/ (Math/log (Math/abs (double ar))) (Math/log (Math/abs (double bs))))) "lg") bs ar)
                                         :base bs
                                         :arg ar))
(def LogProto (assoc OperationProto
                     :diff (fn [this nameVar] (diff (Divide (NatureLog (proto-get this :arg)) (NatureLog (proto-get this :base))) nameVar))))
(def Lg (constructor LogConstructor LogProto))

(declare Pw)
(defn PowConstructor [this bs ar] (assoc ((partial OperationConstructor this (fn [base, arg] (Math/pow base arg)) "pw") bs ar)
                                         :base bs
                                         :arg ar))
(def PowProto (assoc OperationProto
                     :diff (fn [this nameVar] (Multiply (Pw (proto-get this :base) (proto-get this :arg)) (diff (Multiply (NatureLog (proto-get this :base)) (proto-get this :arg)) nameVar)))))
(def Pw (constructor PowConstructor PowProto))

(declare And)
(defn AndConstructor [this & args] (apply (partial OperationConstructor this (fn [a, b] (Double/longBitsToDouble (bit-and (Double/doubleToLongBits a) (Double/doubleToLongBits b)))) "&") args))
(def AndProto OperationProto)
(def And (constructor AndConstructor AndProto))

(declare Xor)
(defn XorConstructor [this & args] (apply (partial OperationConstructor this (fn [a, b] (Double/longBitsToDouble (bit-xor (Double/doubleToLongBits a) (Double/doubleToLongBits b)))) "^") args))
(def XorProto OperationProto)
(def Xor (constructor XorConstructor XorProto))

(declare Or)
(defn OrConstructor [this & args] (apply (partial OperationConstructor this (fn [a, b] (Double/longBitsToDouble (bit-or (Double/doubleToLongBits a) (Double/doubleToLongBits b)))) "|") args))
(def OrProto OperationProto)
(def Or (constructor OrConstructor OrProto))

(declare Negate)
(defn NegateConstructor [this arg] (OperationConstructor this - "negate" arg))
(def NegateProto (assoc OperationProto
                        :diff (fn [this nameVar] (Negate (diff (first (arguments this)) nameVar)))))
(def Negate (constructor NegateConstructor NegateProto))



(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)

(defn _show [result]
      (if (-valid? result) (str "-> " (pr-str (-value result)) " | " (pr-str (apply str (-tail result))))
                           "!"))
(defn tabulate [parser inputs]
      (run! (fn [input] (printf "    %-10s %s\n" (pr-str input) (_show (parser input)))) inputs))

(defn _empty [value] (partial -return value))

(defn _char [p]
      (fn [[c & cs]]
          (if (and c (p c)) (-return c cs))))

(defn _map [f result]
      (if (-valid? result)
        (-return (f (-value result)) (-tail result))))

(defn _combine [f a b]
      (fn [str]
          (let [ar ((force a) str)]
               (if (-valid? ar)
                 (_map (partial f (-value ar))
                       ((force b) (-tail ar)))))))

(defn _either [a b]
      (fn [str]
          (let [ar ((force a) str)]
               (if (-valid? ar) ar ((force b) str)))))

(defn _parser [p]
      (fn [input]
          (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))

(defn +char [chars] (_char (set chars)))

(defn +map [f parser] (comp (partial _map f) parser))

(def +ignore (partial +map (constantly 'ignore)))

(defn iconj [coll value]
      (if (= value 'ignore) coll (conj coll value)))
(defn +seq [& ps]
      (reduce (partial _combine iconj) (_empty []) ps))

(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))

(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))

(defn +or [p & ps]
      (reduce _either p ps))

(defn +opt [p]
      (+or p (_empty nil)))

(defn +star [p]
      (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))

(defn +plus [p] (+seqf cons p (+star p)))

(defn +str [p] (+map (partial apply str) p))

(def *digit (+char "0123456789"))

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))

(defn *string [[& arrayOfSymbols]] (apply +seqf str (mapv (fn [x] (+char (str x)))  arrayOfSymbols)))

(def *number (+map (comp Constant (comp double read-string)) (+str (+seqf concat (+seqf cons (+opt (+char "-")) (+plus *digit)) (+seqf cons (+opt (+char ".")) (+star *digit))))))
(def *variable (+map (comp Variable str) (+char "xyz")))

(def priorityToArraySymbolOfOperations [["^"] ["|"] ["&"] ["+" "-"] ["*" "/"] ["negate"]])
(def operationsToConstructor {"+" Add "-" Subtract "*" Multiply "/" Divide "negate" Negate "^" Xor "&" And "|" Or})
(def maxPriority (count priorityToArraySymbolOfOperations))

(declare *expression)
(defn *operation [priority] (+map operationsToConstructor (apply +or (mapv *string (priorityToArraySymbolOfOperations (- priority 1))))))
(def *braces (+seqn 1 (+char "(") (delay (*expression 1)) (+char ")")))

(def *argument (+or *variable *number *braces))

(def *unary (+seqf (fn [arrayOfOperations argument] (reduce (fn [expr nowOperation] (nowOperation expr)) argument (reverse arrayOfOperations)))
                   (+star (+seqn 0 *ws (*operation maxPriority) *ws))
                   *argument))

(defn *binary [nowPriority] (+seqf (fn [firstArgument, arrayOfOperations] (reduce (fn [expr, [operation, secondArgument]] (operation expr secondArgument)) firstArgument arrayOfOperations))
                                   (*expression (+ nowPriority 1))
                                   (+star (+seqn 0  *ws (+seq (*operation nowPriority) (*expression (+ nowPriority 1)))))))

(defn *expression [nowPriority]  (+seqn 0 *ws (if (= nowPriority maxPriority)  *unary (*binary nowPriority)) *ws))


(def parseObjectInfix  (_parser (*expression 1)))