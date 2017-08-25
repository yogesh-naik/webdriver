package com.qtpselenium.core.hybrid.testcases;

/*
 * Program to print all palindromes in a given range
 */
public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		countPal(100, 2000);
	}

	static boolean isPalindrome(int n) {
		// Find reverse of n
		int rev = 0;
		for (int i = n; i > 0; i /= 10)
			rev = rev * 10 + i % 10;

		// If n and rev are same, then n is palindrome
		return (n == rev);
	}

	static void countPal(int min, int max) {
		for (int i = min; i <= max; i++)
			if (isPalindrome(i))
				System.out.println(i);
	}
}
