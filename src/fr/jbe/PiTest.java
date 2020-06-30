package fr.jbe;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.LongStream;

public class PiTest {

	public static void main(String[] args) {
		System.out.println("Simple loop");
		long start = System.currentTimeMillis();
		int prec = 100;
		while (prec <= 10000000) {
			testPiSimpleLoop(prec);
			prec = prec*10;
		}
		System.out.println("time : " + String.valueOf(System.currentTimeMillis() - start) + "s");
		
		System.out.println("\nStream loop");
		start = System.currentTimeMillis();
		for (int decimal = 1; decimal <= 6; decimal++) {
			testStreamPiDecimal(decimal);
		}
		System.out.println("time : " + String.valueOf(System.currentTimeMillis() - start) + "s");

		System.out.println("\nStream parallel loop");
		start = System.currentTimeMillis();
		for (int decimal = 1; decimal <= 6; decimal++) {
			testStreamPiParallel(decimal);
		}
		System.out.println("time : " + String.valueOf(System.currentTimeMillis() - start) + "s");

		System.out.println("\nStream parallel loop pool 4");
		start = System.currentTimeMillis();
		for (int decimal = 1; decimal <= 6; decimal++) {
			testStreamPiParallelPool4(decimal);
		}
		System.out.println("time : " + String.valueOf(System.currentTimeMillis() - start) + "s");
	}
	
	private static void testPiSimpleLoop(int precision) {
		double pi = 0.0;
		double some = 0.0;
		
		for (int index = 0; index <= precision; index++) {
			some += Math.pow(-1, index) / (2*index + 1);
		}
		pi = 4 * some;
		
		System.out.println("Pi value (pecision : "+ precision +") : " + pi);
	}
	
	private static void testStreamPiDecimal(int decimal) {
		double pi = 0.0;
		double some = 0.0;
		long precision = (long) Math.pow(10, decimal+1);
		
		some = LongStream.rangeClosed(0, precision).mapToDouble(k -> {return Math.pow(-1, k) / (2*k + 1);}).sum();
		pi = 4 * some;
		
		System.out.println("Pi value (decimal : "+ decimal +", pecision : "+ precision +") : " + pi);
	}
	
	private static void testStreamPiParallel(int decimal) {
		double pi = 0.0;
		double some = 0.0;
		long precision = (long) Math.pow(10, decimal+1);
		
		some = LongStream.rangeClosed(0, precision).parallel().mapToDouble(k -> {return Math.pow(-1, k) / (2*k + 1);}).sum();
		pi = 4 * some;
		
		System.out.println("Pi value (decimal : "+ decimal +", pecision : "+ precision +") : " + pi);
	}
	
	private static void testStreamPiParallelPool4(int decimal) {
		double pi = 0.0;
		double some = 0.0;
		long precision = (long) Math.pow(10, decimal+1);
		
		ForkJoinPool customThreadPool = new ForkJoinPool(4);
		try {
			some = customThreadPool.submit(() -> LongStream.rangeClosed(0, precision).parallel().mapToDouble(k -> {return Math.pow(-1, k) / (2*k + 1);}).sum()).get();
		} catch (InterruptedException | ExecutionException e) {
			some = 0.0;
		}
		pi = 4 * some;
		
		System.out.println("Parallel pool Pi value (decimal : "+ decimal +", pecision : "+ precision +") : " + pi);
	}

}
