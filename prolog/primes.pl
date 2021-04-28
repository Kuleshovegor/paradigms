min_divisor(Num, K, Div) :- Now is (6 * K - 1), (Now * Now) > Num, !, Div = Num.
min_divisor(Num, K, Div) :- Now is (6 * K - 1), T is Num mod Now, T = 0, !, Div = Now.
min_divisor(Num, K, Div) :- Now is (6 * K + 1), T is Num mod Now, T = 0, !, Div = Now.
min_divisor(Num, K, Div) :- NextK is K + 1, min_divisor(Num, NextK, Div).
min_divisor(Num, Div) :- table_min_divisor(Num, Div), !.
min_divisor(Num, Div) :- T is Num mod 2, T = 0, !, Div = 2, assert(table_min_divisor(Num, 2)).
min_divisor(Num, Div) :- T is Num mod 3, T = 0, !, Div = 3, assert(table_min_divisor(Num, 3)).
min_divisor(Num, Div) :- min_divisor(Num, 1, Div), assert(table_min_divisor(Num, Div)).
prime(N) :- min_divisor(N, N).
composite(N) :- \+ prime(N).
get_prime_divisors(1, Now, Res) :- !, Res = Now.
get_prime_divisors(Num, Now, Res) :- min_divisor(Num, Div), NextNum is Num / Div, append(Now, [Div], NextNow), get_prime_divisors(NextNum, NextNow, Res).
prime_divisors(N, Divisors) :- number(N), !, get_prime_divisors(N, [], Res), Divisors = Res.
get_num_of_divisors([], 1, Pref).
get_num_of_divisors([Head | Tail], Res, Pref) :- \+ Pref > Head, get_num_of_divisors(Tail, PrefRes, Head), Res is PrefRes * Head.
get_num_of_divisors(Divisors, Num) :- get_num_of_divisors(Divisors, Num, 1).
prime_divisors(N, Divisors) :- get_num_of_divisors(Divisors, Num), N = Num.
nth_prime(1, NowNum, Res) :- prime(NowNum), !, Res = NowNum.
nth_prime(1, NowNum, Res) :- !, NextNum is NowNum + 1, nth_prime(1, NextNum, Res).
nth_prime(N, NowNum, Res) :- prime(NowNum), !, N1 is N - 1, NextNum is NowNum + 1, nth_prime(N1, NextNum, Res).
nth_prime(N, NowNum, Res) :- NextNum is NowNum + 1, nth_prime(N, NextNum, Res).
nth_prime(N, P) :- nth_prime(N, 2, P).