# Primality tests

Various algorithms for finding prime numbers.

___

Algorithms featured:

1. NastyPrimes - brutal modular [trial division](https://en.wikipedia.org/wiki/Trial_division). O(n^n).
2. EraSieve - classic Sieve of Eratosthenes.
3. FermatPrime - the 
4. MillerRabin
5. Frobenius
6. Elliptic curve - 

___

Wrote these to make myself learn threading and to demonstrate the sheer exponential improvement of learning some basic algorithmics. 

It's Java, which takes [an ungeneralisable amount](http://benchmarksgame.alioth.debian.org/u32/java.php) off the efficiency, but absolute speed is not the present goal. I guess you could use these for benchmarking different processors, but I wouldn't.

___

### FEATURES TO ADD: 

* Show user relevant pseudocode and formulae.
* Real-time reporting of each's [Big-O](https://en.wikipedia.org/wiki/Big_O_notation) efficiency.
* Visualisation of the first twenty iterations or so. Stepped clickthrough?
* A unified GUI allowing switching between models. 

___


##1. 'Trial division'. 

(Time complexity is n^n.)

In pseudocode:

> candidate = number that might be prime
> i = index. (Initial value 2.)

> 1. If candidate < 2, terminate: candidate is not prime.

> 2. Else while i < (candidate / 2):

>   2.1. If the modulus of candidate % i is 0

>   2.2. Terminate; candidate is not prime.

> 3. Else terminate; candidate is prime.


In Java:

> if (numToCheck <= 1) 
>  return false; 
> else 
>     for (int i = 2; i <= numToCheck/2; i++) 
>     {
>       if (numToCheck % i == 0) 
>          return false;
>     }
> return true;

*************************************************

##2. 'The Sieve of Eratosthenes'. 

(Time complexity is n log of log n.)

In pseudocode:

> Input: an integer n > 1
> 1. Let A be an array of Boolean values, indexed by integers 2 through n, initially all set to true.

> for i = 2, 3, 4, ..., not exceeding sqrt(n):

>  if A[i] is true:

>    for j = i2, i2+i, i2+2i, i2+3i, ..., not exceeding n :

>      A[j] := false
 
> Output: all i such that A[i] is true.


To find all the prime numbers less than or equal to a given integer n by Eratosthenes' method:

    Create a list of consecutive integers from 2 through n: (2, 3, 4, ..., n).
    Initially, let p equal 2, the first prime number.
    Starting from p, enumerate its multiples by counting to n in increments of p, and mark them in the list (these will be 2p, 3p, 4p, ... ; the p itself should not be marked).
    Find the first number greater than p in the list that is not marked. If there was no such number, stop. Otherwise, let p now equal this new number (which is the next prime), and repeat from step 3.

When the algorithm terminates, the numbers remaining not marked in the list are all the primes below n.


In Java:


*************************************************

## 3. The Fermat test 

(Time complexity is .)

In pseudocode:

> 

In Java:


*************************************************

## 4. Miller-Rabin test

(Time complexity is .)

In pseudocode:

> 

In Java:


*************************************************

## 5. Quadratic Frobenius test

(Time complexity is .)

In pseudocode:

> 

In Java:


*************************************************

6. Elliptic curve - 

(Time complexity is .)

In pseudocode:

> 

In Java:
